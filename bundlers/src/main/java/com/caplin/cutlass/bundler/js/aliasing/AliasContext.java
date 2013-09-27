package com.caplin.cutlass.bundler.js.aliasing;

import java.util.Set;

import com.caplin.cutlass.structure.ScopeLevel;

public class AliasContext
{
	
	private ScopeLevel requestLevel;
	private String packageNamespace;
	private AliasRegistry aliasRegistry;
	private Set<String> validClasses;

	public AliasContext(ScopeLevel requestLevel, String packageNamespace, AliasRegistry aliasRegistry, Set<String> validClasses)
	{
		this.requestLevel = requestLevel;
		this.packageNamespace = packageNamespace;
		this.aliasRegistry = aliasRegistry;
		this.validClasses = validClasses;
	}

	public Set<String> getValidClasses()
	{
		return validClasses;
	}

	public AliasRegistry getAliasRegistry()
	{
		return aliasRegistry;
	}

	public ScopeLevel getRequestLevel()
	{
		return requestLevel;
	}

	public String getPackageNamespace() {
		return packageNamespace;
	}

}
