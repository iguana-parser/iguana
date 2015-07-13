package org.iguana.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilParserLogger implements ParserLogger {
	
	private boolean enabled = false;
	
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
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void log(String s) {
		if (enabled) log.info(s);
	}

	@Override
	public void log(String s, Object... args) {
		if (enabled) log.info(String.format(s, args));
	}

	@Override
	public void log(String s, Object arg) {
		if (enabled) log.info(String.format(s, arg));
	}

	@Override
	public void log(String s, Object arg1, Object arg2) {
		if (enabled) log.info(String.format(s, arg1, arg2));		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3) {
		if (enabled) log.info(String.format(s, arg1, arg2, arg3));		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		if (enabled) log.info(String.format(s, arg1, arg2, arg3, arg4));
	}

}
