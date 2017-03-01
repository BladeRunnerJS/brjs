package org.bladerunnerjs.model;


public interface AppVersionGenerator
{
	void appendTimetamp(boolean appendTimetamp);
	void setVersion(String version);
	String getVersion();
	String getVersionPattern();
}
