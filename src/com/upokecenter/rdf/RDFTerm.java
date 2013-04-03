package com.upokecenter.rdf;

public final class RDFTerm {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + kind;
		result = prime * result
				+ ((languageTag == null) ? 0 : languageTag.hashCode());
		result = prime * result + ((lexicalForm == null) ? 0 : lexicalForm.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RDFTerm other = (RDFTerm) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (kind != other.kind)
			return false;
		if (languageTag == null) {
			if (other.languageTag != null)
				return false;
		} else if (!languageTag.equals(other.languageTag))
			return false;
		if (lexicalForm == null) {
			if (other.lexicalForm != null)
				return false;
		} else if (!lexicalForm.equals(other.lexicalForm))
			return false;
		return true;
	}

	private static void escapeLanguageTag(String str, StringBuilder builder){
		int length=str.length();
		boolean hyphen=false;
		for(int i=0;i<length;i++){
			int c=str.charAt(i);
			if((c>='A' && c<='Z')){
				builder.append((char)(c+0x20));
			} else if(c>='a' && c<='z'){
				builder.append((char)c);
			} else if(hyphen && c>='0' && c<='9'){
				builder.append((char)c);
			} else if(c=='-'){
				builder.append((char)c);
				hyphen=true;
				if(i+1<length && str.charAt(i+1)=='-') {
					builder.append('x');
				}
			} else {
				builder.append('x');
			}
		}
	}

	private static void escapeBlankNode(String str, StringBuilder builder){
		int length=str.length();
		String hex="0123456789ABCDEF";
		for(int i=0;i<length;i++){
			int c=str.charAt(i);
			if((c>='A' && c<='Z') || (c>='a' && c<='z') ||
					(c>0 && c>='0' && c<='9')){
				builder.append((char)c);
			}
			else if(c>=0xD800 && c<=0xDBFF && i+1<length &&
					str.charAt(i+1)>=0xDC00 && str.charAt(i+1)<=0xDFFF){
				// Get the Unicode code point for the surrogate pair
				c=0x10000+(c-0xD800)*0x400+(str.charAt(i+1)-0xDC00);
				builder.append("U00");
				builder.append(hex.charAt((c>>20)&15));
				builder.append(hex.charAt((c>>16)&15));
				builder.append(hex.charAt((c>>12)&15));
				builder.append(hex.charAt((c>>8)&15));
				builder.append(hex.charAt((c>>4)&15));
				builder.append(hex.charAt((c)&15));
				i++;
			}
			else {
				builder.append("u");
				builder.append(hex.charAt((c>>12)&15));
				builder.append(hex.charAt((c>>8)&15));
				builder.append(hex.charAt((c>>4)&15));
				builder.append(hex.charAt((c)&15));
			}
		}
	}

	private static void escapeString(String str,
			StringBuilder builder, boolean uri){
		int length=str.length();
		String hex="0123456789ABCDEF";
		for(int i=0;i<length;i++){
			int c=str.charAt(i);
			if(c==0x09){
				builder.append("\\t");
			} else if(c==0x0a){
				builder.append("\\n");
			} else if(c==0x0d){
				builder.append("\\r");
			} else if(c==0x22){
				builder.append("\\\"");
			} else if(c==0x5c){
				builder.append("\\\\");
			} else if(uri && c=='>'){
				builder.append("%3E");
			} else if(c>=0x20 && c<=0x7E){
				builder.append((char)c);
			}
			else if(c>=0xD800 && c<=0xDBFF && i+1<length &&
					str.charAt(i+1)>=0xDC00 && str.charAt(i+1)<=0xDFFF){
				// Get the Unicode code point for the surrogate pair
				c=0x10000+(c-0xD800)*0x400+(str.charAt(i+1)-0xDC00);
				builder.append("\\U00");
				builder.append(hex.charAt((c>>20)&15));
				builder.append(hex.charAt((c>>16)&15));
				builder.append(hex.charAt((c>>12)&15));
				builder.append(hex.charAt((c>>8)&15));
				builder.append(hex.charAt((c>>4)&15));
				builder.append(hex.charAt((c)&15));
				i++;
			}
			else {
				builder.append("\\u");
				builder.append(hex.charAt((c>>12)&15));
				builder.append(hex.charAt((c>>8)&15));
				builder.append(hex.charAt((c>>4)&15));
				builder.append(hex.charAt((c)&15));
			}
		}
	}

