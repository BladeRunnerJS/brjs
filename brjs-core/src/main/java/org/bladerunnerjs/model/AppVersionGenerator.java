package org.bladerunnerjs.model;


public interface AppVersionGenerator
{
	void setProdVersion(String version);
	String getProdVersion();
	void setDevVersion(String version);
	String getDevVersion();
	String getVersionPattern();
}
