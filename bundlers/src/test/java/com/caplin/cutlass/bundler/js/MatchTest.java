package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import org.junit.Test;

public class MatchTest {

	@Test
	public void aMatchWithANodeThatIsAnAlias() {
		LetterNode root = new LetterNode();
		LetterNode aliasNode = root.getOrCreateNextNode( 'a' ).getOrCreateNextNode( '_' ).getOrCreateNextNode( 'g' );
		
		aliasNode.setAlias();
		
		Match match = new Match( 'a', root.find( 'a' ) );
		
		match.processNextCharacter( '_' );
		match.processNextCharacter( 'g' );
		
		assertTrue( match.isAlias() );
	}
}
