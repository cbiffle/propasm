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

import propasm.model.Instruction;
import propasm.model.NumericOperand;
import propasm.model.Operand;
import propasm.p32.HubOp;

/**
 * Base class for hub pseudo-ops, which provide programmer-friendly abstractions
 * on top of {@link HubOp}.
 * 
 * @author cbiffle
 *
 */
public abstract class AbstractHubPseudoOp extends HubOp {

  /**
   * {@inheritDoc}
   * 
   * Hub pseudo-ops use hardcoded source values to indicate their type, so no
   * user-specified source operand can be provided.
   */
  @Override
  public boolean requiresSource() {
    return false;
  }

  /**
   * Overridden by subclasses to specify the three-bit hub operation they
   * represent.
   * 
   * @return a numeric hub operation.
   */
  protected abstract int getHubOp();
  
  /**
   * {@inheritDoc}
   * 
   * All hub pseudo-ops use a hardcoded source value to indicate their type.
   * This implementation simply returns the value generated by
   * {@link #getHubOp()}.
   */
  @Override
  public Operand defaultSource(Instruction context) {
    return new NumericOperand(getHubOp()); 
  }

  /**
   * {@inheritDoc}
   * 
   * All hub pseudo-ops use an immediate source value to indicate their type,
   * so this always returns {@code true}.
   */
  @Override
  public boolean immediateByDefault() {
    return true;
  }

}
