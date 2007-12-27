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
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import propasm.model.AssemblyInputException;
import propasm.model.ClockMode;
import propasm.model.Effect;
import propasm.model.Instruction;
import propasm.model.InstructionSet;
import propasm.model.LabelReference;
import propasm.model.LogicException;
import propasm.model.NumericOperand;
import propasm.model.Operand;
import propasm.model.Operation;
import propasm.model.PllMode;
import propasm.model.Predicate;
import propasm.model.ProgramBuilder;
import propasm.model.RegisterSet;
import static propasm.parallax.Token.Type.*;

/**
 * Parser for Parallax's assembler format, as used by the assembler built into
 * the Propeller Tool software.  Consumes {@link Token} objects, as generated
 * by the {@link ParallaxLexer}.
 * 
 * Parallax's assembler format is not specified in a formal grammar, so this
 * parser takes some liberties and makes some interpretations:<ul>
 * <li>Allows labels on their own line.</li>
 * <li>Allows potentially conflicting effects to be specified.  (This may change
 *     in the next version.)</li>
 * <li>Does not allow a local label to be specified before any non-local label
 *     has been defined.</li>
 * </ul>
 * 
 * @author cbiffle
 *
 */
public class ParallaxParser {
  private final InstructionSet isa = new ParallaxMnemonics();
  private final RegisterSet registers = new ParallaxRegisterNames();

  /** ProgramBuilder used as output. */
  private final ProgramBuilder builder;
  /** Enclosing context for inclusions. */
  private final InclusionHandler parent;
  /** Token iterator derived from lexer's Iterable. */
  private Iterator<Token> tokens;
  
  /** Current token being considered. */
  private Token current;
  /** Most recent non-local label encountered (for namespacing local labels). */
  private String nonLocalLabel;
  
  /**
   * Initializes a new parser, using the given {@link ProgramBuilder} for
   * output and symbol resolution.
   * 
   * @param builder  output target.
   * @param parent   parent context for handling included files and such.
   */
  public ParallaxParser(ProgramBuilder builder, InclusionHandler parent) {
    this.parent = parent;
    this.builder = builder;
  }
  
  /**
   * Parses the given stream of tokens.
   * 
   * @param tokens  tokens to parse.
   * @throws ParseException  if the token stream is syntactically invalid.
   * @throws LogicException  if the token stream has semantic issues, such as
   *         failing a FIT directive.
   * @throws AssemblyInputException 
   */
  public void parse(Iterable<Token> tokens) throws AssemblyInputException {
    this.tokens = tokens.iterator();
    advance();
    program();
  }
  
  /*
   * program ::= ( COMMENT | NL | line )*
   * 
   */
  private void program() throws AssemblyInputException {
    while(!current.is(EOF)) {
      if(current.is(COMMENT) || current.is(NL)) {
        advance(); // skip
      } else if(current.is(IDENT) || current.is(SPACE) || current.is(COLON)) {
        line();
      } else {
        throw new ParseException("Unexpected start of line: " + current.getText(), current);
      }
    }
  }
  
  /*
   * line ::= ( <label> | <local-label> )? SPACE 
   *          ( <predicate> <op-or-directive> (SPACE)? (COMMENT)? )?
   */
  private void line() throws AssemblyInputException {
    if(current.is(IDENT)) {
      label();
    } else if(current.is(COLON)) {
      localLabel();
    }
    if(current.is(NL)) return;
    if(current.is(SPACE)) {
      advance();
    } else {
      throw new ParseException("Expecting whitespace, found: " + current.getText(),
                               current); 
    }
    if(current.is(NL)) return;
    Predicate pred = null;
    if(current.is(IDENT)) {
      pred = tryPredicate();
    }
    opOrDirective(pred);
    allowOptionalWhitespace();
    if(current.is(COMMENT)) {
      advance();
    }
  }
  
  /*
   * label ::= IDENT
   */
  private void label() throws LogicException {
    String label = current.getText();
    int line = current.getLine(), col = current.getColumn();
    advance();
    try {
      builder.defineLabel(label);
    } catch(IllegalStateException e) {
      throw new LogicException("Label '" + label + "' defined more than once!",
                               line, col);
    }
    nonLocalLabel = label;
  }
  
