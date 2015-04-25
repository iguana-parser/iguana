package org.jgll.util.generator;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
import org.jgll.parser.GLLParser;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

import com.google.common.truth.codegen.CompilingClassLoader;
import com.google.common.truth.codegen.CompilingClassLoader.CompilerException;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class ParserGenerator {
	
	public static GLLParser getParser(Grammar grammar, Input input, Configuration config) {
		
		Class<?> clazz = getClass("test", "Test", getParserCode(grammar, input, config));
		
		GLLParser parser = null;
		try {
			parser = (GLLParser) clazz.newInstance();
		} catch (IllegalAccessException | IllegalArgumentException
				| SecurityException | InstantiationException e) {
			e.printStackTrace();
		}
		
		return parser;
	}
	
	
	public static String getParserCode(Grammar grammar, Input input, Configuration config) {
		StringWriter writer = new StringWriter();
		// TODO: fix the generation
//		grammar.toGrammarGraph(input, config).generate(new  PrintWriter(writer));
		return writer.toString();
	}
	
	private static Class<?> getClass(String packageName, String className, String code) {
		
		Class<?> clazz = null;
		String fullClassName = packageName + "." + className;
			
		try {
			CompilingClassLoader compilingClassLoader = new CompilingClassLoader(ParserGenerator.class.getClassLoader(), fullClassName, code, null);
			clazz = compilingClassLoader.loadClass(fullClassName);
		} catch (CompilerException | ClassNotFoundException e) {
			throw new RuntimeException("Compiliation failed", e);
		}
		
		return clazz;
	}
		
}
