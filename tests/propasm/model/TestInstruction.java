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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import propasm.testutil.MockSymbolTable;


public class TestInstruction {
  private MockSymbolTable symbolTable;
  private Instruction instruction;
  
  @Before public void setUp() {
    symbolTable = new MockSymbolTable();
    instruction = new Instruction(symbolTable);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsNullOperation() {
    instruction.setOperation(null);
  }

  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsNullSource() {
    instruction.setSource(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsNullDest() {
    instruction.setDest(null);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsReadOnlyDest() {
    Operand ro = new Operand() {
      @Override public boolean isReadOnly() {
        return true;
      }
    };
    
    instruction.setDest(ro);
  }

  @Test public void testBinaryOpcode() {
    instruction.setOperation(new Operation() {
        // Set a notable bit pattern for the opcode.
        @Override public int getOpcode() {
          return 0x2A;
        }
        // Ensure that the R bit is not set by default.
        @Override public boolean generatesResultByDefault() {
          return false;
        }
      });
    instruction.setPredicate(Predicate.IF_NEVER); // zero out the predicate
    
    assertEquals(0xA8000000, instruction.binaryRepresentation());
  }

  @Test public void testEffectApplication() {
    instruction.setOperation(new Operation() {
        @Override public int getOpcode() {
          return 0x0;
        }
      });
    instruction.setPredicate(Predicate.IF_NEVER); // zero out the predicate
    
    assertEquals(0x00800000, instruction.binaryRepresentation());
    
    instruction.addEffect(Effect.NR);
    instruction.addEffect(Effect.WC);
    assertEquals(0x01000000, instruction.binaryRepresentation());
  }
  

  @Test public void testPredicateApplication() {
    instruction.setOperation(new Operation() {
        @Override public int getOpcode() {
          return 0x0;
        }
      });
    instruction.setPredicate(Predicate.IF_NEVER); // zero out the predicate
    
    assertEquals(0x00800000, instruction.binaryRepresentation());

    instruction.setPredicate(Predicate.IF_NC);
    assertEquals(0x008C0000, instruction.binaryRepresentation());
  }
  
  @Test public void testOperandApplication() {
    instruction.setOperation(new Operation() {
        @Override public int getOpcode() {
          return 0x0;
        }
      });
    instruction.setPredicate(Predicate.IF_NEVER); // zero out the predicate
    
    instruction.setSource(new NumericOperand(0x0A5));
    instruction.setDest(new NumericOperand(0x1FF));
    
    assertEquals(0x0083FEA5, instruction.binaryRepresentation());
  }
}
