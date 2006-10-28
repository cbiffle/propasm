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
 * Models a type of operation, used by instructions in the source file.
 * 
 * This class does not distinguish between physical operations (implemented by
 * the hardware) and pseudo-ops (implemented by code generation in the
 * assembler).
 * 
 * @author cbiffle
 *
 */
public abstract class Operation {
  /**
   * Checks if uses of this operation require a programmer-specified destination
   * operand.  This is used both by parsers and when generating code.
   * 
   * Operations that do not require the programmer to specify a destination
   * may still generate one; see {@link #defaultDest(Instruction)}.
   * 
   * @return {@code true} if the programmer must specify a destination operand;
   *         {@code false} otherwise.
   */
  public boolean requiresDest() {
    return false;
  }

  /**
   * Checks if uses of this operation require a programmer-specified source
   * operand.  This is used both by parsers and when generating code.
   * 
   * Operations that do not require the programmer to specify a source
   * may still generate one; see {@link #defaultSource(Instruction)}.
   * 
   * @return {@code true} if the programmer must specify a source operand;
   *         {@code false} otherwise.
   */
  public boolean requiresSource() {
    return false;
  }
  
  /**
   * Indicates whether this operation, by default, generates a result as a
   * side-effect.  This can be overridden with the {@link Effect#NR} and
   * {@link Effect#WR} effects.
   * 
   * @return {@code true} if the operation generates a result by default,
   *         {@code false} if no result is generated unless requested.
   */
  public boolean generatesResultByDefault() {
    return true;
  }
  
  /**
   * Generates a non-programmer-specified source operand, given the instruction
   * where the operand is needed.  This method is only invoked if the operation
   * returns {@code false} from {@link #requiresSource()}.
   * 
   * @param context  instruction where the operand is needed.
   * @return a generated source operand for the instruction.
   */
  public Operand defaultSource(Instruction context) {
    return null;
  }

  /**
   * Generates a non-programmer-specified destination operand, given the
   * instruction where the operand is needed.  This method is only invoked if
   * the operation returns {@code false} from {@link #requiresDest()}.
   * 
   * @param context  instruction where the operand is needed.
   * @return a generated destination operand for the instruction.
   */
  public Operand defaultDest(Instruction context) {
    return null;
  }
  
  /**
   * Returns the default {@link Predicate} to use if the programmer does not
   * specify one.  This method should be overridden if the instruction needs an
   * unusual default predicate (for a no-op, for example).
   * 
   * @return the default predicate for this operation.
   */
  public Predicate defaultPredicate() {
    return Predicate.IF_ALWAYS;
  }
  
  /**
   * Returns the opcode for this operation, a six-bit integer.  Opcodes identify
   * the physical operation used to implement an operation, and thus may not
   * be unique: pseudo-ops always share their opcode with a hardware op.
   * 
   * @return the operation's opcode in the lower six bits.
   */
  public abstract int getOpcode();
  
  /**
   * Indicates whether uses of this operation should have their immediate flag
   * set.  This is only meaningful (and only used) to specify that a generated
   * source operand is immediate when {@link #requiresSource()} returns
   * {@code false}.
   * 
   * @return {@code true} if the source operand is immediate by default,
   *         {@code false} otherwise.
   */
  public boolean immediateByDefault() {
    return false;
  }

  /**
   * Generates a template word for instructions using this operation.  Callers
   * can generate the full instruction word by applying predicates, effects,
   * and operands to the result of this method.
   * 
   * @return a template instruction word.
   */
  public final int getTemplate() {
    int template = 0;
    template |= getOpcode() << (32 - 6);
    
    if(generatesResultByDefault()) template |= (1 << 23);
    if(immediateByDefault()) template |= (1 << 22);
    
    Predicate p = defaultPredicate();
    template = p.applyToWord(template);
    
    return template;
  }
  
}
