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
 * Models a set of standard, named memory locations within the Cog, such as the
 * standard Parallax registers $1F0-$1FF.
 * 
 * @author cbiffle
 *
 */
public interface RegisterSet {
  /**
   * Generates an operand representing a register, given its name.  If the name
   * does not belong to a register, returns {@code null}.
   * 
   * @param register  name of register
   * @return an operand for accessing the register, or {@code null}
   */
  Operand getRegister(String register);
}
