package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.bladerunnerjs.utility.trie.Trie;
import org.bladerunnerjs.utility.trie.TrieLockedException;
import org.bladerunnerjs.utility.trie.exception.EmptyTrieKeyException;
import org.bladerunnerjs.utility.trie.exception.TrieKeyAlreadyExistsException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


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
	}
	
}