  /*
   * local-label ::= COLON IDENT
   */
  private void localLabel() throws ParseException {
    advance();
    String labelName = current.getText();
    if(!current.is(IDENT)) {
      throw new ParseException("Expecting label name, found: " + labelName,
                               current);
    }
    if(nonLocalLabel == null) {
      throw new ParseException("Local labels like :" + labelName + " must be " +
                               "defined beneath a non-local label!",
                               current);
    }
    String label = swizzleLocalLabel(labelName);
    builder.defineLabel(label);
    advance();
  }

  /*
   * predicate ::= ( IDENT SPACE )?
   *   such that IDENT starts with if_
   */
  private Predicate tryPredicate() throws ParseException {
    String text = current.getText();
    Predicate pred = null;
    if(text.startsWith("if_")) {
      pred = Predicate.valueOf(text.toUpperCase());
      advance();
      if(current.is(SPACE)) {
        advance();
      } else {
        throw new ParseException("Expecting whitespace after predicate, found: " + current.getText(),
                                 current);
      }
    }
    return pred;
  }
  /*
   * op-or-directive ::= ( <org-directive>
   *                     | <fit-directive>
   *                     | <res-directive>
   *                     | <byte-data>
   *                     | <word-data>
   *                     | <long-data>
   *                     | <op>
   *                     )
   */
  private void opOrDirective(Predicate pred) throws AssemblyInputException {
    if(current.is(COMMENT)) {
      return;
    }
    if(!current.is(IDENT) && !current.is(DOT)) {
      throw new ParseException("Expecting operation or directive, found: " + current.getText(),
                               current);
    }
    if(current.is(DOT)) {
      extendedDirective();
    } else {
      String text = current.getText();
      if(text.equals("org")) {
        if(pred != null) {
          throw new ParseException("Cannot use predicate with ORG.", current);
        }
        orgDirective();
      } else if(text.equals("fit")) {
        if(pred != null) {
          throw new ParseException("Cannot use predicate with FIT.", current);
        }
        fitDirective();
      } else if(text.equals("res")) {
        if(pred != null) {
          throw new ParseException("Cannot use predicate with RES.", current);
        }
        res();
      } else if(text.equals("byte")) {
        byteData();
      } else if(text.equals("word")) {
        wordData();
      } else if(text.equals("long")) {
        longData();
      } else {
        op(pred);
      }
    }
  }
  /*
   * extended-directive ::= DOT IDENT ( operand ( COMMA operand )* )?
   */
  private void extendedDirective() throws AssemblyInputException {
    advance(); // skip dot
    expect(IDENT, "Expecting extended directive");
    
    String text = current.getText();
    int line = current.getLine(), col = current.getColumn();
    advance();
    if(text.equals("align")) {
      alignDirective();
    } else if(text.equals("include")) {
      includeDirective();
    } else if(text.equals("blob")) {
      blobDirective();
    } else if(text.equals("xinfreq")) {
      xinfreqDirective();
    } else if(text.equals("clkmode")) {
      clkmodeDirective();
    } else {
      throw new ParseException("Unknown directive: ." + text,
                               line, col);
    }
  }
  
  private void alignDirective() throws AssemblyInputException {
    allowOptionalWhitespace();
    expect(IDENT, "Expecting alignment type");
    
    String text = current.getText();
    if(text.equals("byte")) {
      // no-op
    } else if(text.equals("word")) {
      builder.ensureWordAlignment();
    } else if(text.equals("long")) {
      builder.ensureLongAlignment();
    } else {
      throw new ParseException("Invalid alignment type: " + text, current);
    }
    advance();
  }
  
  private void includeDirective() throws AssemblyInputException {
    allowOptionalWhitespace();
    expect(STRING, "Expecting include filename");
    
    String filename = current.getText();
    try {
      parent.include(filename);
    } catch(IOException e) {
      throw new ParseException("Could not include " + filename, current);
    }
    advance();
  }

  private void blobDirective() throws AssemblyInputException {
    allowOptionalWhitespace();
    expect(STRING, "Expecting blob filename");
    
    String filename = current.getText();
    try {
      parent.includeBlob(filename);
    } catch(IOException e) {
      throw new ParseException("Could not include blob " + filename, current);
    }
    advance();
  }
  
