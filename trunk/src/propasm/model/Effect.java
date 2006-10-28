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
 * Describes the various flags an instruction may affect.  These effects are
 * specified in the source, and override any default values for the instruction.
 * This can have some weird effects; for example, the {@code test} pseudo-op
 * is simply {@code and} with the {@code NR} effect &mdash; so these two
 * instructions are equivalent:
 *   test x, y wr
 *   and  x, y nr
 * 
 * @author cbiffle
 *
 */
public enum Effect {
  // bit  set?
  WR(0x1, true),
  NR(0x1, false),
  WC(0x2, true),
  NC(0x2, false),
  WZ(0x4, true),
  NZ(0x4, false);
  
  private int mask;
  private boolean set;
  
  private Effect(int bit, boolean set) {
    this.set = set;
    this.mask = bit << 23;
  }
  
  /**
   * Applies the flag for this Effect to the instruction word, returning a
   * modified version.
   * 
   * @param word  original instruction word
   * @return instruction word with this Effect applied.
   */
  public int applyToWord(int word) {
    return (word & ~mask) | (set? mask : 0);
  }
  
}
