package org.iguana.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilParserLogger implements ParserLogger {
	
	private static Logger log = Logger.getLogger(JavaUtilParserLogger.class.getName());
	
	static {
		log.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		log.setLevel(Level.INFO);
		handler.setLevel(Level.INFO);
		handler.setFormatter(new ParserLogFormatter());
		log.addHandler(handler);		
	}

	@Override
	public void log(String s) {
		log.info(s);
	}

	@Override
	public void log(String s, Object... args) {
		log.info(String.format(s, args));
	}

	@Override
	public void log(String s, Object arg) {
		log.info(String.format(s, arg));
	}

	@Override
	public void log(String s, Object arg1, Object arg2) {
		log.info(String.format(s, arg1, arg2));		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3) {
		log.info(String.format(s, arg1, arg2, arg3));		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		log.info(String.format(s, arg1, arg2, arg3, arg4));
	}

}
