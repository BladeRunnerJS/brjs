package org.bladerunnerjs.model.engine;

import org.bladerunnerjs.model.exception.PropertiesException;


public interface NodeProperties
{
	public void setProperty(String name, String value) throws PropertiesException;

	public String getProperty(String name) throws PropertiesException;
}
