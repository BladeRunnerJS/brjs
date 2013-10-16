package com.caplin.cutlass.bundler.js;

import java.util.HashMap;
import java.util.Map;

class LetterNode
{
	private Map<Character, LetterNode> children = new HashMap<Character, LetterNode>();
	private boolean isAlias = false;
	private boolean identifierEnd = false;

	LetterNode getOrCreateNextNode(char child)
	{
		if (children.get(child) == null)
		{
			children.put(child, new LetterNode());
			return children.get(child);
		}
		return children.get(child);
	}

	LetterNode find(char target)
	{
		return children.get(target);
	}

	boolean isIdentifierEnd()
	{
		return identifierEnd;
	}
	
	boolean isAlias()
	{
		return isAlias;
	}

	public void setIndentifierEnd()
	{
		this.identifierEnd = true;
	}
	
	public LetterNode setAlias()
	{
		isAlias = true;
		
		return this;
	}

}
