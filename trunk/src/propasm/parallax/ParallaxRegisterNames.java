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

import propasm.model.NumericOperand;
import propasm.model.Operand;
import propasm.model.ReadOnlyRegister;
import propasm.model.RegisterSet;

/**
 * The set of Cog register names used by Parallax, in the assembler built into
 * their Propeller Tool software.
 * 
 * @author cbiffle
 *
 */
public class ParallaxRegisterNames implements RegisterSet {

  private static enum Register {
    PAR(0x1F0, true),
    CNT(0x1F1, true),
    INA(0x1F2, true),
    INB(0x1F3, true),
    OUTA(0x1F4),
    OUTB(0x1F5),
    DIRA(0x1F6),
    DIRB(0x1F7),
    CTRA(0x1F8),
    CTRB(0x1F9),
    FRQA(0x1FA),
    FRQB(0x1FB),
    PHSA(0x1FC),
    PHSB(0x1FD),
    VCFG(0x1FE),
    VSCL(0x1FF);
    
    private int address;
    private boolean readOnly;
    private Register(int address) {
      this(address, false);
    }
    private Register(int address, boolean readOnly) {
      this.address = address;
      this.readOnly = readOnly;
    }
    public int getAddress() {
      return address;
    }
    public boolean isReadOnly() {
      return readOnly;
    }
  }
  
  public Operand getRegister(String register) {
    try {
      
      Register reg = Register.valueOf(register.toUpperCase());
      
      if(reg.isReadOnly()) {
        return new ReadOnlyRegister(reg.getAddress());
      } else {
        return new NumericOperand(reg.getAddress());
      }
      
    } catch(IllegalArgumentException e) {
      return null;
    }
  }

}
