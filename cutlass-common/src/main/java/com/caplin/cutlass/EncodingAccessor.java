package com.caplin.cutlass;

import com.caplin.cutlass.structure.BladerunnerConf;

public class EncodingAccessor
{
	private static String inputEncoding = BladerunnerConf.getDefaultInputEncoding();
	private static String outputEncoding = BladerunnerConf.getDefaultOutputEncoding();
	
	public static String getDefaultInputEncoding()
	{
		return inputEncoding;
	}
	
	public static void setDefaultInputEncoding(String encoding)
	{
		inputEncoding = encoding;
	}
	
	public static String getDefaultOutputEncoding()
	{
		return outputEncoding;
	}
	
	public static void setDefaultOutputEncoding(String encoding)
	{
		outputEncoding = encoding;
	}
}
