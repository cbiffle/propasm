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
 * Models a literal number or resolved label reference, when used as an operand.
 * 
 * @author cbiffle
 *
 */
public class NumericOperand extends Operand {
  private final int value;
  public NumericOperand(int value) {
/*    if(value < -256 || value > 511) {
      throw new IllegalArgumentException("Value out of range: " + value);
    }*/
    this.value = value;
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
