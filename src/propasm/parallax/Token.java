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

/**
 * A token in the assembler input stream.  Generated by the scanner
 * ({@link ParallaxLexer}) and consumed by the parser ({@link ParallaxParser}).
 * 
 * @author cbiffle
 *
 */
public class Token {
  private String text;
  private int row, column;
  private Type type;
  
  /**
   * Indicates a token's type.
   */
  public enum Type {
    /** A hexadecimal constant, such as $1FF. */
    HEX_NUMBER,
    /** A binary constant, such as %0101. */
    BINARY_NUMBER,
    /** A decimal constant, such as 42. */
    DECIMAL_NUMBER,
    /** Any word-like identifier, such as a mnemonic or label. */
    IDENT,
    /** The hash sign (#), marking an immediate operand. */
    HASH,
    /** The colon (:), marking a local label. */
    COLON,
    /** The comma (,), separating operands. */
    COMMA,
    /** The commercial-at (@), for taking the heap address of a location. */
    AT,
    /** The dot, indicating a special directive. */
    DOT,
    /** Any number of non-linebreak whitespace characters. */
    SPACE,
    /** A comment. */
    COMMENT,
    /** An inline string. */
    STRING,
    /** A Unix, DOS, or pre-Unix Mac line ending. */
    NL,
    /** Virtual token for end-of-file. */
    EOF
  }

  public int getColumn() {
    return column;
  }
  public void setColumn(int column) {
    this.column = column;
  }

  public int getLine() {
    return row;
  }
  public void setLine(int row) {
    this.row = row;
  }

  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }

  public Type getType() {
    return type;
  }
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * Shorthand for checking if a token is of a certain type.
   * 
   * @param type  type desired
   * @return {@code true} for a match, {@code false} otherwise.
   */
  public boolean is(Type type) {
    return this.type == type;
  }
  
  @Override public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("[token ");
    buf.append(type);
    buf.append(" \"");
    buf.append(text);
    buf.append("\"]");
    return buf.toString();
  }
}