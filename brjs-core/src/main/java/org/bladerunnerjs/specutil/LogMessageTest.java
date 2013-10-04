package org.bladerunnerjs.specutil;

import org.bladerunnerjs.testing.utility.LogMessage;
import org.junit.Assert;
import org.junit.Test;



public class LogMessageTest
{
	@Test
	public void setEqualsMethodOnlyChecksFieldsAndNotObjectInstance()
	{
		Assert.assertEquals( new LogMessage("a message", (Object[])new String[]{"a","b"}), new LogMessage("a message", (Object[])new String[]{"a","b"}) );
	}
}
