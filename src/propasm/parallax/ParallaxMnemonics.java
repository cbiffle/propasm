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
package propasm.parallax;

import propasm.model.InstructionSet;
import propasm.model.Operation;
import propasm.p32.*;
import propasm.parallax.pseudo.*;

/**
 * The set of mnemonics used by Parallax, in the assembler built into their
 * Propeller Tool software.
 * 
 * Includes:<ul>
 * <li>The 57 native Propeller instructions.</li>
 * <li>The eight hub operations as distinct pseudo-ops.</li>
 * <li>12 additional pseudo-ops derived from the native instructions.</li>
 * </ul>
 * 
 * @author cbiffle
 *
 */
public class ParallaxMnemonics implements InstructionSet {

  private static enum Mnemonic {
    // Propeller v1 physical operations 
    ABS(new AbsOp()),
    ABSNEG(new AbsNegOp()),
    ADD(new AddOp()),
    ADDABS(new AddAbsOp()),
    ADDS(new AddSOp()),
    ADDSX(new AddSXOp()),
    ADDX(new AddXOp()),
    AND(new AndOp()),
    ANDN(new AndNOp()),
    CMPSUB(new CmpSubOp()),
    HUBOP(new HubOp()),
    DJNZ(new DjnzOp()),
    JMPRET(new JmpRetOp()),
    MAX(new MaxOp()),
    MAXS(new MaxSOp()),
    MIN(new MinOp()),
    MINS(new MinSOp()),
    MOV(new MovOp()),
    MOVD(new MovDOp()),
    MOVI(new MovIOp()),
    MOVS(new MovSOp()),
    MUXC(new MuxCOp()),
    MUXNC(new MuxNCOp()),
    MUXNZ(new MuxNZOp()),
    MUXZ(new MuxZOp()),
    NEG(new NegOp()),
    NEGC(new NegCOp()),
    NEGNC(new NegNCOp()),
    NEGNZ(new NegNZOp()),
    NEGZ(new NegZOp()),
    OR(new OrOp()),
    RDBYTE(new RdByteOp()),
    RDLONG(new RdLongOp()),
    RDWORD(new RdWordOp()),
    RCL(new RclOp()),
    RCR(new RcrOp()),
    REV(new RevOp()),
    ROL(new RolOp()),
    ROR(new RorOp()),
    SAR(new SarOp()),
    SHL(new ShlOp()),
    SHR(new ShrOp()),
    SUB(new SubOp()),
    SUBABS(new SubAbsOp()),
    SUBS(new SubSOp()),
    SUBSX(new SubSXOp()),
    SUBX(new SubXOp()),
    SUMC(new SumCOp()),
    SUMNC(new SumNCOp()),
    SUMNZ(new SumNZOp()),
    SUMZ(new SumZOp()),
    TJNZ(new TjnzOp()),
    TJZ(new TjzOp()),
    WAITCNT(new WaitCntOp()),
    WAITPEQ(new WaitPeqOp()),
    WAITPNE(new WaitPneOp()),
    WAITVID(new WaitVidOp()),
    XOR(new XorOp()),
    
    // hub pseudo-ops
    CLKSET(new ClkSetOp()),
    COGID(new CogIdOp()),
    COGINIT(new CogInitOp()),
    COGSTOP(new CogStopOp()),
    LOCKNEW(new LockNewOp()),
    LOCKRET(new LockRetOp()),
    LOCKSET(new LockSetOp()),
    LOCKCLR(new LockClrOp()),
    
    // pseudo-ops
    CALL(new CallOp()),
    CMP(new CmpOp()),
    CMPS(new CmpSOp()),
    CMPSX(new CmpSXOp()),
    CMPX(new CmpXOp()),
    JMP(new JmpOp()),
    NOP(new NopOp()),
    RET(new RetOp()),
    TEST(new TestOp()),
    WRBYTE(new WrByteOp()),
    WRWORD(new WrWordOp()),
    WRLONG(new WrLongOp()),
    ;
    
    private Operation op;
    private Mnemonic(Operation op) {
      this.op = op;
    }
    public Operation getOp() {
      return op;
    }
  }
  
  public Operation getOperationForMnemonic(String mnemonic) {
    try {
      Mnemonic m = Mnemonic.valueOf(mnemonic.toUpperCase());
      return m.getOp();
    } catch(IllegalArgumentException e) {
      return null;
    }
  }

}
