package org.jgll.sppf;

import static org.jgll.util.Configuration.LookupType.*;

import org.jgll.util.Configuration;
import org.jgll.util.Equals;
import org.jgll.util.Hash;

/**
 *
 * 
 * @author Ali Afroozeh
 *
 */
public class SPPFUtil {

    private Hash<NonPackedNode> nonPackedNodeHash;
    private Equals<NonPackedNode> nonPackedNodeEquals;
    
    private static SPPFUtil instance;
    
    public static SPPFUtil getInstance() {
        if (instance == null) 
            throw new RuntimeException("Instance is not initialized yet.");
        
        return instance;
    }
    
    public static void init(Configuration config) {

    	Hash<NonPackedNode> nonPackedNodeHash;
        Equals<NonPackedNode> nonPackedNodeEquals;
        
        if (config.getLookupType() == MAP_GLOBAL) {
        	nonPackedNodeHash = (node) -> config.getHashFunction().hash(node.getGrammarSlot().hashCode(), 
        															    node.getLeftExtent(), 
        															    node.getRightExtent());
        	
        	nonPackedNodeEquals = (node, other) ->  node.getGrammarSlot() == other.getGrammarSlot() && 
        			                                node.getLeftExtent() == other.getLeftExtent() && 
        			                                node.getRightExtent() == other.getRightExtent();
        } else {
        	nonPackedNodeHash = (node) -> config.getHashFunction().hash(node.getLeftExtent(), 
																	    node.getRightExtent());

			nonPackedNodeEquals = (node, other) ->  node.getLeftExtent() == other.getLeftExtent() && 
													node.getRightExtent() == other.getRightExtent();       	
        }
        
        instance = new SPPFUtil(nonPackedNodeHash, nonPackedNodeEquals);
        
    }
    
    private SPPFUtil(Hash<NonPackedNode> nonPackedNodeHash, Equals<NonPackedNode> nonPackedNodeEquals) {
        this.nonPackedNodeHash = nonPackedNodeHash;
        this.nonPackedNodeEquals = nonPackedNodeEquals;
    }
    
    public int hash(NonPackedNode node) { 
        return nonPackedNodeHash.hash(node);
    }
    
    public boolean equals(NonPackedNode node, NonPackedNode other) {
        return nonPackedNodeEquals.equals(node, other);
    }
}