  private void xinfreqDirective() throws AssemblyInputException {
    allowOptionalWhitespace();
    expect(DECIMAL_NUMBER, "Expecting input frequency");
    
    builder.setInputFrequency(Integer.parseInt(current.getText()));
    
    advance();
  }
  
  private void clkmodeDirective() throws AssemblyInputException {
    allowOptionalWhitespace();
    expect(IDENT, "Expecting oscillator mode");
    ClockMode c = parseEnum(ClockMode.class, "oscillator mode");
    
    allowOptionalWhitespace();
    PllMode pll = PllMode.PLL_DISABLED;
    if(current.is(IDENT)) {
      pll = parseEnum(PllMode.class, "PLL mode");
    }
    
    builder.setInitialClockMode(c, pll);
  }
  
  /*
   * org-directive ::= "org" SPACE <optional-number>
   */
  private void orgDirective() throws ParseException {
    advance();
    allowOptionalWhitespace();
    int value = number(0);
    builder.setRuntimeAddress(value);
  }
  /*
   * res-directive ::= "res" SPACE <number>
   */
  private void res() throws ParseException {
    advance();
    allowOptionalWhitespace();
    int count = number();
    builder.reserveBytes(count * 4);
  }
  /*
   * byte-data ::= "byte" SPACE <number>
   */
  private void byteData() throws ParseException {
    advance();
    if(current.is(SPACE)) {
      advance();
    } else {
      throw new ParseException("Expected whitespace after BYTE, found: " + current.getText(),
                               current);
    }
    int line = current.getLine(), col = current.getColumn();
    if(current.is(STRING)) {
      for(byte b : getUtf8Bytes()) {
        builder.addByte(b);
      }
      advance();
    } else {
      int constant = number();
      int highbits = constant & 0xFFFFFF00;
      if(highbits != 0 && highbits != 0xFFFFFF00) {
        throw new ParseException("Constant out of range for byte literal: " + constant,
                                 line, col);
      }
      builder.addByte((byte)constant);
    }
    
    allowOptionalWhitespace();
    while(current.is(COMMA)) {
      advance();
      allowOptionalWhitespace();
      line = current.getLine(); col = current.getColumn();
      if(current.is(STRING)) {
        for(byte b : getUtf8Bytes()) {
          builder.addByte(b);
        }
        advance();
      } else {
        int constant = number();
        int highbits = constant & 0xFFFFFF00;
        if(highbits != 0 && highbits != 0xFFFFFF00) {
          throw new ParseException("Constant out of range for byte literal: " + constant,
                                   line, col);
        }
        builder.addByte((byte)constant);
      }
      allowOptionalWhitespace();
    }
  }

  private byte[] getUtf8Bytes() {
    try {
      return current.getText().getBytes("UTF8");
    } catch(UnsupportedEncodingException e) {
      throw new RuntimeException("Platform does not support UTF-8; aborting.");
    }
  }
  /*
   * word-data ::= "word" SPACE <number>
   */
  private void wordData() throws AssemblyInputException {
    advance();
    if(current.is(SPACE)) {
      advance();
    } else {
      throw new ParseException("Expected whitespace after BYTE, found: " + current.getText(),
                               current);
    }
    Operand value = operand(16);
    builder.ensureWordAlignment();
    if(value.containsValue()) {
      int constant = value.getValue();
      builder.addWord(constant);
    } else {
      LabelReference ref = (LabelReference)value;
      builder.addWord(ref.retrieveAddress(builder, 0));
    }
    
    allowOptionalWhitespace();
    while(current.is(COMMA)) {
      advance();
      allowOptionalWhitespace();
      value = operand(16);
      builder.ensureWordAlignment();
      if(value.containsValue()) {
        int constant = value.getValue();
        builder.addWord(constant);
      } else {
        LabelReference ref = (LabelReference)value;
        builder.addWord(ref.retrieveAddress(builder, 0));
      }
      allowOptionalWhitespace();
    }
  }
  /*
   * long-data ::= "long" SPACE <number>
   */
  private void longData() throws AssemblyInputException {
    advance();
    if(current.is(SPACE)) {
      advance();
    } else {
      throw new ParseException("Expected whitespace after LONG, found: " + current.getText(),
                               current);
    }
    Operand value = operand(32);
    builder.ensureLongAlignment();
    if(value.containsValue()) {
      int constant = value.getValue();
      builder.addLong(constant);
    } else {
      LabelReference ref = (LabelReference)value;
      builder.addLong(ref.retrieveAddress(builder, 0));
    }
    
    allowOptionalWhitespace();
    while(current.is(COMMA)) {
      advance();
      allowOptionalWhitespace();
      value = operand(32);
      builder.ensureLongAlignment();
      if(value.containsValue()) {
        int constant = value.getValue();
        builder.addLong(constant);
      } else {
        LabelReference ref = (LabelReference)value;
        builder.addLong(ref.retrieveAddress(builder, 0));
      }
      allowOptionalWhitespace();
    }
  }
  /*
   * optional-number ::= ( <number> )?
   */
  private int number(int def) throws ParseException {
    return number(def, false);
  }
  
