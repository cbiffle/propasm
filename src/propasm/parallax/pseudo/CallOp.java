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
import propasm.model.LabelReference;
import propasm.model.Operand;
import propasm.p32.JmpRetOp;

/**
 * CALL - pseudo-op
 * Calls a named subroutine, automatically setting up non-recursive linkage.
 * 
 * This is not magic, but code generation.  The key: the target subroutine must
 * have a labeled RET (or JMPRET) instruction, whose label must be the name of
 * the subroutine plus the suffix "_ret".  For example:
 * 
 *           call high
 *           ...
 * high      or OUTA, mask
 * high_ret  ret
 * 
 * An instruction "call #foo" is exactly equivalent to "jmpret foo_ret, #foo".
 * 
 * @author cbiffle
 *
 */
public class CallOp extends JmpRetOp {

  @Override
  public boolean requiresDest() {
    return false;
  }

  @Override
  public boolean generatesResultByDefault() {
    return false;
  }
  
  @Override
  public Operand defaultDest(Instruction context) {
    Operand source = context.getSource();
    if(source instanceof LabelReference) {
      LabelReference ref = (LabelReference)source;
      return new LabelReference(ref.getTargetName() + "_ret");
    } else {
      throw new IllegalStateException("Call must be used with a label.");
    }
  }

}
