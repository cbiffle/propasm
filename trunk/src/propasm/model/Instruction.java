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

import java.util.EnumSet;
import java.util.Set;

/**
 * Models a single instruction in the source file.  An instruction is a
 * combination of:<ul>
 * <li>An optional {@link Predicate};</li>
 * <li>An {@link Operation};</li>
 * <li>Source and Dest {@link Operand}s, which may or may not be present
 *     (depending on the operation);</li>
 * <li>A flag indicating whether the Source Operand is an immediate;</ul>
 * <li>Zero or more {@link Effect}s.</li>
 * </ul>
 * 
 * @author cbiffle
 *
 */
public class Instruction {
  private final SymbolTable table;
  
  private Operation operation;
  private Predicate predicate;
  private Operand dest;
  private Operand source;
  private boolean immediateSource = false;
  private final Set<Effect> effects = EnumSet.noneOf(Effect.class);
  
  public Instruction(SymbolTable table) {
    this.table = table;
  }
  
  public Operation getOperation() {
    return operation;
  }
  public void setOperation(Operation operation) {
    if(operation == null) {
      throw new IllegalArgumentException("Operation cannot be null.");
    }
    this.operation = operation;
  }

  public Predicate getPredicate() {
    return predicate;
  }
  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }
  
  public Operand getSource() {
    return source;
  }
  public void setSource(Operand operand) {
    if(operand == null) throw new IllegalArgumentException("Null source");
    source = operand;
  }

  public void setDest(Operand operand) {
    if(operand == null) throw new IllegalArgumentException("Null dest");
    if(operand.isReadOnly()) {
      throw new IllegalArgumentException("Read-only operand cannot be used " +
                "in D field.");
    }
    dest = operand;
  }
  
  public void setImmediateSource(boolean immediate) {
    immediateSource = immediate;
  }

  public void addEffect(Effect effect) {
    effects.add(effect);
  }
  
  /**
   * Cooks up a word-sized binary representation of this instruction by
   * combining the operation with the operands, effects, and predicate.
   * 
   * Uses the symbol table provided in the constructor to convert label
   * references to binary form.
   * 
   * @return a four-byte binary representation.
   */
  public int binaryRepresentation() {
    int word = operation.getTemplate();
    
    for(Effect effect : effects) {
      word = effect.applyToWord(word);
    }
    if(predicate != null) {
      word = predicate.applyToWord(word);
    }
    if(immediateSource) {
      word |= (1 << 22);
    }
    Operand d = dest;
    if(d == null) d = operation.defaultDest(this);
    Operand s = source;
    if(s == null) s = operation.defaultSource(this);
    word = applyOperand(word, d, 9);
    word = applyOperand(word, s, 0);
    
    return word;
    
  }
  
  /**
   * Figures out how to represent the given operand, and returns a modified
   * version of {@code word}.
   * 
   * Note that the operand may be applied as zero.  In particular, the current
   * symbol table implementation ({@link ProgramBuilder}) will provide zero as
   * a placeholder for unresolved forward references.
   * 
   * @param word  original instruction word 
   * @param operand  operand to apply
   * @param offset  offset into the word where the operand should appear
   * @return  a modified instructon word containing the operand.
   */
  private int applyOperand(int word, Operand operand, int offset) {
    if(operand == null) return word;
    
    int value = 0;
    if(operand.containsValue()) {
      value = operand.getValue();
    } else {
      LabelReference ref = (LabelReference)operand;
      value = ref.retrieveAddress(table, offset);
    }
    word |= value << offset;
    return word;
  }

}
