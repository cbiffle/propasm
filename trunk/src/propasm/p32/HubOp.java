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
package propasm.p32;


/**
 * HUBOP - 03
 * Sends a command to the hub.  Requires hub sync, which may take up to 15
 * clocks.
 * 
 * Command in low-order three bits of S, argument/result in D.
 * Commands:
 * 000  CLKSET
 * 001  COGID
 * 010  COGINIT
 * 011  COGSTOP
 * 100  LOCKNEW
 * 101  LOCKRET
 * 110  LOCKSET
 * 111  LOCKCLR
 * 
 * @author cbiffle
 *
 */
public class HubOp extends AbstractBinaryOp {

  @Override
  public int getOpcode() {
    return 0x03;
  }

  @Override
  public boolean generatesResultByDefault() {
    return false;
  }
  

}
