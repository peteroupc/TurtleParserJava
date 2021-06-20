/*
Written in 2013 by Peter Occil.
Any copyright to this work is released to the Public Domain.
In case this is not possible, this work is also
licensed under Creative Commons Zero (CC0):
https://creativecommons.org/publicdomain/zero/1.0/

*/
package com.upokecenter.rdf;

import java.io.IOException;
import java.util.Set;

public interface IRDFParser {
  public Set<RDFTriple> parse() throws IOException;
}
