package org.bladerunnerjs.model;

import java.util.Date;


public class TimestampAppVersionGenerator implements AppVersionGenerator
{
	@Override
	public String getProdVersion()
	{
		return String.valueOf( new Date().getTime() );
	}
	
	@Override
	public String getDevVersion()
	{
		return "dev";
	}
}
