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


public class TestPredicate {
  /**
   * A very loose check of {@link Predicate#applyToWord(int)}.  Doesn't attempt
   * to check the actual flag values for more than a handful of Predicates, but
   * validates that those effects touch only the bits they should (and thus the
   * application algorithm). 
   */
  @Test public void testBasicApplication() {
    int word = 0xA5A5A5A5;
    word = Predicate.IF_NEVER.applyToWord(word);
    assertEquals(0xA581A5A5, word);
    word = Predicate.IF_AE.applyToWord(word);
    assertEquals(0xA58DA5A5, word);
    word = Predicate.IF_C_EQ_Z.applyToWord(word);
    assertEquals(0xA5A5A5A5, word);
  }
}
