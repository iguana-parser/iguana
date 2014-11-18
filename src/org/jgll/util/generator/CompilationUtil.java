package org.jgll.util.generator;

import org.jgll.parser.GLLParser;

import com.google.common.truth.codegen.CompilingClassLoader;
import com.google.common.truth.codegen.CompilingClassLoader.CompilerException;

public class CompilationUtil {
	
	public static GLLParser getParser(String code) {
		Class<?> clazz = getClass("test", "Test", code);
		
		GLLParser parser = null;
		try {
			parser = (GLLParser) clazz.newInstance();
		} catch (IllegalAccessException | IllegalArgumentException
				| SecurityException | InstantiationException e) {
			e.printStackTrace();
		}
		
		return parser;
	}
	
	public static Class<?> getClass(String packageName, String className, String code) {
		
		Class<?> clazz = null;
		String fullClassName = packageName + "." + className;
			
		try {
			CompilingClassLoader compilingClassLoader = new CompilingClassLoader(CompilationUtil.class.getClassLoader(), fullClassName, code, null);
			clazz = compilingClassLoader.loadClass(fullClassName);
		} catch (CompilerException | ClassNotFoundException e) {
			throw new RuntimeException("Compiliation failed", e);
		}
		
		return clazz;
	}
		
}
