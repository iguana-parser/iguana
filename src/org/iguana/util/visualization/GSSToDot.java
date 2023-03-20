/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util.visualization;

import org.iguana.gss.GSSEdge;
import org.iguana.gss.GSSNode;
import org.iguana.utils.visualization.DotGraph;

import java.util.HashMap;
import java.util.Map;

import static org.iguana.utils.visualization.DotGraph.newEdge;
import static org.iguana.utils.visualization.DotGraph.newNode;

public class GSSToDot {

    private final Map<GSSNode<?>, Integer> ids = new HashMap<>();

    public DotGraph execute(Iterable<GSSNode<?>> set) {
        DotGraph dotGraph = new DotGraph();

        for (GSSNode<?> gssNode : set) {
            dotGraph.addNode(newNode(getId(gssNode), gssNode.toString()));

            for (GSSEdge<?> edge : gssNode.getGSSEdges()) {
                DotGraph.Edge dotEdge = newEdge(getId(gssNode), getId(edge.getDestination()));
                if (edge.getReturnSlot() != null) {
                    dotEdge.setLabel(edge.getReturnSlot().toString());
                }
                dotGraph.addEdge(dotEdge);
            }
        }

        return dotGraph;
    }

    private int getId(GSSNode<?> node) {
        return ids.computeIfAbsent(node, k -> ids.size() + 1);
    }

}
