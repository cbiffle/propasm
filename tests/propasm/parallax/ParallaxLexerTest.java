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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;

import static propasm.parallax.Token.Type.*;


/**
 * @author cbiffle
 *
 */
public class ParallaxLexerTest {
  private ParallaxLexer lexer;
  
  @Test public void testEmptyString() throws ParseException, IOException {
    Iterable<Token> tokens = lex("");
    assertTypes(tokens, EOF);
  }

  @Test public void testBasicTypes() throws ParseException, IOException {
    Iterable<Token> tokens = lex("hi 42 $42$AF0 @:foo#' hello\n\n");
    assertTypes(tokens, IDENT, SPACE, DECIMAL_NUMBER, SPACE, HEX_NUMBER,
                HEX_NUMBER, SPACE, AT, COLON, IDENT, HASH, COMMENT, NL, NL, EOF);
  }
  

  protected void makeLexer(Reader in) {
    lexer = new ParallaxLexer(in);
  }
  
  protected Iterable<Token> lex(String str) throws ParseException, IOException {
    makeLexer(new StringReader(str));
    return lexer.lex();
  }
  
  protected void assertTypes(Iterable<Token> tokens, Token.Type... types) {
    Iterator<Token> tokenI = tokens.iterator();
    for(Token.Type type : types) {
      assertEquals(type, tokenI.next().getType());
    }
  }
}
