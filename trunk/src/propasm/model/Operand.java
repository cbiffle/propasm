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
 * Models either a source or dest operand for an instruction.
 * 
 * @author cbiffle
 *
 */
public abstract class Operand {
  
  /**
   * Indicates whether the value referenced by this operand is read-only &mdash;
   * that is, whether it will cause undefined behavior if it appears in the D
   * field of an instruction.
   * 
   * @return {@code true} if the operand is read-only, {@code false} otherwise.
   */
  public boolean isReadOnly() {
    return false;
  }
  
  /**
   * Indicates whether this operand, taken alone, contains a meaningful value to
   * encode in an instruction.  For example, numbers and register references
   * contain values; forward label references do not.
   * 
   * If the operand contains a value, it can be retrieved by calling
   * {@link #getValue()}.
   * 
   * @return  {@code true} if this operand contains a numeric value;
   *          {@code false} otherwise.
   */
  public boolean containsValue() {
    return false;
  }
  
  /**
   * If this operand contains a value (as determined by
   * {@link #containsValue()}), returns that value.  In other cases the
   * behavior is unspecified.
   * 
   * TODO: a comment like that suggests that the class hierarchy is wrong.
   * 
   * @return the operand value, if available.
   */
  public int getValue() {
    throw new UnsupportedOperationException();
  }
}
