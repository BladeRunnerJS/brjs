package org.bladerunnerjs.core.log;

public enum LoggerType
{
	CORE("core"),
	BUNDLER("bundler"),
	APP_SERVER("appserver"),
	TRANSFORM("transform"),
	COMMAND("command"), 
	UTIL("util"),
	FILTER("filter"), 
	REST_API("restapi"), 
	MINIFIER("minifier"), 
	SERVLET("servlet"), 
	DATABASE("database"), 
	DATABASE_UTIL("database.util"), 
	OBSERVER("observer");

	private static final String NAME_PREFIX = "brjs";
	private final String name;

	private LoggerType()
	{
		this("");
	}

	private LoggerType(String name)
	{
		String prepend = NAME_PREFIX;
		if (!name.equals(""))
		{
			prepend += ".";
		}
		this.name = prepend + name;
	}

	public String toString()
	{
		return name;
	}

	public String getTypedLoggerName(Class<?> clazz)
	{
		return toString() + "." + clazz.getSimpleName();
	}
	
}
