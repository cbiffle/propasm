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
 * Generic exception class for textual/syntactic errors in the input file --
 * anything that can be localized to a particular line/column.
 * 
 * @author cbiffle
 *
 */
public abstract class AssemblyInputException extends Exception {
  private final int line, col;
  
  public AssemblyInputException(String msg, int line, int col) {
    super(msg);
    this.line = line;
    this.col = col;
  }
  public int getColumn() {
    return col;
  }
  public int getLine() {
    return line;
  }
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("Line ");
    buf.append(getLine());
    buf.append(':');
    buf.append(getColumn());
    buf.append(": ");
    buf.append(getMessage());
    return buf.toString();
  }
}
