package org.bladerunnerjs.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class FixedLengthStringBuilderTest
{

	private FixedLengthStringBuilder builder;
	
	@Before
	public void setup()
	{
		 builder = new FixedLengthStringBuilder(9);
	}
	
	@Test
	public void testToArraySmallerThanBuffer() throws Exception
	{
		builder.append('a');
		builder.append('b');
		assertEquals("ab", builder.toString());
	}
	
	@Test
	public void testToArrayBiggerThanBuffer() throws Exception
	{
		builder.append('1');
		builder.append('2');
		builder.append('3');
		builder.append('4');
		builder.append('5');
		builder.append('6');
		builder.append('7');
		builder.append('8');
		builder.append('9');
		builder.append('0');
		assertEquals("234567890", builder.toString());
	}
	
}
