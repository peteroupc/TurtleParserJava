package com.upokecenter.rdf;

import java.util.Set;

public final class RDFUtility {
	private RDFUtility(){}
	
	/**
	 * A lax comparer of RDF triples which doesn't compare
	 * blank node labels
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean laxEqual(RDFTriple a, RDFTriple b){
		if(a==null)return (b==null);
		if(a.equals(b))return true;
		if(a.getSubject().getKind()!=b.getSubject().getKind())
			return false;
		if(a.getObject().getKind()!=b.getObject().getKind())
			return false;
		if(!a.getPredicate().equals(b.getPredicate()))
			return false;
		if(a.getSubject().getKind()!=RDFTerm.BLANK){
			if(!a.getSubject().equals(b.getSubject()))
				return false;			
		}
		if(a.getObject().getKind()!=RDFTerm.BLANK){
			if(!a.getObject().equals(b.getObject()))
				return false;			
		}
		return true;
	}
	
	public static boolean areIsomorphic(Set<RDFTriple> graph1, Set<RDFTriple> graph2){
		if(graph1==null)return graph2==null;
		if(graph1.equals(graph2))return true;
		// Graphs must have the same size to be isomorphic
		if(graph1.size()!=graph2.size())return false;
		boolean haveBlankNodes=false;
		for(RDFTriple triple : graph1){
			// do a strict comparison
			if(triple.getSubject().getKind()!=RDFTerm.BLANK &&
					triple.getObject().getKind()!=RDFTerm.BLANK){
				if(!graph2.contains(triple))
					return false;
			} else {
				// do a lax comparison
				boolean found=false;
				for(RDFTriple triple2 : graph2){
					if(laxEqual(triple,triple2)){
						found=true;
						break;
					}
				}
				if(!found)return false;
			}
		}
		if(!haveBlankNodes)return true;
		return true;
	}
}
