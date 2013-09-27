package com.caplin.cutlass.structure.model;

import java.io.File;


public class BasicNameCalculator implements NodeNameCalculator
{
	
	@Override
	public String calculateNodeName(File nodePath)
	{
		return nodePath.getName();
	}

}
