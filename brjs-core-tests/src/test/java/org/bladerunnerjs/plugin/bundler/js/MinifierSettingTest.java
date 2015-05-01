package org.bladerunnerjs.plugin.bundler.js;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bladerunnerjs.plugin.bundlers.compositejs.MinifierSetting;
import org.junit.Before;
import org.junit.Test;

public class MinifierSettingTest {

	private MinifierSetting minifierSettings;
	private Map<String, String> settings;
	
	@Before
	public void setup()
	{
		settings =  new LinkedHashMap<String, String>();
	}
	
	@Test
	public void settingsHasDefaultValues()
	{
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("none", minifierSettings.devSetting());
		assertEquals("combined", minifierSettings.prodSetting());
	}
	
	@Test
	public void settingsCanBeOverriden()
	{
		settings.put("dev-minifier", "combined");
		settings.put("prod-minifier", "closure-simple");
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("combined", minifierSettings.devSetting());
		assertEquals("closure-simple", minifierSettings.prodSetting());
	}
	
	@Test
	public void settingsCanOnlyBeDefinedWithCorrectNamecase()
	{
		settings.put("Dev-minifier", "combined");
		settings.put("Prod-minifier", "closure-simple");
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("none", minifierSettings.devSetting());
		assertEquals("combined", minifierSettings.prodSetting());
	}
}
