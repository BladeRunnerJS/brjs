package org.bladerunnerjs.model;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.model.exception.ConfigException;

public interface RootAssetLocation extends AssetLocation {
	void setRequirePrefix(String requirePrefix) throws ConfigException;
}
