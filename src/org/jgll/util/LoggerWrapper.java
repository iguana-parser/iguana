package org.jgll.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * A wrapper for java.util.logging, providing the same feeling as the SLF4J.
 * 
 * @author Ali Afroozeh
 *
 */
public class LoggerWrapper {
	
	private Logger logger;
	
	private LoggerWrapper(Logger logger) {
		this.logger = logger;
	}

	public static <T> LoggerWrapper getLogger(Class<T> clazz) {
		return new LoggerWrapper(Logger.getLogger(clazz.getName()));
	}
	
	public void info(String s, Object...args) {
		if(logger.isLoggable(Level.INFO)) {
			logger.info(String.format(s, args));
		}
	}
	
	public void warning(String s, Object...args) {
		if(logger.isLoggable(Level.WARNING)) {
			logger.info(String.format(s, args));
		}
	}
	
	public void error(String s, Object...args) {
		if(logger.isLoggable(Level.SEVERE)) {
			logger.info(String.format(s, args));
		}
	}

	public void debug(String s, Object...args) {
		if(logger.isLoggable(Level.FINE)) {
			logger.info(String.format(s, args));
		}
	}
	
	public void trace(String s, Object...args) {
		if(logger.isLoggable(Level.FINEST)) {
			logger.info(String.format(s, args));
		}
	}
	
	
	
}
