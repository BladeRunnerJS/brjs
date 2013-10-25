package org.bladerunnerjs.testing.utility;

import java.util.Arrays;

import com.google.common.base.Joiner;


public class LogMessage
{
	final String message;
	final Object[] params;
	
	public LogMessage(String message, Object... params)
	{
		this.message = message;
		this.params = params;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		LogMessage m = (LogMessage) o;
		return ( message.equals(m.message) && Arrays.equals(params, m.params) );
	}
	
	@Override
	public String toString()
	{
		return "\"" + message + "\" with params [" + Joiner.on(", ").join(params) + "]";
	}
}
