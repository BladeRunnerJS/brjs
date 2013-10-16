package com.caplin.cutlass.bundler.js;

import java.io.IOException;

import com.caplin.cutlass.util.Utils;

public class CurrentJsBundlerLine {

	private static final String THIRDPARTY_REGEX = ".*(br|caplin)\\.thirdparty\\(.*";
	/* TODO: get rid of this hack ! */
	private static final String STATIC_INCLUDE_REGEX = ".*(br|caplin)\\.staticInclude\\(.*";
	private static final String CAPLIN_IMPLEMENT_OR_EXTEND_REGEX = ".*(br|caplin)\\.(extend|implement|mixin|inherit|provide)\\(.*, *.*";

	private final StringBuffer stringSinceStartOfLine = new StringBuffer();
	
	private char previousChar = ' ';

	public void append( char latest ) throws IOException {
		Utils.ensureCharactersDontMatchMacLineEndings(previousChar, latest);
		
		previousChar = latest;
		stringSinceStartOfLine.append( latest );

		if ( latest == '\n' ) {
			stringSinceStartOfLine.setLength( 0 );
		}
	}

	public void checkIfStaticOrThirdpartyDependency( Match match ) {
		String line = stringSinceStartOfLine.toString();

		if ( line.matches(CAPLIN_IMPLEMENT_OR_EXTEND_REGEX) || line.matches(STATIC_INCLUDE_REGEX) ) {
			match.setStaticDependency(true);
		} else if ( line.matches(THIRDPARTY_REGEX) ) {
			match.setThirdPartyDependency(true);
		}
	}
}