package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

public class SuffixAssetFilter implements AssetFilter {
	private List<String> suffixes;
	
	public SuffixAssetFilter(String suffix) {
		suffixes = new ArrayList<>();
		suffixes.add(suffix);
	}
	
	public SuffixAssetFilter(List<String> suffixes) {
		this.suffixes = suffixes;
	}
	
	@Override
	public boolean accept(String assetName) {
		boolean matchesSuffixes = false;
		
		for(String suffix : suffixes) {
			if(assetName.endsWith(suffix)) {
				matchesSuffixes = true;
				break;
			}
		}
		
		return matchesSuffixes;
	}
}
