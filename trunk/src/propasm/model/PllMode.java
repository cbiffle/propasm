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
 * @author cbiffle
 *
 */
public enum PllMode {
  PLL_DISABLED {
    @Override public boolean isEnabled() {
      return false;
    }
  },
  X1(1, 1),
  X2(2, 2),
  X4(3, 4),
  X8(4, 8),
  X16(5, 16);
  
  private final int value;
  private final int multiplier;
  
  private PllMode() {
    this(0, 1);
  }
  private PllMode(int value, int multiplier) {
    this.value = value;
    this.multiplier = multiplier;
  }
  
  public int getValue() {
    return value;
  }
  
  public int getMultiplier() {
    return multiplier;
  }
  
  public boolean isEnabled() {
    return true;
  }
  
  public int multiply(int clockFrequency) {
    return clockFrequency * multiplier;
  }
}
