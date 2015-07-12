package org.iguana.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilParserLogger implements ParserLogger {
	
	private boolean enabled = false;
	
	private Logger log = Logger.getLogger(JavaUtilParserLogger.class.getName());
	
	public JavaUtilParserLogger() {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String s, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String s, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		// TODO Auto-generated method stub
		
	}


}
