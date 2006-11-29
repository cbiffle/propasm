// Copyright (C) 2006 Cliff L. Biffle.
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
package propasm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propasm.util.ByteBuffer;

/**
 * Generates machine code, based on information it receives from the parser.
 * 
 * This class distinguishes between the actual position in the output and the
 * <em>runtime address</em>.  The runtime address is the address that the
 * current instruction <em>will occupy</em> once it's loaded into a Cog.
 * 
 * In terms of Parallax directives, it's the runtime address that is affected by
 * ORG.
 * 
 * Runtime addresses are manipulated in terms of bytes internally, but the
 * external interface is in units of words (to match Cog internal addressing).
 * 
 * Instances of ProgramBuilder are not reusable.  Object creation is cheap;
 * exploit it.
 * 
 * @author cbiffle
 *
 */
public class ProgramBuilder implements SymbolTable {
  private ByteBuffer output = new ByteBuffer();
  /** Number of bytes reserved past the end of the image by directives. */
  private int pendingReservation = 0;
  /** Current runtime address, as seen from the code -- IN BYTES. */
  private int runtimeAddress = 0;
  
  // Machine Settings
  private PllMode initialPllMode = PllMode.X16;
  private ClockMode initialClockMode = ClockMode.XTAL1;
  /** Input clock frequency.  This may be computed from clockSpeed. */
  private int inputFrequency = 5000000;
  /** Initial clock speed when this code is loaded.  This may be computed. */
  private int initialClockFrequency = 0;
  
  /**
   * Labels that have been encountered and defined in the source.
   * Map from name to address info.
   */
  private final Map<String, AddressRecord> definedLabels = 
    new HashMap<String, AddressRecord>();
  
  /**
   * References to labels that have been referenced, but not yet defined.
   * Map from label name to list of reference locations.
   */
  private final Map<String, List<UnboundLabel>> pendingReferences =
    new HashMap<String, List<UnboundLabel>>();
  
  /** Canned loader preamble. */
  private static final byte[] PREAMBLE = {
    0, 0, 0, 0, // clock speed
    0, // clk
    0, //checksum
    0x10, 0x00, // spin offset
    0, 0, // data base
    0, 0, // stack base
    0x18, 0x00,
    0, 0, // stack pointer
    0, 0, // spin object size
    0x02, 0x00, // spin data offset
    0x08, 0x00, 0x00, 0x00, // stub method layout
    0x35, // push0
    0x37, 0x04, // push 2^5
    0x35, // push0
    0x2C, // coginit
    0x00, 0x00, 0x00 // padding for alignment
  };
  
  public ProgramBuilder() {
    for(byte b : PREAMBLE) {
      write(b);
    }
    runtimeAddress = 0;
  }
  
  public int getInputFrequency() {
    return inputFrequency;
  }

  public void setInputFrequency(int inputFrequency) {
    this.inputFrequency = inputFrequency;
  }
  
  public void setInitialClockMode(ClockMode mode, PllMode pllMode) {
    initialClockMode = mode;
    initialPllMode = pllMode;
  }
  
  public int getInitialClockFrequency() {
    if(initialClockFrequency != 0) {
      return initialClockFrequency;
    } else {
      return initialPllMode.multiply(initialClockMode.derive(inputFrequency));
    }
  }

  /**
   * Explicitly resets the runtime address.  This does not add or subtract
   * bytes from the output, but purely changes the code's perspective of its
   * current address.
   * 
   * @param addr  new runtime address, in words.
   */
  public void setRuntimeAddress(int addr) {
    if(addr < 0 || addr > 512) {
      throw new IllegalArgumentException("Address out of range");
    }
    runtimeAddress = addr * 4;
  }
  
  /**
   * Returns the current runtime address.  Note that this will typically be
   * immediately following the most recently generated instruction.
   * 
   * @return the current runtime address, in words.
   */
  public int getRuntimeAddress() {
    return (runtimeAddress >> 2) + (pendingReservation >> 2);
  }
  
  /**
   * Returns the current address within the image, including any preamble and
   * ignoring runtime address directives.
   * 
   * Image addresses are in bytes.
   * 
   * @return the address within the image.
   */
  public int getImageAddress() {
    return output.position() + pendingReservation;
  }
  
  /**
   * Reserves some number of bytes for data.  This does not actually add any
   * space to the generated image, unless instructions or data follow it.
   * 
   * @param count  number of bytes to reserve.
   */
  public void reserveBytes(int count) {
    pendingReservation += count;
  }
  
  /**
   * Adds a raw data byte at the current image position.
   * 
   * @param value  byte to add.
   */
  public void addByte(byte value) {
    flushReservation();
    write(value);
  }
  
  /**
   * Adds a raw data shortword (16 bits) at the current image position.
   * 
   * @param value  shortword to add.
   */
  public void addWord(int value) {
    flushReservation();
    writeWord(value);
  }
  
  /**
   * Adds a raw data longword (32 bits) at the current image position.
   * 
   * @param value  longword to add.
   */
  public void addLong(int value) {
    flushReservation();
    writeLong(value);
  }
  
  /**
   * Adds an instruction at the current image position.
   * 
   * @param instr instruction to add.
   */
  public void addInstruction(Instruction instr) {
    int word = instr.binaryRepresentation();
    writeLong(word);
  }
  
  
  /**
   * Defines a label at the current runtime address.  Used to process labelled
   * instructions in the source.
   * 
   * @param label  name of label
   * @throw IllegalStateException  if the label has already been defined.
   */
  public void defineLabel(String label) {
    if(definedLabels.containsKey(label)) {
      throw new IllegalStateException("Duplicate definition of label " + label);
    }
    AddressRecord record = new AddressRecord(getRuntimeAddress(),
                                             getImageAddress());
    definedLabels.put(label, record);
  }

