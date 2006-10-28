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
 * Describes the instruction set of a target chip, in terms of
 * programmer-visible mnemonics.  Implementations of InstructionSet handle
 * physical operations and pseudo-ops, but not directives or data.
 * 
 * @author cbiffle
 *
 */
public interface InstructionSet {
  /**
   * Returns the Operation object for the given mnemonic.  Individual
   * implementations may or may not be case-sensitive; their associated parser
   * should behave appropriately.
   * 
   * If no operation is available for {@code mnemonic}, returns {@code null}.
   * 
   * @param mnemonic  mnemonic to look up.
   * @return  operation backing the mnemonic, or {@code null} if the mnemonic
   *          is unknown.
   */
  Operation getOperationForMnemonic(String mnemonic);
}
