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
 * Models a reference to a label in the source file, when used as an operand.
 * 
 * @author cbiffle
 *
 */
public class LabelReference extends Operand {
  private final String targetName;
  private final MemoryType memoryType;

  public LabelReference(final String targetName) {
    this(targetName, MemoryType.LOCAL);
  }
  public LabelReference(String targetName, MemoryType memoryType) {
    this.targetName = targetName;
    this.memoryType = memoryType;
  }
  
  public String getTargetName() {
    return targetName;
  }

  public MemoryType getMemoryType() {
    return memoryType;
  }
  
  public int retrieveAddress(SymbolTable table, int offset) {
    switch(memoryType) {
    case LOCAL:
      return table.localAddressOfSymbol(targetName, offset);
    case SHARED:
      return table.imageAddressOfSymbol(targetName, offset);
    }
    throw new IllegalStateException("A new memory type?  Why?!");
  }
  
  public static enum MemoryType {
    LOCAL,
    SHARED;
  }
}
