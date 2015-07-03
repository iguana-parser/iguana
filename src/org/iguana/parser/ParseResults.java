package org.iguana.parser;

import org.iguana.util.BenchmarkUtil;
import static org.iguana.util.generator.GeneratorUtil.*;

public class ParseResults {

	public static String format(Iterable<ParseResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header());
		
		for (ParseResult r : results) {
			sb.append(r.getInput().getURI()).append(NewLine);
			if (r.isParseSuccess())
				sb.append(BenchmarkUtil.format(r.getInput(), r.asParseSuccess().getStatistics())).append(NewLine);
			else
				sb.append("Parse error " + r.asParseError());
		}
		return sb.toString();
	}
	
}
