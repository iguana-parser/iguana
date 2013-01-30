package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class L0 extends GrammarSlot {

	private static L0 instance;
	
	public static L0 getInstance() {
		if(instance == null) {
			instance = new L0();
		}
		return instance;
	}
	
	
	private L0() {
		super(-1, "$");
	}


	@Override
	public void code(Writer writer) throws IOException {
		writer.append("case L0:\n");
		writer.append("if (!descriptorSet.isEmpty()) {\n");
		writer.append("Descriptor descriptor = descriptorSet.nextDescriptor();\n");
		writer.append("cu = descriptor.getGSSNode();\n");
		writer.append("ci = descriptor.getInputIndex();\n");
		writer.append("cn = descriptor.getSPPFNode();\n");
		writer.append("label = descriptor.getLabel().getId();\n");
		writer.append("break;\n");
		writer.append("} else {\n");
		writer.append("end = System.nanoTime();\n");
		writer.append("log(start, end);\n");
		writer.append("NonterminalSymbolNode root = lookup.getStartSymbol();\n");
		writer.append("if (root == null) {");
		writer.append("log.info(\"Parsing failed.\");\n");
		writer.append("throw new ParsingFailedException(errorNonterminal, errorIndex, input);\n");
		writer.append("}\n");
		writer.append("return root;\n");
		writer.append("}\n");
	}

}
