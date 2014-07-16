package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;

public interface RootAssetLocation extends AssetLocation {
	void setRequirePrefix(String requirePrefix) throws ConfigException;
}
