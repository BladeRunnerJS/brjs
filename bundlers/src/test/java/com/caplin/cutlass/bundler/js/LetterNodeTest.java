package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import org.junit.Test;

public class LetterNodeTest {

	@Test
	public void letterNodeCanBeAlias() {
		LetterNode letterNode = new LetterNode();
		
		letterNode.setAlias();
		
		assertTrue( letterNode.isAlias() );
	}
}
