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
 * Describes the various possible settings for the P8X32's clock.
 * 
 * @author cbiffle
 *
 */
public enum ClockMode {
  XINPUT(0x22, true),
  XTAL1( 0x2A, true),
  XTAL2( 0x32, true),
  XTAL3( 0x3A, true),
  RCFAST(0x00, false) {
    @Override public int derive(@SuppressWarnings("unused") int freq) {
      return 12000000;
    }
  },
  RCSLOW(0x01, false) {
    @Override public int derive(@SuppressWarnings("unused") int freq) {
      return 20000;
    }
  };
  
  private final byte base;
  private final boolean external;
 
  private ClockMode(int base, boolean external) {
    this.base = (byte)base;
    this.external = external;
  }
  
  public byte getValue(PllMode pll) {
    if(!external && pll.isEnabled()) {
      throw new IllegalArgumentException(this + " cannot be used with PLL");
    }
    byte mode = base;
    if(pll.isEnabled()) {
      mode |= 0x40;
      mode += pll.getValue();
    }
    return mode;
  }
  
  public int derive(int inputFrequency) {
    return inputFrequency;
  }
}
