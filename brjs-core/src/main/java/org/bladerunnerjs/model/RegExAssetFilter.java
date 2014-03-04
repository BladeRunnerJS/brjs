package org.bladerunnerjs.model;

import java.util.regex.Pattern;

public class RegExAssetFilter implements AssetFilter {
	private final Pattern pattern;
	
	public RegExAssetFilter(String regularExpression) {
		pattern = Pattern.compile(regularExpression);
	}
	
	@Override
	public boolean accept(String assetName) {
		return pattern.matcher(assetName).matches();
	}
}
