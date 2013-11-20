package org.bladerunnerjs.core.plugin.bundler.js;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class MinifierSettingTest {

	private MinifierSetting minifierSettings;
	private Map<String, String> settings;
	
	@Before
	public void setup()
	{
		settings =  new HashMap<String, String>();
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
		settings.put("dev", "combined");
		settings.put("prod", "closure-simple");
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("combined", minifierSettings.devSetting());
		assertEquals("closure-simple", minifierSettings.prodSetting());
	}
	
	@Test
	public void settingsCanOnlyBeDefinedWithCorrectNamecase()
	{
		settings.put("Dev", "combined");
		settings.put("Prod", "closure-simple");
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("none", minifierSettings.devSetting());
		assertEquals("combined", minifierSettings.prodSetting());
	}
}
