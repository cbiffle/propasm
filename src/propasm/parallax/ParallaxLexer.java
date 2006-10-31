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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static propasm.parallax.Token.Type.*;

/**
 * Scanner/lexer for the Parallax format.  This is a whitespace-sensitive format
 * (guh), so this scanner collects all contiguous blocks of non-linefeed
 * whitespace into separate SPACE tokens, which the Parser must deal with
 * explicitly.
 * 
 * The Parallax mnemonic format is not specified by a formal grammar, so this
 * lexer takes some liberties and makes some interpretations:<ol>
 * <li>Tokens that can be distinguished without intervening whitespace do not
 *     require it.  For example, "foo$1F2" lexes into two separate tokens,
 *     the IDENT "foo" and the HEX_NUMBER "$1F2".  (The parser grammar still
 *     requires whitespace in the logical places.)</li>
 * <li>Labels and mnemonics allow the full Unicode character set.</li>
 * <li>Space and tab are treated as whitespace.</li>
 * <li>Accepts both normal line endings (ASCII 0x10) and DOS doublebyte line
 *     endings (ASCII 0x13 0x10), for those of you still on teletypes.</li>
 * </ol>
 * 
 * @author cbiffle
 *
 */
public class ParallaxLexer {
  /** Input. */
  private final Reader in;
  /** List of tokens, built up as scanning progresses. */
  private final List<Token> tokens = new ArrayList<Token>();
  /** Unicode codepoint of next character in input stream. */
  private int c;
 
  private int lineNumber = 1, colNumber = 1;
  private int startLine = 1, startCol = 1;
  /** Buffer for building up the text of the current token. */
  private final StringBuilder currentText = new StringBuilder();
  /** Whether or not we're currently normalizing case to lowercase. */
  boolean ignoreCase = true;

  /**
   * Creates a new, ready-to-use lexer for the given data source.
   * 
   * @param in  character data source.
   */
  public ParallaxLexer(Reader in) {
    this.in = in;
  }

  /**
   * Kicks off the lexing, and returns the lexed tokens as an Iterable.  This
   * method will exhaust the input.
   * 
   * @return an Iterable of Tokens found in the input, terminated by an EOF
   *         Token.
   * @throws IOException  if the input stream cannot be read.
   * @throws ParseException  if the input cannot be made to fit within our
   *         grammar.
   */
  public Iterable<Token> lex() throws IOException, ParseException {
    readChar();
    while(c != -1) {
      next();
    }
    quickToken(EOF, "");
    return tokens;
  }
  
  /**
   * Dispatches the next token from the input stream.
   * 
   * @throws IOException  if the input stream cannot be read.
   * @throws ParseException  if the token cannot be lexed.
   */
  private void next() throws IOException, ParseException {
    if(isNewline()) {
      newline();
    } else if(isWhitespace()) {
      space();
    } else if(isIdentifierStart()) {
      identifier();
    } else if(c == '$') {
      hexLiteral();
    } else if(c == '%') {
      binaryLiteral();
    } else if(c == '-' || c >= '0' && c <= '9') {
      decimalLiteral();
    } else if(c == '\'') {
      comment();
    } else if(c == '"') {
      string();
    } else if(c == '#') {
      quickToken(HASH, "#");
    } else if(c == ',') {
      quickToken(COMMA, ",");
    } else if(c == ':') {
      quickToken(COLON, ":");
    } else if(c == '@') {
      quickToken(AT, "@");
    } else if(c == '.') {
      quickToken(DOT, ".");
    } else {
      throw new ParseException("Unexpected character '" + (char)c + "'",
                               lineNumber, colNumber);
    }
  }

  private boolean isNewline() {
    return c == '\n' || c == '\r';
  }
  
  /////// BEGIN TOKEN PROCESSING METHODS
  
  private void newline() throws IOException {
    int previous = c;
    appendAndAdvance();
    if(previous == '\r' && c == '\n') {
      appendAndAdvance();
    }
    finishToken(NL);
    lineNumber++;
    colNumber = 1;
  }

  private boolean isWhitespace() {
    return Character.isWhitespace(c);
  }

  private void space() throws IOException {
    appendAndAdvance();
    while(isWhitespace() && (c != '\r' && c != '\n')) {
      appendAndAdvance();
    }

    finishToken(SPACE);
  }
  
  private boolean isIdentifierStart() {
    return Character.isLetter(c) || c == '_';
  }

