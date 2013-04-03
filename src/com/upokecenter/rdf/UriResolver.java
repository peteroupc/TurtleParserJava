package com.upokecenter.rdf;

final class UriResolver {
	private UriResolver(){}
	private static int[] splitUri(String ref){
		int index=0;
		int[] ret=new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		if(ref==null || ref.length()==0)return ret;
		boolean scheme=false;
		// scheme
		while(index<ref.length()){
			int c=ref.charAt(index);
			if(index>0 && c==':'){
				scheme=true;
				ret[0]=0;
				ret[1]=index;
				index++;
				break;
			}
			if(index==0 && !((c>='a' && c<='z') || (c>='A' && c<='Z'))){
				index++;
				break;
			}
			else if(index>0 && !((c>='a' && c<='z') || (c>='A' && c<='Z') || (c>='0' && c<='9') ||
					c=='+' && c=='-' && c=='.')){
				index++;
				break;
			}

			index++;
		}
		if(!scheme) {
			index=0;
		}
		if(index+2<=ref.length() && ref.charAt(index)=='/' && ref.charAt(index+1)=='/'){
			// authority
			ret[2]=index+2;
			ret[3]=ref.length();
			index+=2;
			while(index<ref.length()){
				int c=ref.charAt(index);
				if(c=='/' || c=='?' || c=='#'){
					ret[3]=index;
					break;
				}
				index++;
			}
		}
		// path
		ret[4]=index;
		ret[5]=ref.length();
		while(index<ref.length()){
			int c=ref.charAt(index);
			if(c=='?' || c=='#'){
				ret[5]=index;
				break;
			}
			index++;
		}
		if(ret[4]>ret[5]){
			ret[4]=0;
			ret[5]=0;
		}
		// query
		ret[6]=index+1;
		ret[7]=ref.length();
		while(index<ref.length()){
			int c=ref.charAt(index);
			if(c=='#'){
				ret[7]=index;
				break;
			}
			index++;
		}
		// fragment
		ret[8]=index+1;
		ret[9]=ref.length();
		if(ret[6]>ret[7]){
			ret[6]=-1;
			ret[7]=-1;
		}
		if(ret[8]>ret[9]){
			ret[8]=-1;
			ret[9]=-1;
		}
		return ret;
	}

	private static void appendScheme(
			StringBuilder builder, String ref, int[] segments){
		if(segments[0]>=0){
			builder.append(ref.substring(segments[0],segments[1]));
			builder.append(':');
		}
	}
	private static void appendAuthority(
			StringBuilder builder, String ref, int[] segments){
		if(segments[2]>=0){
			builder.append("//");
			builder.append(ref.substring(segments[2],segments[3]));
		}
	}
	private static void appendPath(
			StringBuilder builder, String ref, int[] segments){
		builder.append(ref.substring(segments[4],segments[5]));
	}
	private static void appendQuery(
			StringBuilder builder, String ref, int[] segments){
		if(segments[6]>=0){
			builder.append('?');
			builder.append(ref.substring(segments[6],segments[7]));
		}
	}
	private static void appendFragment(
			StringBuilder builder, String ref, int[] segments){
		if(segments[8]>=0){
			builder.append('#');
			builder.append(ref.substring(segments[8],segments[9]));
		}
	}

	private static String pathParent(String ref, int startIndex, int endIndex){
		if(startIndex>endIndex)return "";
		endIndex--;
		while(endIndex>=startIndex){
			if(ref.charAt(endIndex)=='/')
				return ref.substring(startIndex,endIndex+1);
			endIndex--;
		}
		return "";
	}

