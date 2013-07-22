package org.jgll.recognizer;

public class RecognizerFactory {

	private static GLLRecognizer contextFreeRecognizer;
	private static GLLRecognizer prefixRecognizer;
	
	public static GLLRecognizer contextFreeRecognizer() {
		if(contextFreeRecognizer == null) {
			contextFreeRecognizer = new InterpretedGLLRecognizer();
			return contextFreeRecognizer;
		}
		return contextFreeRecognizer;
	}
	
	public static GLLRecognizer prefixContextFreeRecognizer() {
		if(prefixRecognizer == null) {
			prefixRecognizer = new PrefixGLLRecognizer();
			return prefixRecognizer;
		}
		return prefixRecognizer;
	}
	
}
