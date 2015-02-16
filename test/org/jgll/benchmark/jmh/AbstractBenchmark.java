package org.jgll.benchmark.jmh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;
import org.jgll.util.GrammarUtil;

public class AbstractBenchmark {

	protected static List<File> find(String dir, String ext) throws IOException {
		List<File> inputs = new ArrayList<>();
		Collection<?> files = FileUtils.listFiles(new File(dir), new String[] {ext}, true);
		Iterator<?> it = files.iterator();
		while(it.hasNext()) {
			inputs.add((File) it.next());							
		}
		return inputs;
	}
	
	protected static Grammar getGrammar(String path) {
		try {
			return GrammarUtil.load(new File(path).toURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
