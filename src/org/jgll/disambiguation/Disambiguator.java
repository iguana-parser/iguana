package org.jgll.disambiguation;

import org.jgll.exception.DisambiguationFailedException;
import org.jgll.sppf.SPPFNode;

public interface Disambiguator {

	public void disambiguate(SPPFNode node) throws DisambiguationFailedException;

	public int getNumberOfAmbiguosNodes();

	public void addDisambiguationPattern(DisambiguationRule pattern);
	
}
