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

/**
 * Models a 9-bit literal number or resolved label reference, when used as an
 * operand.
 * 
 * From the Java perspective, this class accepts the union of all unsigned 9-bit
 * integers, and all signed two's-complement 9-bit integers.  Thus, any value
 * between 511 and -256 will work. 
 * 
 * @author cbiffle
 *
 */
public class NumericOperand extends Operand {
  private final int value;
  public NumericOperand(int value) {
    this(value, 9);
  }
  public NumericOperand(int value, int bits) {
    if(value > (-1 >>> (32 - (bits - 1)))) {
      // Treat as unsigned
      if(value > (-1 >>> (32 - bits))) {
        throw new IllegalArgumentException("Unsigned value out of range for " +
                        "9-bit field: " + value);
      }
    } else {
      // Treat as signed
      // Drop all but relevant bits, and sign-extend back to 32.
      int effectiveValue = (value << (32 - bits)) >> (32 - bits);
      // Did we lose meaning?
      if(value != effectiveValue) { 
        throw new IllegalArgumentException("Value out of range for " + bits + 
                                           "-bit field: " + value);
      }
    }
    this.value = value & (-1 >>> (32 - bits));
  }
  
  @Override
  public boolean containsValue() {
    return true;
  }
  
  @Override
  public int getValue() {
    return value;
  }
  
}
