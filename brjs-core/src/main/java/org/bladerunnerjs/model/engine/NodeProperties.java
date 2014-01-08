package org.bladerunnerjs.model.engine;

import org.bladerunnerjs.model.exception.PropertiesException;


public interface NodeProperties
{
	public void setPersisentProperty(String name, String value) throws PropertiesException;

	public String getPersisentProperty(String name) throws PropertiesException;
}
