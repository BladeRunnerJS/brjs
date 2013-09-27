package com.caplin.cutlass.bundler.js.analyser;

import com.caplin.cutlass.bundler.js.Match;
import com.caplin.cutlass.bundler.js.analyser.DefaultCodeAnalyser.CodeUnitVisitor;

public interface CodeAnalyser
{
	public abstract void setRoot(String rootName);

	public abstract void add(Match match);

	public abstract void addCompleted();

	public abstract void emit(CodeUnitVisitor visitor);

	public abstract String emitString();
}
