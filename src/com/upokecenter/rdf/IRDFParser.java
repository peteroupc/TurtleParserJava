package com.upokecenter.rdf;

import java.io.IOException;
import java.util.Set;

public interface IRDFParser {
	public Set<RDFTriple> parse() throws IOException;
}
