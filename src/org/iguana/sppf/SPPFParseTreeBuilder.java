package org.iguana.sppf;

import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.RuleNode;
import org.iguana.traversal.SPPFVisitor;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class SPPFParseTreeBuilder {

    public void build(NonterminalNode node, ParseTreeBuilder parseTreeBuilder) {

    }

    static class SPPFVisitorImpl implements SPPFVisitor<List<ParseTreeNode>> {

        private final ParseTreeBuilder parseTreeBuilder;
        private final Input input;

        public SPPFVisitorImpl(ParseTreeBuilder parseTreeBuilder, Input input) {
            this.parseTreeBuilder = parseTreeBuilder;
            this.input = input;
        }

        @Override
        public List<ParseTreeNode> visit(TerminalNode node) {
            List<ParseTreeNode> list = new ArrayList<>();
            list.add(new org.iguana.parsetree.TerminalNode() {
                @Override
                public Terminal definition() {
                    return node.getGrammarSlot().getTerminal();
                }

                @Override
                public int start() {
                    return node.getLeftExtent();
                }

                @Override
                public int end() {
                    return node.getRightExtent();
                }

                @Override
                public String text() {
                    return input.subString(start(), end());
                }
            });
            return list;
        }

        @Override
        public List<ParseTreeNode> visit(NonterminalNode node) {
            if (node.isAmbiguous()) {

            } else {
                switch (node.getGrammarSlot().getNodeType()) {
                    case Basic:
                        List<ParseTreeNode> children = new ArrayList<>();
                        node.getChildren();

                        parseTreeBuilder.visit(new RuleNode() {

                            @Override
                            public ParseTreeNode childAt(int i) {
                                return children.get(i);
                            }

                            @Override
                            public ParseTreeNode childWithName(String name) {
                                return null;
                            }

                            @Override
                            public Rule rule() {
                                throw new RuntimeException("Not yet implemented");
                            }

                            @Override
                            public Iterable<ParseTreeNode> children() {
                                return children;
                            }

                            @Override
                            public int start() {
                                return node.getLeftExtent();
                            }

                            @Override
                            public int end() {
                                return node.getRightExtent();
                            }

                            @Override
                            public String text() {
                                return input.subString(start(), end());
                            }
                        });
                }
            }
            return null;
        }

        @Override
        public List<ParseTreeNode> visit(IntermediateNode node) {
            return null;
        }

        @Override
        public List<ParseTreeNode> visit(PackedNode node) {
            return null;
        }
    }

}


