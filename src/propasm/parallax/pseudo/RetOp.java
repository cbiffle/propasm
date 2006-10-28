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

/**
 * RET - pseudo-op
 * Returns from a subroutine (previously invoked with CALL).  See the notes on
 * {@link CallOp} for more details.
 * 
 * RET is a JMP operation with generated garbage D and S fields.  It expects to
 * be updated by a JMPRET (or CALL) instruction before use.
 * 
 * @author cbiffle
 *
 */
public class RetOp extends JmpOp {

  @Override
  public boolean requiresSource() {
    return false;
  }

  @Override
  public boolean immediateByDefault() {
    return true;
  }

}
