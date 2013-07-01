package org.jgll.util.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class ParserLogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(record.getLevel().getName()).append(" : ");
		
		sb.append(record.getMessage());
		
		sb.append("\n");
		
		return sb.toString();
	}

}