  private void identifier() throws IOException {
    appendAndAdvance();
    while(isIdentifierStart() || isDecimalDigit()) {
      appendAndAdvance();
    }
    finishToken(IDENT);
  }
  
  private void hexLiteral() throws IOException, ParseException {
    appendAndAdvance();
    if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == '_') {
      while((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == '_') {
        if(c == '_') {
          // skip
          readChar();
        } else {
          appendAndAdvance();
        }
      }
    } else {
      throw new ParseException("Expecting hex literal after $; found unexpected char " + (char)c,
                               lineNumber, colNumber);
    }
    
    finishToken(HEX_NUMBER);
  }

  private void binaryLiteral() throws IOException, ParseException {
    appendAndAdvance();
    if(c == '1' || c == '0' || c == '_') {
      while(c == '1' || c == '0' || c == '_') {
        if(c == '_') {
          // skip
          readChar();
        } else {
          appendAndAdvance();
        }
      }
    } else {
      throw new ParseException("Expecting binary literal after %; found unexpected char " + (char)c,
                               lineNumber, colNumber);
    }

    finishToken(BINARY_NUMBER);
  }
  
  private boolean isDecimalDigit() {
    return (c >= '0' && c <= '9');
  }
  private void decimalLiteral() throws IOException, ParseException {
    appendAndAdvance();
    while(isDecimalDigit() || c == '_') {
      if(c == '_') {
        // skip
        readChar();
      } else {
        appendAndAdvance();
      }
    }
    
    finishToken(DECIMAL_NUMBER);
  }
  
  private void comment() throws IOException {
    appendAndAdvance();
    while(c != '\n' && c != '\r' && c != -1) {
      appendAndAdvance();
    }
    finishToken(COMMENT);
  }

  private void string() throws IOException, ParseException {
    ignoreCase = false;
    readChar(); // do not include initial quote mark.

    boolean escape = false;
    while(escape || c != '"') {
      if(c == '\n' || c == '\r') {
        throw new ParseException("String literals cannot span lines.",
                                 lineNumber, colNumber);
      } else if(c == -1) {
        throw new ParseException("Unterminated string literal at EOF",
                                 lineNumber, colNumber);
      } else if(escape) {
        switch(c) {
        case '\\':
          currentText.append('\\');
          break;
        case 'n':
          currentText.append('\n');
          break;
        case 'r':
          currentText.append('\r');
          break;
        case 'e':
          currentText.append((char)0x1B);
          break;
        case 'b':
          currentText.append('\b');
          break;
        case 'f':
          currentText.append('\f');
          break;
        case '"':
          currentText.append('"');
          break;
        default:
          throw new ParseException("Unknown character escape '\\" + (char)c + "'",
                                   lineNumber, colNumber);
        }
        readChar();
        escape = false;
      } else if(c == '\\') {
        escape = true;
        readChar();
      } else {
        appendAndAdvance();
      }
    }
    ignoreCase = true;
    readChar(); // omit final quote mark
    
    finishToken(STRING);
  }
  
  //// END TOKEN PROCESSING METHODS
  
  /**
   * Fabricates a simple token using predefined text.  Used for the simple
   * tokens, such as HASH, COLON, and COMMA.
   */
  private void quickToken(Token.Type type, String text) throws IOException {
    Token token = new Token();
    token.setType(type);
    token.setLine(lineNumber);
    token.setColumn(colNumber);
    token.setText(text);
    tokens.add(token);
    readChar();
  }

  /**
   * Includes the current character in the Token being built, and advances one
   * character.
   * 
   * @throws IOException  if advancing a character fails.
   */
  private void appendAndAdvance() throws IOException {
    currentText.appendCodePoint(c);
    readChar();
  }

  /**
   * Creates a new Token using text buffered by {@link #appendAndAdvance()}
   * and adds it to the stream.
   * 
   * @param type  type of Token to create
   */
  private void finishToken(Token.Type type) {
    Token token = new Token();
    token.setType(type);
    token.setLine(startLine);
    token.setColumn(startCol);
    token.setText(currentText.toString());
    currentText.setLength(0);
    tokens.add(token);
    
    startLine = lineNumber;
    startCol = colNumber;
  }

  /**
   * Advances input by one character.  Also lowercases.
   * 
   * @throws IOException  if input cannot be read.
   */
  private void readChar() throws IOException {
    c = in.read();
    if(c != -1 && ignoreCase) c = Character.toLowerCase(c);
    colNumber++;
    if(c == '\t') {
      colNumber += 7;
    }

  }
}
