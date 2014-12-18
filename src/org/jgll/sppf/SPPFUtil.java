package org.jgll.sppf;

import org.jgll.util.Configuration;
import org.jgll.util.Hash;
import org.jgll.util.Equals;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupType.*;

/**
 *
 * 
 * @author Ali Afroozeh
 *
 */
public class SPPFUtil {

    private Hash<NonPackedNode> nonPackedNodeHash;
    private Equals<NonPackedNode> nonPackedNodeEquals;
    private Hash<PackedNode> packedNodeHash; 
    private Equals<PackedNode> packedNodeEquals;
    
    private static SPPFUtil instance;
    
    public static SPPFUtil getInstance() {
        if (instance == null) 
            throw new RuntimeException("Instance is not initialized yet.");
        return instance;
    }
    
    public static void init(Configuration config) {

    	Hash<NonPackedNode> nonPackedNodeHash;
        Equals<NonPackedNode> nonPackedNodeEquals;
        Hash<PackedNode> packedNodeHash;
        Equals<PackedNode> packedNodeEquals;
        
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
        
        if (config.getGSSType() == NEW) {
        	packedNodeHash = (node) -> node.hashCode();
        	packedNodeEquals = (node, other) -> node.equals(other);
        } else {
        	packedNodeHash = (node) -> config.getHashFunction().hash(node.getGrammarSlot().hashCode(), node.getPivot());
        	packedNodeEquals = (node, other) -> node.getGrammarSlot() == other.getGrammarSlot() &&
        			                            node.getPivot() == other.getPivot();
        }
        
        instance = new SPPFUtil(nonPackedNodeHash, nonPackedNodeEquals, packedNodeHash, packedNodeEquals);
        
    }
    
    private SPPFUtil(Hash<NonPackedNode> nonPackedNodeHash,
                     Equals<NonPackedNode> nonPackedNodeEquals,
                     Hash<PackedNode> packedNodeHash,
                     Equals<PackedNode> packedNodeEquals) {
        this.nonPackedNodeHash = nonPackedNodeHash;
        this.nonPackedNodeEquals = nonPackedNodeEquals;
        this.packedNodeHash = packedNodeHash;
        this.packedNodeEquals = packedNodeEquals;
    }
    
    public int hash(NonPackedNode node) { 
        return nonPackedNodeHash.hash(node);
    }
    
    public boolean equals(NonPackedNode node, NonPackedNode other) {
        return nonPackedNodeEquals.equals(node, other);
    }
    
    public int hash(PackedNode node) { 
        return packedNodeHash.hash(node);
    }
    
    public boolean equals(PackedNode node, PackedNode other) {
        return packedNodeEquals.equals(node, other);
    }
}
