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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;

import propasm.AssemblerConfig;
import propasm.model.AssemblyInputException;
import propasm.model.LogicException;
import propasm.model.ProgramBuilder;

/**
 * Command-line main class for the Parallax-format assembler.
 * 
 * @author cbiffle
 *
 */
public class ParallaxFrontend implements InclusionHandler {
  public static void main(String[] args)
  throws IOException, AssemblyInputException {
    new ParallaxFrontend().assemble(args);
  }
  
  private ProgramBuilder builder;
  
  public void assemble(String[] argArray) throws IOException, AssemblyInputException {
    List<String> args = new ArrayList<String>();
	 for (String arg : argArray) args.add(arg);

	 AssemblerConfig config = new AssemblerConfig();
	 boolean commandLineOkay = consumeSwitches(args, config);
	 if (args.isEmpty()) {
		 System.err.println("No input files specified.");
		 commandLineOkay = false; // even if the flags were fine.
	 }
	 if (commandLineOkay == false) {
		 printUsage();
		 return;
	 }
    
    for(String filename : args) {
      builder = new ProgramBuilder(config);
      long time = System.currentTimeMillis();
      try {
        parse(filename);
      } catch(Exception e) {
        if(e instanceof RuntimeException) throw (RuntimeException)e;
        // abort file
        continue;
      }
      
      byte[] data;
      try {
        data = builder.finish();
      } catch(LogicException e) {
        System.err.println("Error generating code for " + filename + ":");
        System.err.println(e);
        continue;
      }
      
      OutputStream out = new FileOutputStream(filename + ".binary");
      out.write(data);
      out.close();
      time = System.currentTimeMillis() - time;
      
      
      System.out.printf("%s -> %s, %d bytes (%dms)\n",
                        filename, filename + ".binary",
                        data.length, time);
    }
  }
  
  public void include(String filename)
  throws IOException,AssemblyInputException {
    parse(filename);
  }
  
  private void parse(String filename) throws IOException, AssemblyInputException {
    Reader in = new InputStreamReader(new FileInputStream(filename), "UTF8");
    ParallaxLexer lexer = new ParallaxLexer(in);
    ParallaxParser parser = new ParallaxParser(builder, this);
    Iterable<Token> tokens;
    try {
      tokens = lexer.lex();
    } catch(ParseException e) {
      System.err.println(e);
      throw e;
    } catch(IOException e) {
      System.err.println("Error reading file " + filename);
      System.err.println(e.getMessage());
      throw e;
    } finally {
      in.close();
    }
    
    try {
      parser.parse(tokens);
    } catch(ParseException e) {
      System.err.println("Error parsing " + filename + ":");
      System.err.println(e);
      throw e;
    } catch(LogicException e) {
      System.err.println("Error processing " + filename + ":");
      System.err.println(e);
      throw e;
    }

  }
  
  public void includeBlob(String filename) throws IOException {
    FileInputStream in = new FileInputStream(filename);
	 try {
		 while (true) {
		 	int b = in.read();
			if (b == -1) break;
			builder.addByte((byte)b);
		 }
	 } finally {
		 in.close();
	 }
  }

  private void printUsage() {
    System.err.println("Usage: java <vm options> -jar propasm.jar " +
                "<flags> <input files>");
    System.err.println("Any number of input files may be specified, though " +
                "specifying zero\nwill get you this message.");
	 System.err.println("Flags:");
	 System.err.println(" -raw  Generate raw machine code without a bootloader.");
	 System.err.println("       The Propeller won't load the output directly;" +
	     " this is useful for making");
	 System.err.println("       a \"coglet\" to include in larger assembly " +
	     "programs.");
  }

  private boolean consumeSwitches(List<String> args, AssemblerConfig config) {
	  while (args.size() > 0 && args.get(0).startsWith("-")) {
		  String flag = args.remove(0);
		  if (flag.equals("-raw")) {
			  config.setGenerateBootloader(false);
		  } else {
			  System.err.println("Unrecognized flag: " + flag);
			  return false;
		  }
	  }
	  return true;
  }
  
}
