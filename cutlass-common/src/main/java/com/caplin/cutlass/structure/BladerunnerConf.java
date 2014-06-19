package com.caplin.cutlass.structure;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.caplin.cutlass.CutlassConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

public class BladerunnerConf
{
	public int jettyPort = 7070;
	public String defaultInputEncoding = "UTF-8";
	public String defaultOutputEncoding = "UTF-8";
	public String loginRealm = "BladeRunnerLoginRealm";
	public Map<String, String> minifiers = new HashMap<String, String>();
	
	private static int $jettyPort;
	private static String $defaultInputEncoding;
	private static String $defaultOutputEncoding;
	private static Map<String, String> $minifiers;
	
	static
	{
		File sdkDir = new File(".").getAbsoluteFile().getParentFile();
		File confDir = new File(sdkDir.getParentFile(), CutlassConfig.CONF_DIR);
		initialize(new File(confDir, "brjs.conf"));
	}
	
	public BladerunnerConf()
	{
		minifiers.put("default", "com.caplin.cutlass.bundler.js.minification.ConcatenatingMinifier");
	}
	
	public static void initialize(File confFile)
	{
		BladerunnerConf conf = getBladerunnerConf(confFile);
		
		$jettyPort = conf.jettyPort;
		$defaultInputEncoding = conf.defaultInputEncoding;
		$defaultOutputEncoding = conf.defaultOutputEncoding;
		$minifiers = conf.minifiers;
	}
	
	public static int getJettyPort()
	{
		return $jettyPort;
	}
	
	public static String getDefaultInputEncoding()
	{
		return $defaultInputEncoding;
	}
	
	public static String getDefaultOutputEncoding()
	{
		return $defaultOutputEncoding;
	}
	
	public static Map<String, String> getMinifiers()
	{
		return $minifiers;
	}
	
	private static BladerunnerConf getBladerunnerConf(File bladerunnerConfFile)
	{
		BladerunnerConf bladerunnerConf = new BladerunnerConf();
		
		if(bladerunnerConfFile.exists())
		{
			try
			{
				YamlReader bladerunnerConfReader = new YamlReader(new FileReader(bladerunnerConfFile));
				
				try
				{
					BladerunnerConf bladerunnerConfFromFile = bladerunnerConfReader.read(BladerunnerConf.class);
					
					if(bladerunnerConfFromFile != null)
					{
						bladerunnerConf = bladerunnerConfFromFile;
					}
				}
				finally
				{
					bladerunnerConfReader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return bladerunnerConf;
	}
}
