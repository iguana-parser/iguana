package org.jgll.recognizer;

public class RecognizerFactory {

	private static GLLRecognizer recognizer;
	
	
	public static GLLRecognizer contextFreeRecognizer() {
		if(recognizer == null) {
			recognizer = new GrammarInterpreterRecognizer();
			return recognizer;
		}
		return recognizer;
	}
	
	
}
