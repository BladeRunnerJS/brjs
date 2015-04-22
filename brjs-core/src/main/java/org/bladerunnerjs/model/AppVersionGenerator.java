package org.bladerunnerjs.model;


public interface AppVersionGenerator
{
	void setVersion(String version);
	String getVersion();
	String getVersionPattern();
}
