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
package propasm.parallax;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import propasm.model.Operand;


/**
 * @author cbiffle
 *
 */
public class ParallaxRegisterNamesTest {
  private ParallaxRegisterNames prn;
  
  @Before public void setUp() {
    prn = new ParallaxRegisterNames();
  }
  
  @Test public void testBasicGet() {
    Operand reg = prn.getRegister("ina");
    assertNotNull(reg);
    assertEquals(0x1F2, reg.getValue());
  }
  
  @Test public void testCaseVariations() {
    assertNotNull(prn.getRegister("INA"));
    assertNotNull(prn.getRegister("InA"));
  }
  
  @Test public void testUnknownRegisterGivesNull() {
    assertNull(prn.getRegister("ax"));
  }
}
