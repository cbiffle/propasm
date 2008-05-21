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
package propasm.util;

/**
 * An in-memory, dynamically-resized byte array that exposes a file-like
 * random-access interface.  Used during code generation.
 * 
 * (Since the P8X32's maximum object size is on the order of 32KiB, buffering it
 * in memory during assembly is not a big deal.)
 * 
 * @author cbiffle
 *
 */
public class ByteBuffer {
  /** Backing byte array. */
  private byte[] buffer = new byte[1024];
  /** Number of valid bytes in the byte array. */
  private int size = 0;
  /** Current read/write position in the byte array. */
  private int position = 0;
  
  /**
   * Writes or overwrites a byte at the current position, advancing the
   * position.
   * 
   * @param b  byte to write.
   */
  public void write(int b) {
    buffer[position++] = (byte)b;
    if(position > size) size = position;
    if(size >= buffer.length) {
      byte[] newBuffer = new byte[buffer.length + 1024];
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
      buffer = newBuffer;
    }
  }
  
  /**
   * Reads a byte at the current position, advancing the position.
   * 
   * @return  byte read
   */
  public int read() {
    return buffer[position++];
  }
  /**
   * Changes the current position.  Valid positions start at 0 and continue to
   * {@link #size()} - 1; this method cannot be used to seek past
   * {@link #size()}.
   * 
   * @param newPosition  new read/write position.
   */
  public void seek(int newPosition) {
    if(newPosition > size) throw new IllegalArgumentException("Seeked off end");
    this.position = newPosition;
  }
  
  /**
   * Copies {@link #size()} bytes out of this buffer as a byte array.
   * 
   * @return copy of contents.
   */
  public byte[] toByteArray() {
    byte[] out = new byte[size];
    System.arraycopy(buffer, 0, out, 0, size);
    return out;
  }
  
  /**
   * Returns the number of bytes that have been written to this buffer.  This is
   * the size that bounds calls to {@link #seek(int)} and that determines the
   * output of {@link #toByteArray()}.  (The internal array size may be larger
   * than this, but that's none of your concern.)
   * 
   * @return number of bytes in the buffer.
   */
  public int size() {
    return size;
  }
  
  /**
   * @return the current read/write position.
   */
  public int position() {
    return position;
  }
}
