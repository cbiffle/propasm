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

import propasm.AssemblerConfig;


/**
 * TODO: coverage of this class is not as deep as it should be.
 *
 */
public class TestProgramBuilder {
  private ProgramBuilder builder;
  
  @Before public void setUp() {
    AssemblerConfig config = new AssemblerConfig();
    config.setGenerateBootloader(true);
    builder = new ProgramBuilder(config);
  }
  
  /**
   * Hand-assembled empty program fixture.
   */
  private static final byte[] EMPTY_PROGRAM = {
    0x00, (byte)0xB4, (byte)0xC4, 0x04, 0x6F, (byte)0xA2, 0x10, 0x00,
    0x20, 0x00, 0x28, 0x00, 0x18, 0x00, 0x2C, 0x00,
    0x10, 0x00, 0x02, 0x00, 0x08, 0x00, 0x00, 0x00,
    0x35, 0x37, 0x04, 0x35, 0x2C, 0x00, 0x00, 0x00,
  };
  
  @Test public void testEmptyProgram() throws LogicException {
    byte[] data = builder.finish();
    assertBytesEqual(EMPTY_PROGRAM, data);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsOutOfRangeRuntimeAddress() {
    builder.setRuntimeAddress(600);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testRejectsNegativeRuntimeAddress() {
    builder.setRuntimeAddress(-30);
  }
  
  @Test public void testRuntimeAddressSticks() {
    // Yes, this test is trivial, but I actually broke this at one point.
    builder.setRuntimeAddress(42);
    assertEquals(42, builder.getRuntimeAddress());
    builder.reserveBytes(8 * 4);
    assertEquals(50, builder.getRuntimeAddress());
  }
  
  
  @Test public void testImageAddressReflectsReservations() {
    assertEquals(EMPTY_PROGRAM.length, builder.getImageAddress());
    builder.reserveBytes(171);
    assertEquals(EMPTY_PROGRAM.length + 171, builder.getImageAddress());
    builder.addByte((byte)0x42);
    assertEquals(EMPTY_PROGRAM.length + 172, builder.getImageAddress());
  }

  @Test public void testRuntimeAddressReflectsReservations() {
    assertEquals(0, builder.getRuntimeAddress());
    builder.reserveBytes(171);
    assertEquals(171 / 4, builder.getRuntimeAddress());
    builder.addByte((byte)0x42);
    assertEquals(172 / 4, builder.getRuntimeAddress());
  }
  
  
  private void assertBytesEqual(byte[] fixture, byte[] data) {
    assertEquals(fixture.length, data.length);
    for(int i = 0; i < fixture.length; i++) {
      assertEquals("At position " + i, fixture[i], data[i]);
    }
  }
}