	private static String normalizePath(String path){
		int len=path.length();
		if(len==0 || path.equals("..") || path.equals("."))
			return "";
		if(path.indexOf("/.")<0 && path.indexOf("./")<0)
			return path;
		StringBuilder builder=new StringBuilder();
		int index=0;
		while(index<len){
			char c=path.charAt(index);
			if((index+3<=len && c=='/' &&
					path.charAt(index+1)=='.' &&
					path.charAt(index+2)=='/') ||
					(index+2==len && c=='.' &&
					path.charAt(index+1)=='.')){
				// begins with "/./" or is "..";
				// move index by 2
				index+=2;
				continue;
			} else if((index+3<=len && c=='.' &&
					path.charAt(index+1)=='.' &&
					path.charAt(index+2)=='/')){
				// begins with "../";
				// move index by 3
				index+=3;
				continue;
			} else if((index+2<=len && c=='.' &&
					path.charAt(index+1)=='/') ||
					(index+1==len && c=='.')){
				// begins with "./" or is ".";
				// move index by 1
				index+=1;
				continue;
			} else if(index+2==len && c=='/' &&
					path.charAt(index+1)=='.'){
				// is "/."; append '/' and break
				builder.append('/');
				break;
			} else if((index+3==len && c=='/' &&
					path.charAt(index+1)=='.' &&
					path.charAt(index+2)=='.')){
				// is "/.."; remove last segment,
				// append "/" and return
				int index2=builder.length()-1;
				while(index2>=0){
					if(builder.charAt(index2)=='/'){
						break;
					}
					index2--;
				}
				if(index2<0) {
					index2=0;
				}
				builder.setLength(index2);
				builder.append('/');
				break;
			} else if((index+4<=len && c=='/' &&
					path.charAt(index+1)=='.' &&
					path.charAt(index+2)=='.' &&
					path.charAt(index+3)=='/')){
				// begins with "/../"; remove last segment
				int index2=builder.length()-1;
				while(index2>=0){
					if(builder.charAt(index2)=='/'){
						break;
					}
					index2--;
				}
				if(index2<0) {
					index2=0;
				}
				builder.setLength(index2);
				index+=3;
				continue;
			} else {
				builder.append(c);
				index++;
			}
		}
		return builder.toString();
	}
	private static void appendNormalizedPath(
			StringBuilder builder, String ref, int[] segments){
		builder.append(normalizePath(ref.substring(segments[4],segments[5])));
	}

	public static boolean hasScheme(String ref){
		int[] segments=splitUri(ref);
		return segments[0]>=0;
	}

	public static String relativeResolve(String ref, String base){
		int[] segments=splitUri(ref);
		int[] segmentsBase=splitUri(base);
		StringBuilder builder=new StringBuilder();
		if(segments[0]>=0){
			appendScheme(builder,ref,segments);
			appendAuthority(builder,ref,segments);
			appendNormalizedPath(builder,ref,segments);
			appendQuery(builder,ref,segments);
			appendFragment(builder,ref,segments);
		} else if(segments[2]>=0){
			appendScheme(builder,base,segmentsBase);
			appendAuthority(builder,ref,segments);
			appendNormalizedPath(builder,ref,segments);
			appendQuery(builder,ref,segments);
			appendFragment(builder,ref,segments);
		} else if(segments[4]==segments[5]){
			appendScheme(builder,base,segmentsBase);
			appendAuthority(builder,base,segmentsBase);
			appendPath(builder,base,segmentsBase);
			if(segments[6]>=0){
				appendQuery(builder,ref,segments);
			} else {
				appendQuery(builder,base,segmentsBase);
			}
			appendFragment(builder,ref,segments);
		} else {
			appendScheme(builder,base,segmentsBase);
			appendAuthority(builder,base,segmentsBase);
			if(segments[4]<segments[5] && ref.charAt(segments[4])=='/'){
				appendNormalizedPath(builder,ref,segments);
			} else {
				StringBuilder merged=new StringBuilder();
				if(segmentsBase[2]>=0 && segmentsBase[4]==segments[5]){
					merged.append('/');
					appendPath(merged,ref,segments);
					builder.append(normalizePath(merged.toString()));
				} else {
					merged.append(pathParent(base,segmentsBase[4],segmentsBase[5]));
					appendPath(merged,ref,segments);
					builder.append(normalizePath(merged.toString()));
				}
			}
			appendQuery(builder,ref,segments);
			appendFragment(builder,ref,segments);
		}
		return builder.toString();
	}
}
