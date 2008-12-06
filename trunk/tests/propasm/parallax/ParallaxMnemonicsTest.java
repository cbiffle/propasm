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

import propasm.model.Operation;


/**
 * @author cbiffle
 *
 */
public class ParallaxMnemonicsTest {
  private ParallaxMnemonics mne;
  
  @Before public void setUp() {
    mne = new ParallaxMnemonics();
  }
  
  @Test public void testBasicGet() {
    Operation op = mne.getOperationForMnemonic("mov");
    assertNotNull(op);
    assertEquals(0x28, op.getOpcode());
  }
  
  @Test public void testCaseVariations() {
    assertNotNull(mne.getOperationForMnemonic("MOV"));
    assertNotNull(mne.getOperationForMnemonic("MoV"));
  }
  
  @Test public void testUnknownOperationGivesNull() {
    assertNull(mne.getOperationForMnemonic("lwrx"));
  }
}
