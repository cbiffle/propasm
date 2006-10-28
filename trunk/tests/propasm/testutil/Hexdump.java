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
package propasm.testutil;

/**
 * @author cbiffle
 *
 */
public class Hexdump {
  private Hexdump() {}
  
  public static void dump(byte[] data) {
    for(int lineOffset = 0; lineOffset < data.length; lineOffset += 16) {
      System.out.printf("%04X", lineOffset);
      for(int offset = lineOffset; offset < lineOffset + 16; offset++) {
        if((offset & 0x3) == 0) System.out.print(" ");
        if(offset < data.length) {
          System.out.printf("%02X ", data[offset]);
        } else {
          System.out.print("   ");
        }
      }
      for(int offset = lineOffset; offset < lineOffset + 16; offset++) {
        if((offset & 0x7) == 0) System.out.print(" ");
        if(offset < data.length) {
          if(data[offset] >= 0x20 && data[offset] <= 0x7E) {
            System.out.printf("%c", (char)data[offset]);
          } else {
            System.out.print(".");
          }
        } else {
          System.out.print(" ");
        }
      }
      System.out.println();
    }
  }
}
