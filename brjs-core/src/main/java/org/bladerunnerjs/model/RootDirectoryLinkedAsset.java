package org.bladerunnerjs.model;

import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.model.exception.ConfigException;


//TODO: get rid of this interface
public interface RootDirectoryLinkedAsset extends LinkedAsset
{
	void setRequirePrefix(String requirePrefix) throws ConfigException;
}
