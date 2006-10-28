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

import org.junit.Test;


public class TestOperation {
 
  @Test public void testBasicTemplate() {
    Operation op = new Operation() {
        @Override public int getOpcode() {
          return 0;
        }
      };
    assertEquals(0x00BC0000, op.getTemplate());
  }

  @Test public void testOpcodeGeneration() {
    Operation op = new Operation() {
        @Override public int getOpcode() {
          return 0x1A;
        }
      };
    assertEquals(0x68BC0000, op.getTemplate());
  }

  @Test public void testDefaultResultFlag() {
    Operation op = new Operation() {
        @Override public int getOpcode() {
          return 0;
        }
        @Override public boolean generatesResultByDefault() {
          return false;
        }
      };
    assertEquals(0x003C0000, op.getTemplate());
  }
  

  @Test public void testDefaultImmediateFlag() {
    Operation op = new Operation() {
        @Override public int getOpcode() {
          return 0;
        }
        @Override public boolean immediateByDefault() {
          return true;
        }
      };
    assertEquals(0x00FC0000, op.getTemplate());
  }
  
  @Test public void testDefaultPredicate() {
    Operation op = new Operation() {
        @Override public int getOpcode() {
          return 0;
        }
        @Override public Predicate defaultPredicate() {
          return Predicate.IF_C_AND_NZ;
        }
      };
    assertEquals(0x00900000, op.getTemplate());
  }
  
    
}
