package com.caplin.cutlass.structure.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;


public class SuffixNameCalculator implements NodeNameCalculator
{
	
	private String suffix = "";
	
	public SuffixNameCalculator(String suffix)
	{
		this.suffix = suffix;
	}

	@Override
	public String calculateNodeName(File nodePath)
	{
		String replaceReverse = StringUtils.reverse(suffix);
		String nodeNameReverse = StringUtils.reverse(nodePath.getName());
		String replacedReverse = nodeNameReverse.replaceFirst(replaceReverse, "");
		return StringUtils.reverse(replacedReverse);		
	}

}
