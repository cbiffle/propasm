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
 * Models the Propeller's various <em>instruction predicates</em>, which
 * allow instructions to be conditionally executed based on flag values.
 * 
 * This class currently encodes the Parallax predicate syntax, and is likely to
 * change to allow other syntaxes.
 * 
 * @author cbiffle
 *
 */
public enum Predicate {
  IF_NEVER(0x0),
  IF_A(0x1),
  IF_NC_AND_NZ(0x1),
  IF_NZ_AND_NC(0x1),
  IF_NC_AND_Z(0x2),
  IF_Z_AND_NC(0x2),
  IF_AE(0x3),
  IF_NC(0x3),
  IF_C_AND_NZ(0x4),
  IF_NZ_AND_C(0x4),
  IF_NE(0x5),
  IF_NZ(0x5),
  IF_C_NE_Z(0x6),
  IF_Z_NE_C (0x6),
  IF_NC_OR_NZ(0x7),
  IF_NZ_OR_NC(0x7),
  IF_C_AND_Z(0x8),
  IF_Z_AND_C(0x8),
  IF_C_EQ_Z(0x9),
  IF_Z_EQ_C (0x9),
  IF_E(0xA),
  IF_Z(0xA),
  IF_NC_OR_Z(0xB),
  IF_Z_OR_NC(0xB),
  IF_B(0xC),
  IF_C(0xC),
  IF_C_OR_NZ(0xD),
  IF_NZ_OR_C(0xD),
  IF_BE(0xE),
  IF_C_OR_Z(0xE),
  IF_Z_OR_C(0xE),
  IF_ALWAYS(0xF);
  
  private int flags;
  private Predicate(int flags) {
    this.flags = flags;
  }
  
  /**
   * Creates a new instruction word by applying this predicate to the provided
   * base word.
   * 
   * @param word  original instruction word.
   * @return version of {@code word} using this predicate.
   */
  public int applyToWord(int word) {
    word &= ~(0xF << 18);
    word |= flags << 18;
    return word;
  }
}
