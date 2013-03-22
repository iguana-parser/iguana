package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.Descriptor;
import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class L0 extends GrammarSlot {
	
	private static final Logger log = LoggerFactory.getLogger(L0.class);
	
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
	
	public GrammarSlot parse(GLLParser parser, Input input, GrammarSlot start) {
		
		GrammarSlot slot = start.parse(parser, input);
		
		while(slot != null) {
			slot = slot.parse(parser, input);
		}
		
		while(parser.hasNextDescriptor()) {
			Descriptor descriptor = parser.nextDescriptor();
			slot = descriptor.getLabel();
			GSSNode cu = descriptor.getGSSNode();
			SPPFNode cn = descriptor.getSPPFNode();
			int ci = descriptor.getInputIndex();
			parser.update(cu, cn, ci);
			log.trace("Processing ({}, {}, {}, {})", slot, ci, cu, cn);
			slot = slot.parse(parser, input);
		
			while(slot != null) {
				slot = slot.parse(parser, input);
			}
		}
		return null;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		while(parser.hasNextDescriptor()) {
			Descriptor descriptor = parser.nextDescriptor();
			GrammarSlot slot = descriptor.getLabel();
			GSSNode cu = descriptor.getGSSNode();
			SPPFNode cn = descriptor.getSPPFNode();
			int ci = descriptor.getInputIndex();
			parser.update(cu, cn, ci);
			log.trace("Processing ({}, {}, {}, {})", slot, ci, cu, cn);
			slot = slot.parse(parser, input);
			while(slot != null) {
				slot = slot.parse(parser, input);
			}
		}
		return null;
	}
	
	@Override
	public void recognize(GLLRecognizer recognizer, Input input, org.jgll.recognizer.GSSNode cu, int ci) {
		while(recognizer.hasNextDescriptor()) {
			org.jgll.recognizer.Descriptor descriptor = recognizer.nextDescriptor();
			GrammarSlot slot = descriptor.getLabel();
			cu = descriptor.getGSSNode();
			ci = descriptor.getInputIndex();
			log.trace("Processing ({}, {}, {})", slot, ci, cu);
			slot.recognize(recognizer, input, cu, ci);
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
