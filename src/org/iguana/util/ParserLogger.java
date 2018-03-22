package org.iguana.util;

import iguana.utils.logging.IguanaLogger;
import iguana.utils.logging.JavaUtilIguanaLogger;
import iguana.utils.logging.LogLevel;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
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

    public ParserLogger() {
        Configuration config = Configuration.load();
        if (config.getLogLevel() == LogLevel.NONE)
            logger = IguanaLogger.DEFAULT;
        else
            logger = new JavaUtilIguanaLogger("Iguana Logger", config.getLogLevel());
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

    public void terminalNodeAdded() {
        countTerminalNodes++;
    }

    public void nonterminalNodeAdded() {
        countNonterminalNodes++;
    }

    public void intermediateNodeAdded() {
        countIntermediateNodes++;
    }

    public void packedNodeAdded() {
        countPackedNodes++;
    }

    public void ambiguousNodeAdded() {
        countAmbiguousNodes++;
    }

    public void gssNodeAdded(GSSNode node) {
        countGSSNodes++;
        logger.log("GSS node added %s", node);
    }

    public void gssEdgeAdded(GSSEdge edge) {
        countGSSEdges++;
        logger.log("GSS Edge added %s", edge);
    }

    public void descriptorAdded(Descriptor<?> descriptor) {
        logger.log("Descriptor created: %s", descriptor);
        descriptorsCount++;
    }

    public void log(String s) {
        logger.log(s);
    }

    public void log(String s, Object arg) {
        logger.log(s, arg);
    }

    public void log(String s, Object arg1, Object arg2) {
        logger.log(s, arg1, arg2);
    }

    public void log(String s, Object arg1, Object arg2, Object arg3) {
        logger.log(s, arg1, arg2, arg3);
    }

    public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
        logger.log(s, arg1, arg2, arg3, arg4);
    }

    public void log(String s, Object...args) {
        logger.log(s, args);
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
