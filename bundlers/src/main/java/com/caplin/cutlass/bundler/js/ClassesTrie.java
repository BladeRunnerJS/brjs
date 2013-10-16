package com.caplin.cutlass.bundler.js;

import java.util.HashMap;
import java.util.Map;

import com.caplin.cutlass.bundler.js.aliasing.AliasInformation;

public class ClassesTrie
{

	private LetterNode root = new LetterNode();
	private Map<String, AliasInformation> qualifiedAliasToAliasInformation = new HashMap<String, AliasInformation>();

	public ClassesTrie()
	{
	}

	public void addClass(String classname)
	{
		addIdentifierToTrie( classname ).setIndentifierEnd();
	}
	
	public void addAlias( String qualifiedAlias, AliasInformation aliasInformation )
	{
		addIdentifierToTrie( qualifiedAlias ).setAlias().setIndentifierEnd();
		
		qualifiedAliasToAliasInformation.put( qualifiedAlias, aliasInformation );
	}
	
	public LetterNode getRootNode()
	{
		return root;
	}
	
	public AliasInformation getAliasInformation( String qualifiedAlias )
	{
		return qualifiedAliasToAliasInformation.get( qualifiedAlias );
	}
	
	private LetterNode addIdentifierToTrie( String classname )
	{
		LetterNode node = root;
		
		for( char character : classname.toCharArray() )
		{
			node = node.getOrCreateNextNode( character );
		}
		
		return node;
	}
}