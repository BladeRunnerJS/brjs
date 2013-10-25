package com.caplin.cutlass.bundler.js.analyser;

import com.caplin.cutlass.bundler.js.Match;
import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor;

public class NullCodeAnalyser implements CodeAnalyser
{
	@Override
	public void setRoot(String rootName)
	{
		// do nothing
	}

	@Override
	public void add(Match match)
	{
		// do nothing
	}

	@Override
	public void addCompleted()
	{
		// do nothing
	}

	@Override
	public void emit(CodeUnitVisitor visitor)
	{
		// do nothing
	}

	@Override
	public String emitString()
	{
		return null;
	}
}
