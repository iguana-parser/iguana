package org.iguana.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;

import org.iguana.grammar.Grammar;


public class GrammarUtil {

	public static void save(Grammar grammar, URI path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		out.writeObject(grammar);
		out.close();		
	}
	
	public static Grammar load(URI path) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(path))));
		Grammar grammar = (Grammar) in.readObject();
		in.close();
		return grammar;
	}
	
}
