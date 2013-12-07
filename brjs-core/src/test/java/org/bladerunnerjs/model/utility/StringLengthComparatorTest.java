package org.bladerunnerjs.model.utility;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.bladerunnerjs.utility.StringLengthComparator;
import org.junit.Test;

import com.google.common.base.Joiner;

public class StringLengthComparatorTest {
	@Test
	public void longerStringsComeFirst() {
		assertEquals("xxx, xx, x", set("xxx", "xx", "x"));
		assertEquals("xxx, xx, x", set("x", "xx", "xxx"));
	}
	
	@Test
	public void longerStringsComeFirstEvenWhenAlphabeticallyTheyShouldOrderInReverse() {
		assertEquals("bb, a", set("a", "bb"));
		assertEquals("bb, a", set("bb", "a"));
	}
	
	@Test
	public void sameLengthStringsAreOrderedAlphabetically() {
		assertEquals("a, b, c", set("a", "b", "c"));
		assertEquals("a, b, c", set("c", "b", "a"));
	}
	
	private String set(String... args) {
		Set<String> items = new TreeSet<>(new StringLengthComparator());
		items.addAll(Arrays.asList(args));
		
		return Joiner.on(", ").join(items);
	}
}