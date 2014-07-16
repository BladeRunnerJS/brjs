package org.bladerunnerjs.model;


/**
 * A Theme is an AssetLocation with a name, that is used to segregate stylistic Assets (e.g. CSS, images) etc. 
 */
public interface ThemedAssetLocation extends AssetLocation {
	public String getThemeName();
}
