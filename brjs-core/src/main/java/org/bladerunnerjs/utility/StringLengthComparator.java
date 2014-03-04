package org.bladerunnerjs.utility;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String> {
	@Override
	public int compare(String s1, String s2) {
		int lengthComparison = s2.length() - s1.length();
		
		return (lengthComparison != 0) ? lengthComparison : s1.compareTo(s2);
	}
}
