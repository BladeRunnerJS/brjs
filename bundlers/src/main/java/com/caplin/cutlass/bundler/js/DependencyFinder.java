package com.caplin.cutlass.bundler.js;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.caplin.cutlass.bundler.js.Match;

/**
 * Receives a stream of chars and tests whether any character string within the stream matches any of the set of possible dependent classes.
 * Independently on each line it searches for caplin keywords that indicate a static inclusion.
 * In this case the dependent class must be emitted first. 
 * 
 * We want to reduce these regexes for performance reasons.
 * We need to do a performance review of this class to improve load times and test times.
 */
public class DependencyFinder
{
	private final LetterNode rootLetterNode;
	private final List<Match> possibleMatches = new ArrayList<Match>();
	private final CurrentJsBundlerLine currentLine = new CurrentJsBundlerLine();
	
	private Match matchResult = null;

	public DependencyFinder( ClassesTrie trie, String sourceFileExtension )
	{
		rootLetterNode = trie.getRootNode();
	}	
	
	public Match next( char latest ) throws IOException
	{
		matchResult = null;
		currentLine.append( latest );
		
		List<Match> toRemove = new ArrayList<Match>();
		
		for ( Match match : possibleMatches )
		{
			boolean latestCharMatches = match.processNextCharacter( latest );
			
			if ( latestCharMatches == false )
			{
				toRemove.add( match );
				
				if ( match.hasMatchedAnIdentifier() )
				{
					setMatch( match );
				}
			}
		}
		
		addMatchIfCharacterIsARootLetter( latest );
		possibleMatches.removeAll(toRemove);
		
		return matchResult;
	}

	private void addMatchIfCharacterIsARootLetter( char latest )
	{
		LetterNode rootFound = rootLetterNode.find( latest );

		if ( rootFound != null )
		{
			Match newMatch = new Match( latest, rootFound );
			possibleMatches.add( newMatch );
		}
	}
	
	private void setMatch( Match match )
	{
		if( matchResult == null || ( match.getDependencyName().length() > matchResult.getDependencyName().length() ) )
		{
			matchResult = match;
			currentLine.checkIfStaticOrThirdpartyDependency( match );
		}
	}
}
