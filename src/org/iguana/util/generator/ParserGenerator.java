/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util.generator;

import java.io.StringWriter;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.parser.Iguana;
import org.iguana.util.Configuration;

import com.google.common.truth.codegen.CompilingClassLoader;
import com.google.common.truth.codegen.CompilingClassLoader.CompilerException;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class ParserGenerator {
	
	public static Iguana getParser(Grammar grammar, Input input, Configuration config) {
		
		Class<?> clazz = getClass("test", "Test", getParserCode(grammar, input, config));
		
		Iguana parser = null;
		try {
			parser = (Iguana) clazz.newInstance();
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
