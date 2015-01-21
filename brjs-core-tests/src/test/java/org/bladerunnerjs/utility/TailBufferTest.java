package org.bladerunnerjs.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class TailBufferTest
{

	private TailBuffer unit;
	@Before
	public void setup()
	{
		 unit = new TailBuffer(9);
	}
	
	@Test
	public void testToArraySmallerThanBuffer() throws Exception
	{
		unit.push('a');
		unit.push('b');
		assertEquals("ab", new String(unit.toArray()));
		assertEquals(2, unit.size());
	}
	
	@Test
	public void testToArrayBiggerThanBuffer() throws Exception
	{
		unit.push('1');
		unit.push('2');
		unit.push('3');
		unit.push('4');
		unit.push('5');
		unit.push('6');
		unit.push('7');
		unit.push('8');
		unit.push('9');
		unit.push('0');
		assertEquals("234567890", new String(unit.toArray()));
		assertEquals(9, unit.size());
		assertEquals(9, unit.maxCapacity());
	}
	
}
