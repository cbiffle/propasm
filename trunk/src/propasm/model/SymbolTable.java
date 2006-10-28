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
 * Interface for objects that handle symbol/label tables during compile time.
 * 
 * @author cbiffle
 *
 */
public interface SymbolTable {
  /**
   * Retrieves or generates a Cog-local (runtime) address for a named symbol.
   * The offset within the target instruction word is also required, in case the
   * SymbolTable has to defer generation of the address.
   * 
   * @param symbol name of symbol
   * @param offset offset within word where it should be placed.
   * @return local address of the symbol, in the low-order six bits.
   */
  int localAddressOfSymbol(String symbol, int offset);
  
  /**
   * Retrieves or generates a shared-RAM (image) address for a named symbol.
   * The offset within the target instruction word is also required, in case the
   * SymbolTable has to defer generation of the address.
   * 
   * @param symbol name of symbol
   * @param offset offset within word where it should be placed.
   * @return local address of the symbol, in the low-order sixteen bits.
   */
  int imageAddressOfSymbol(String symbol, int offset);
}
