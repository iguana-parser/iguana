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

package org.iguana.util.logging;

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
		logger.setLevel(Level.FINEST);
		handler.setLevel(Level.FINEST);
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
