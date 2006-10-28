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
package propasm.parallax.pseudo;

import propasm.p32.JmpRetOp;

/**
 * JMP - pseudo-op
 * Unconditionally jumps to a specified address.
 * 
 * JMP is simply JMPRET with an empty D field and the NR effect set, to prevent
 * it from setting a return link.
 * 
 * @author cbiffle
 *
 */
public class JmpOp extends JmpRetOp {

  @Override
  public boolean requiresDest() {
    return false;
  }

  @Override
  public boolean generatesResultByDefault() {
    return false;
  }

}
