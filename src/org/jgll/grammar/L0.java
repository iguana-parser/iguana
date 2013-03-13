package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.lookup.LookupTable;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GLLParser;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class L0 extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private static L0 instance;
	
	public static L0 getInstance() {
		if(instance == null) {
			instance = new L0();
		}
		return instance;
	}
	
	private L0() {
		super(-1);
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		LookupTable lookupTable = parser.getLookupTable();
		while(lookupTable.hasNextDescriptor()) {
			Descriptor descriptor = lookupTable.nextDescriptor();
			GLLParser.log.trace("Processing {}: ", descriptor);
			parser.setCN(descriptor.getSPPFNode());
			parser.setCU(descriptor.getGSSNode());
			parser.setInputIndex(descriptor.getInputIndex());
			GrammarSlot slot = descriptor.getLabel();
			slot.execute(parser);
		}
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("case L0:\n");
		writer.append("if (lookupTable.hasNextDescriptor()) {\n");
		writer.append("Descriptor descriptor = lookupTable.nextDescriptor();\n");
		writer.append("log.debug(\"Processing {}\", descriptor);");
		writer.append("cu = descriptor.getGSSNode();\n");
		writer.append("ci = descriptor.getInputIndex();\n");
		writer.append("cn = descriptor.getSPPFNode();\n");
		writer.append("label = descriptor.getLabel().getId();\n");
		writer.append("break;\n");
		writer.append("} else {\n");
		writer.append("end = System.nanoTime();\n");
		writer.append("log(start, end);\n");
		writer.append("NonterminalSymbolNode root = lookupTable.getStartSymbol(startSymbol);\n");
		writer.append("if (root == null) {");
		writer.append("log.info(\"Parsing failed.\");\n");
		writer.append("throw new ParseError(errorSlot, errorIndex);\n");
		writer.append("}\n");
		writer.append("return root;\n");
		writer.append("}\n");
	}
	
	@Override
	public String toString() {
		return "L0";
	}

	@Override
	public String getName() {
		return "L0";
	}

}