  /**
   * Resolves the value for the specified label.
   * 
   * ...sometimes.
   * 
   * If the label is not yet specified, this will fake a resolution (returning
   * zero) and record its position in the stream.  Later, when {@link #finish()}
   * is called, it rewrites the operands to match the label positions.
   */
  public int localAddressOfSymbol(String symbol, int offset) {
    if(definedLabels.containsKey(symbol)) {
      return definedLabels.get(symbol).getLocalAddress();
    } else {
      UnboundLabel procrastination = new UnboundLabel(getImageAddress(), offset);
      List<UnboundLabel> pending = pendingReferences.get(symbol);
      if(pending == null) {
        pending = new ArrayList<UnboundLabel>();
        pendingReferences.put(symbol, pending);
      }
      pending.add(procrastination);
      return 0;
    }
  }

  public int imageAddressOfSymbol(String symbol, int offset) {
    if(definedLabels.containsKey(symbol)) {
      return definedLabels.get(symbol).getImageAddress();
    } else {
      UnboundLabel procrastination = new UnboundLabel(getImageAddress(), offset, true);
      List<UnboundLabel> pending = pendingReferences.get(symbol);
      if(pending == null) {
        pending = new ArrayList<UnboundLabel>();
        pendingReferences.put(symbol, pending);
      }
      pending.add(procrastination);
      return 0;
    }
  }

  /**
   * Finishes encoding, resolves all pending references, and updates the
   * preamble.  Returns the assembled binary in a byte array.
   * 
   * @return the bytes of the assembled binary.
   * @throws LogicException if a label is left unresolved.
   */
  public byte[] finish() throws LogicException {
    StringBuilder msg = new StringBuilder();
    
    for(String label : pendingReferences.keySet()) {
      if(!definedLabels.containsKey(label)) {
        msg.append("  ");
        msg.append(label);
        msg.append("\n");
      }
    }
    
    if(msg.length() > 0) {
      msg.insert(0, "Unresolved labels:\n");
      throw new LogicException(msg.toString(), 0, 0);
    }
    for(String label : pendingReferences.keySet()) {
      AddressRecord record = definedLabels.get(label);
      for(UnboundLabel unbound : pendingReferences.get(label)) {
        output.seek(unbound.getInstructionAddress());
        int word = 0;
        for(int i = 0; i < 4; i++) {
          word = (word >>> 8) | (output.read() << 24);
        }
        
        int address = unbound.extractDesiredAddress(record);
        word |= address << unbound.getShift();
        output.seek(unbound.getInstructionAddress());
        for(int i = 0; i < 4; i++) {
          output.write(word & 0xFF);
          word >>>= 8;
        }
      }
      
    }
    
    fillInPreamble();
    
    byte[] bytes = output.toByteArray();
    bytes[5] = computeChecksum(bytes);

    return bytes;
  }
  
  private void write(int value) {
    output.write(value);
    runtimeAddress++;
  }

  private void writeLong(int value) {
    ensureLongAlignment();
    write(value & 0xFF);
    write((value >> 8) & 0xFF);
    write((value >> 16) & 0xFF);
    write((value >> 24) & 0xFF);
  }

  private void writeWord(int value) {
    ensureWordAlignment();
    write(value & 0xFF);
    write((value >> 8) & 0xFF);
  }

  public void ensureWordAlignment() {
    align(2);
  }
  public void ensureLongAlignment() {
    align(4);
  }
  public void align(int unit) {
    int offset = unit - (output.position() & (unit - 1));
    if(offset == unit) return;
    for(int i = 0; i < offset; i++) write(0);
  }
  private byte computeChecksum(byte[] bytes) {
    int sum = 0;
    for(byte b : bytes) {
      sum += b & 0xFF;
    }
    return (byte)(0x14 - sum);
  }

  private void fillInPreamble() {
    output.seek(0);
    writeLong(getInitialClockFrequency());
    write(initialClockMode.getValue(initialPllMode));
    output.seek(0x8);
    writeWord(output.size());
    writeWord(output.size() + 8);
    output.seek(0xE);
    writeWord(output.size() + 12);
    writeWord(output.size() - 16);
  }
  
  private void flushReservation() {
    for( ; pendingReservation > 0; pendingReservation--) {
      write(0x00);
    }
  }
  
  private static class AddressRecord {
    private final int localAddress;
    private final int imageAddress;
    public AddressRecord(final int localAddress, final int imageAddress) {
      this.localAddress = localAddress;
      this.imageAddress = imageAddress;
    }
    public int getImageAddress() {
      return imageAddress;
    }
    public int getLocalAddress() {
      return localAddress;
    }
    @Override public String toString() {
      StringBuffer buf = new StringBuffer("image:");
      buf.append(Integer.toHexString(imageAddress));
      buf.append(" local:");
      buf.append(Integer.toHexString(localAddress));
      return buf.toString();
    }
  }
  
  private static class UnboundLabel {
    private final int instructionAddress;
    private final int shift;
    private boolean needImageAddress = false;
    
    public UnboundLabel(final int instructionAddress, final int shift) {
      this(instructionAddress, shift, false);
    }
    public UnboundLabel(int instructionAddress, int shift, boolean image) {
      this.instructionAddress = instructionAddress;
      this.shift = shift;
      this.needImageAddress = image;
    }
    public int extractDesiredAddress(AddressRecord record) {
      if(needImageAddress) {
        return record.getImageAddress();
      } else {
        return record.getLocalAddress();
      }
    }
    public int getInstructionAddress() {
      return instructionAddress;
    }
    public int getShift() {
      return shift;
    }
    
  }
}
