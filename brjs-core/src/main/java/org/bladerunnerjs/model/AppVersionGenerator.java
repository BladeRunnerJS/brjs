package org.bladerunnerjs.model;


public interface AppVersionGenerator
{
	String getProdVersion();
	String getDevVersion();
	String getVersionPattern();
}
