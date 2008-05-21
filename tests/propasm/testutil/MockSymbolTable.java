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
package propasm.testutil;

import java.util.HashMap;
import java.util.Map;

import propasm.model.SymbolTable;

/**
 * A Map-backed SymbolTable implementation for testing.
 * 
 * @author cbiffle
 *
 */
public class MockSymbolTable implements SymbolTable {

  private final Map<String, Integer> imageAddresses =
      new HashMap<String, Integer>();
  private final Map<String, Integer> localAddresses =
      new HashMap<String, Integer>();
  
  public int imageAddressOfSymbol(String symbol,
      @SuppressWarnings("unused") int offset) {
    Integer addr = imageAddresses.get(symbol);
    if(addr == null) throw new IllegalStateException("Undefined: " + symbol);
    return addr;
  }

  public int localAddressOfSymbol(String symbol, 
      @SuppressWarnings("unused") int offset) {
    Integer addr = localAddresses.get(symbol);
    if(addr == null) throw new IllegalStateException("Undefined: " + symbol);
    return addr;
  }

}
