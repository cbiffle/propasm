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

import propasm.p32.RdByteOp;

/**
 * WRBYTE - pseudo-op
 * byte[S] <- D
 * 
 * WRBYTE is RDBYTE with the NR effect.
 * 
 * @author cbiffle
 *
 */
public class WrByteOp extends RdByteOp {

  @Override
  public boolean generatesResultByDefault() {
    return false;
  }

}
