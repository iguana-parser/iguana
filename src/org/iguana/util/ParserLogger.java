package org.iguana.util;

import iguana.utils.logging.IguanaLogger;
import iguana.utils.logging.JavaUtilIguanaLogger;
import iguana.utils.logging.LogLevel;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.gss.GSSEdge;
import org.iguana.gss.GSSNode;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.Result;
import org.iguana.sppf.*;

public class ParserLogger {

    private static ParserLogger instance = new ParserLogger();

    public static ParserLogger getInstance() {
        return instance;
    }

    private int descriptorsCount;

    private int countNonterminalNodes;

    private int countIntermediateNodes;

    private int countTerminalNodes;

    private int countPackedNodes;

    private int countAmbiguousNodes;

    private int countGSSNodes;

    private int countGSSEdges;

    private final IguanaLogger logger;

    private boolean logEnabled = false;

    public ParserLogger() {
        Configuration config = Configuration.load();
        if (config.getLogLevel() == LogLevel.NONE)
            logger = IguanaLogger.DEFAULT;
        else
            logger = new JavaUtilIguanaLogger("IguanaParser Logger", config.getLogLevel());
    }

    public void reset() {
        descriptorsCount = 0;
        countNonterminalNodes = 0;
        countIntermediateNodes = 0;
        countTerminalNodes = 0;
        countPackedNodes = 0;
        countAmbiguousNodes = 0;
        countGSSNodes = 0;
        countGSSEdges = 0;
    }

    public void enable() {
        logEnabled = true;
    }

    public void terminalNodeAdded(TerminalNode node) {
        countTerminalNodes++;
        if (logEnabled) logger.log("Terminal node added %s", node);
    }

    public void nonterminalNodeAdded(NonterminalNode node) {
        countNonterminalNodes++;
        if (logEnabled) logger.log("Nonterminal node added %s", node);
    }

    public void intermediateNodeAdded(IntermediateNode node) {
        countIntermediateNodes++;
        if (logEnabled) logger.log("Intermediate node added %s", node);
    }

    public void packedNodeAdded(PackedNode packedNode) {
        countPackedNodes++;
        if (logEnabled) logger.log("Packed node added %s", packedNode);
    }

    public void ambiguousNodeAdded(NonPackedNode node) {
        countAmbiguousNodes++;
        if (logEnabled) logger.log("Ambiguous node added: %s", node);
    }

    public void gssNodeAdded(GSSNode<?> node, Object[] data) {
        countGSSNodes++;
        if (logEnabled) {
            if (data != null) {
                logger.log("GSS node added %s(%s)", node, data);
            } else {
                logger.log("GSS node added %s", node, data);
            }
        }
    }

    public void gssEdgeAdded(GSSEdge<?> edge) {
        countGSSEdges++;
        if (logEnabled) logger.log("GSS Edge added %s", edge);
    }

    public void descriptorAdded(Descriptor<?> descriptor) {
        descriptorsCount++;
        if (logEnabled)
            logger.log("Descriptor created: %s", descriptor);
    }

    public <T extends Result> void pop(GSSNode<T> gssNode, int inputIndex, T child, Object value) {
        if (logEnabled) logger.log("Pop %s, %d, %s, %s", gssNode, inputIndex, child, value);
    }

    public void error(GrammarSlot slot, int i, String errorDescription) {
        if (logEnabled) logger.log("Error recorded at %s %d %s", slot, i, errorDescription);
    }

    public <T extends Result> void processDescriptor(Descriptor<T> descriptor) {
        if (logEnabled) logger.log("Processing %s", descriptor);
    }

    public int getDescriptorsCount() {
        return descriptorsCount;
    }

    public int getCountNonterminalNodes() {
        return countNonterminalNodes;
    }

    public int getCountIntermediateNodes() {
        return countIntermediateNodes;
    }

    public int getCountTerminalNodes() {
        return countTerminalNodes;
    }

    public int getCountPackedNodes() {
        return countPackedNodes;
    }

    public int getCountAmbiguousNodes() {
        return countAmbiguousNodes;
    }

    public int getCountGSSNodes() {
        return countGSSNodes;
    }

    public int getCountGSSEdges() {
        return countGSSEdges;
    }

}