  private int number() throws ParseException {
    return number(0, true);
  }
  
  /*
   * number ::= ( HEX_NUMBER | BINARY_NUMBER | DECIMAL_NUMBER )
   */
  private int number(int def, boolean required) throws ParseException {
    int value = def;
    int base = 0;
    String num = null;
    if(current.is(HEX_NUMBER)) {
      base = 16;
      num = current.getText().substring(1);
    } else if(current.is(BINARY_NUMBER)) {
      base = 2;
      num = current.getText().substring(1);
    } else if(current.is(DECIMAL_NUMBER)) {
      base = 10;
      num = current.getText();
    } else if(required) {
      throw new ParseException("Expecting number, found: " + current.getText(), current);
    } else {
      return value;
    }

    long v;
    try {
      v = Long.parseLong(num, base);
    } catch(NumberFormatException e) {
      throw new ParseException("Could not parse number " + current.getText(),
                               current);
    }
    if((v >> 32) != 0 && (v >> 32) != -1) {
      throw new ParseException("Value " + current.getText() + 
                               " is greater than 32 bits in length.",
                               current);
    }
    value = (int)v;
    advance();

    return value;
  }
  
  /*
   * fit-directive ::= "fit" SPACE <optional-number>
   */
  private void fitDirective() throws AssemblyInputException {
    int line = current.getLine();
    int col = current.getColumn();
    advance();
    int reqAddr = number(0x1F0);
    int addr = builder.getRuntimeAddress();
    
    if(addr >= reqAddr) {
      throw new LogicException("Program exceeds bounds!  " +
                "(Required to fit beneath " + Integer.toHexString(reqAddr) +
                ", current size is " + Integer.toHexString(addr) + ")",
                line, col);
    }
    System.out.println("Line " + line + ": FIT directive succeeded at address " + addr);
  }
  
