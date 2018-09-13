package org.protege.editor.core.util;

import java.util.StringTokenizer;

public class Version {
	private int major = 0;
	private int minor = 0;
	private int micro = 0;
	private String qual = "";
	
	public Version(String vs) {
		StringTokenizer tok = new StringTokenizer(vs, ".");
		int cnt = 0;
		while (tok.hasMoreElements()) {
			String ns = tok.nextToken();
			switch (cnt) {
			case 0:
				major = Integer.parseInt(ns);
				break;
			case 1:
				minor = Integer.parseInt(ns);
				break;
			case 2:
				micro = Integer.parseInt(ns);
				break;
			case 3:
				qual = ns;
				break;
				
			}
			cnt++;
			
		}
	}
	
	public int getMajor() {return major;}
	public int getMinor() {return minor;}
	public int getMicro() {return micro;}
	public String getQualifier() {return qual;}
	
	public int num() {
		return (major * 100) + (minor * 10) + micro;
	}
	
	public int compareTo(Version ov) {
		if (num() < ov.num()) {
			return -1;
		} else if (num() == ov.num()) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public String toString() {
		StringBuilder vs = new StringBuilder();
		vs.append(major);
        vs.append(".");
        vs.append(minor);
        vs.append(".");
        vs.append(micro);
        
        String qualifier = getQualifier();
        if ((qualifier != null) && (!qualifier.isEmpty())){
            vs.append(".");
            vs.append(qualifier);
        }
        
        return vs.toString();
		
	}

}
