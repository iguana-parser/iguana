package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.test.TrueFollowSet;
import org.jgll.grammar.slot.test.TruePredictionSet;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

/**
 * 
 * @author Ali Afroozeh
 * 
 * TODO: L0 is not really a grammar slot. Change it!
 *
 */
public class L0 extends HeadGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private static L0 instance;
	
	public static L0 getInstance() {
		if(instance == null) {
			instance = new L0();
		}
		return instance;
	}
	
	private L0() {
		super(-1, new Nonterminal("L0"), -1, new ArrayList<List<Symbol>>(), false, new TruePredictionSet(0), new TrueFollowSet());
	}
	
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer, HeadGrammarSlot start) {
		
		if(!start.test(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			return null;
		}
		
		GrammarSlot slot = start.parse(parser, lexer);
		
		while(slot != null) {
			slot = slot.parse(parser, lexer);
		}
		
		return parse(parser, lexer);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		while(parser.hasNextDescriptor()) {
			GrammarSlot slot = parser.nextDescriptor().getGrammarSlot();
			slot = slot.parse(parser, lexer);
			while(slot != null) {
				slot = slot.parse(parser, lexer);
			}
		}
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
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

}