  /*
   * op ::= IDENT ( <dest-operand>
   *              | <source-operand>
   *              | <dest-operand> COMMA ( SPACE )? <source-operand> )?
   *              ( <effect> ( COMMA ( SPACE )? <effect> )* )?
   * dest-operand ::= <operand>
   * source-operand ::= ( HASH )? <operand>
   */
  private void op(Predicate pred) throws AssemblyInputException {
    Instruction instr = new Instruction(builder);
    String mnemonic = current.getText();
    Operation op = isa.getOperationForMnemonic(mnemonic);
    if(op == null) {
      throw new ParseException("Unknown operation mnemonic '" + mnemonic + "'",
                               current);
    }
    instr.setOperation(op);
    instr.setPredicate(pred);
    advance();
    if(current.is(SPACE)) {
      advance();
    }
    
    boolean d = false;
    if(op.requiresDest()) {
      int destLine = current.getLine(), destCol = current.getColumn();
      Operand dest = operand(9);
      if(dest.isReadOnly()) {
        throw new LogicException("Read-only operand cannot be used in Dest " +
                        "field of instruction.", destLine, destCol);
      }
      instr.setDest(dest);
      d = true;
    }
    allowOptionalWhitespace();
    if(op.requiresSource()) {
      if(d) {
        if(current.is(COMMA)) {
          advance();
          allowOptionalWhitespace();
        } else {
          throw new ParseException("Expecting comma between D and S operands; " +
                                   "found: " + current.getText(), current);
        }
      }
      if(current.is(HASH)) {
        instr.setImmediateSource(true);
        advance();
      }
      instr.setSource(operand(9));
      
    }
    allowOptionalWhitespace();
    if(current.is(IDENT)) {
      instr.addEffect(effect());
      allowOptionalWhitespace();
      while(current.is(COMMA)) {
        advance();
        allowOptionalWhitespace();
        if(current.is(IDENT)) {
          instr.addEffect(effect());
        } else {
          throw new ParseException("Expecting more effects after comma.", current);
        }
      }
    }
    
    builder.addInstruction(instr);
  }
  /*
   * operand ::= <number> | <label> | <local-label>
   */
  private Operand operand(int bits) throws AssemblyInputException {
    if(current.is(HEX_NUMBER) || current.is(DECIMAL_NUMBER) || current.is(BINARY_NUMBER)) {
      int line = current.getLine(), column = current.getColumn();
      int value = number();
      int highbits = (value & ~(-1 >>> (32 - bits)));
      highbits >>= bits;
      if(highbits != 0 && highbits != -1) { 
        throw new LogicException("Number out of range for operand: " + value,
                                 line, column);
      }
      return new NumericOperand(value, bits);
    }
    LabelReference.MemoryType type = LabelReference.MemoryType.LOCAL;
    if(current.is(AT)) {
      type = LabelReference.MemoryType.SHARED;
      advance();
    }
    if(current.is(COLON)) {
      advance();
      String text = current.getText();
      if(current.is(IDENT)) {
        if(nonLocalLabel == null) {
          throw new ParseException("Uses of local labels like :" + text +
                                   " must appear beneath a non-local label!",
                                   current);
        }
        advance();
        return new LabelReference(swizzleLocalLabel(text), type);
      } else {
        throw new ParseException("Expected local label name, found: " + text,
                                 current);
      }
    } else if(current.is(IDENT)) {
      Operand reg = registers.getRegister(current.getText());
      if(reg == null) {
        reg = new LabelReference(current.getText(), type);
      } else {
        if(type != LabelReference.MemoryType.LOCAL) {
          throw new ParseException("Cannot take shared-RAM address of a register.", current);
        }
      }
      advance();
      return reg;
    } else {
      throw new ParseException("Expected number, label, or register name; " +
                               " found: " + current.getText(), current);
    }
  }
  /*
   * effect ::= IDENT
   */
  private Effect effect() throws ParseException {
    Effect effect = null;
    try {
      effect = Effect.valueOf(current.getText().toUpperCase());
    } catch(IllegalArgumentException e) {
      // foo.
    }
    if(effect == null) {
      throw new ParseException("Unknown effect: " + current.getText(), current);
    }
    advance();
    return effect;
  }
  
  /**
   * Name-swizzles a local label, to make it effectively unique beneath the most
   * recent non-local label.
   * 
   * For a containing label "foo" and a local label ":bar", the swizzled form is
   *   foo->:bar
   * 
   * This was chosen to avoid name conflicts (as it contains invalid characters)
   * and to be reasonably descriptive if used in error messages.
   * 
   * @param labelName  local label to swizzle.
   * @return swizzled form.
   */
  private String swizzleLocalLabel(String labelName) {
    return nonLocalLabel + "->:" + labelName;
  }

  /**
   * Consumes any whitespace at the current position.
   */
  private void allowOptionalWhitespace() {
    if(current.is(SPACE)) advance();
  }
  
  /**
   * Advances to the next token.
   */
  private void advance() {
    current = tokens.next();
  }
  
  /**
   * Verifies that the current token is of a given type, without advancing.
   * If the type is wrong, throws a ParseException using the {@code context}
   * message.
   * 
   * @param type  type of token expected.
   * @param context  brief message describing context (e.g. "parsing number")
   * @throws ParseException  if the token does not match.
   */
  private void expect(Token.Type type, String context) throws ParseException {
    if(!current.is(type)) {
      throw new ParseException(context +" (found: " + current.getText() + ")",
                               current);
    }
  }
  
  private <E extends Enum<E>> E parseEnum(Class<E> clazz, String context)
      throws ParseException {
    expect(IDENT, "Expecting " + context);
    for(E e : clazz.getEnumConstants()) {
      if(e.toString().equalsIgnoreCase(current.getText())) {
        advance();
        return e;
      }
    }
    
    throw new ParseException("Invalid " + context + ": " + current, current);
  }
}