	/**
	 * 
	 * Gets a string representation of this RDF term
	 * in N-Triples format.  The string will not end
	 * in a line break.
	 * 
	 */
	@Override
	public String toString(){
		StringBuilder builder=null;
		if(this.kind==BLANK){
			builder=new StringBuilder();
			builder.append("_:");
			escapeBlankNode(identifier,builder);
		} else if(this.kind==LANGSTRING){
			builder=new StringBuilder();
			builder.append("\"");
			escapeString(lexicalForm,builder,false);
			builder.append("\"@");
			escapeLanguageTag(languageTag,builder);
		} else if(this.kind==TYPEDSTRING){
			builder=new StringBuilder();
			builder.append("\"");
			escapeString(lexicalForm,builder,false);
			builder.append("\"");
			if(!"http://www.w3.org/2001/XMLSchema#string".equals(identifier)){
				builder.append("^^<");
				escapeString(identifier,builder,true);
				builder.append(">");
			}
		} else if(this.kind==IRI){
			builder=new StringBuilder();
			builder.append("<");
			escapeString(identifier,builder,true);
			builder.append(">");
		} else
			return "<about:blank>";
		return builder.toString();
	}
	/**
	 * A blank node.
	 */
	public static final int BLANK = 0; // type is blank node name, literal is blank
	/**
	 * An IRI (Internationalized Resource Identifier.)
	 */
	public static final int IRI = 1; // type is IRI, literal is blank
	/**
	 * A string with a language tag.
	 */
	public static final int LANGSTRING = 2; // literal is given
	/**
	 * A piece of data serialized to a string.
	 */
	public static final int TYPEDSTRING = 3; // type is IRI, literal is given
	private String identifier=null;
	private String languageTag=null;
	private String lexicalForm=null;
	/**
	 * 
	 * Gets the IRI or blank node label for this RDF
	 * literal. Supported by all kinds.
	 * 
	 * 
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * Gets the language tag for this RDF literal.
	 * Supported by the LANGSTRING kind.
	 * 
	 * 
	 */
	public String getLanguageTag() {
		return languageTag;
	}
	/**
	 * Gets the lexical form of an RDF literal.
	 * Supported in the LANGSTRING and TYPEDSTRING kinds.
	 * 
	 * 
	 */
	public String getLexicalForm() {
		return lexicalForm;
	}
	public int getKind() {
		return kind;
	}
	private int kind;
	public static RDFTerm fromTypedString(String str, String iri){
		if((str)==null)throw new NullPointerException("str");
		if((iri)==null)throw new NullPointerException("iri");
		if((iri).length()==0)throw new IllegalArgumentException("iri");
		RDFTerm ret=new RDFTerm();
		ret.kind=TYPEDSTRING;
		ret.identifier=iri;
		ret.lexicalForm=str;
		if(iri.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"))
			// this can't be a language string
			// because there is no language tag
			throw new IllegalArgumentException("iri");
		return ret;
	}
	public static RDFTerm fromBlankNode(String name){
		if((name)==null)throw new NullPointerException("name");
		if((name).length()==0)throw new IllegalArgumentException("name");
		RDFTerm ret=new RDFTerm();
		ret.kind=BLANK;
		ret.identifier=name;
		ret.lexicalForm=null;
		return ret;
	}
	public static RDFTerm fromIRI(String iri){
		if((iri)==null)throw new NullPointerException("iri");
		RDFTerm ret=new RDFTerm();
		ret.kind=IRI;
		ret.identifier=iri;
		ret.lexicalForm=null;
		return ret;
	}
	public static RDFTerm fromTypedString(String str) {
		return fromTypedString(str,"http://www.w3.org/2001/XMLSchema#string");
	}
	public static RDFTerm fromLangString(String str, String languageTag) {
		if((str)==null)throw new NullPointerException("str");
		if((languageTag)==null)throw new NullPointerException("languageTag");
		if((languageTag).length()==0)throw new IllegalArgumentException("languageTag");
		RDFTerm ret=new RDFTerm();
		ret.kind=LANGSTRING;
		ret.identifier="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
		ret.languageTag=languageTag;
		ret.lexicalForm=str;
		return ret;
	}
}