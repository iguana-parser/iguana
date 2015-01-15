package org.jgll.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A wrapper for java.util.logging, providing a similar interface to SLF4J.
 * 
 * @author Ali Afroozeh
 *
 */
public class LoggerWrapper {
	
	private Logger logger;
	
	private LoggerWrapper(Logger logger) {
		this.logger = logger;
		logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		logger.setLevel(Level.WARNING);
		handler.setLevel(Level.WARNING);
		handler.setFormatter(new ParserLogFormatter());
		logger.addHandler(handler);
	}

	public static <T> LoggerWrapper getLogger(Class<T> clazz) {
		return new LoggerWrapper(Logger.getLogger(clazz.getName()));
	}
	
	public void info(String s) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(s);
		}		
	}
	
	public void info(String s, Object arg) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(String.format(s, arg));
		}
	}
	
	public void info(String s, Object arg1, Object arg2) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(String.format(s, arg1, arg2));
		}
	}	
	
	public void info(String s, Object...args) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(String.format(s, args));
		}
	}
	
	public void warning(String s) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.warning(s);
		}
	}
	
	public void warning(String s, Object...args) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.warning(String.format(s, args));
		}
	}
	
	public void error(String s, Object...args) {
		if(logger.isLoggable(Level.SEVERE)) {
			logger.severe(String.format(s, args));
		}
	}
	
	public void debug(String s) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(s);
		}
	}

	public void debug(String s, Object...args) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(String.format(s, args));
		}
	}
	
	public void debug(String s, Object arg) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(String.format(s, arg));
		}
	}
	
	public void debug(String s, Object arg1, Object arg2) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(String.format(s, arg1, arg2));
		}
	}

	public void debug(String s, Object arg1, Object arg2, Object arg3) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(String.format(s, arg1, arg2, arg3));
		}
	}
	
	public void debug(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(String.format(s, arg1, arg2, arg3, arg4));
		}
	}	

	public void trace(String s) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(s);
		}
	}
	
	public void trace(String s, Object...args) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(String.format(s, args));
		}
	}
	
	public void trace(String s, Object arg) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(String.format(s, arg));
		}
	}

	public void trace(String s, Object arg1, Object arg2) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(String.format(s, arg1, arg2));
		}
	}

	public void trace(String s, Object arg1, Object arg2, Object arg3) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(String.format(s, arg1, arg2, arg3));
		}
	}	
	
	public void trace(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.finest(String.format(s, arg1, arg2, arg3, arg4));
		}
	}	
	
}
