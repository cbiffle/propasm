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

import org.junit.Test;


public class TestNumericOperand {

  private NumericOperand no;
  
  @Test public void testValidPositiveCreation() {
    no = new NumericOperand(0x0);
    no = new NumericOperand(0x1);
    no = new NumericOperand(0x1FF);
  }

  @Test public void testValidNegativeCreation() {
    no = new NumericOperand(-1);
    no = new NumericOperand(-42);
    no = new NumericOperand(-256);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPositiveCreation() {
    no = new NumericOperand(0x2FF);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNegativeCreation() {
    no = new NumericOperand(-257);
  }
}
