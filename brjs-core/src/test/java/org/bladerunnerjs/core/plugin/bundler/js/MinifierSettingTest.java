package org.bladerunnerjs.core.plugin.bundler.js;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.junit.Before;
import org.junit.Test;

public class MinifierSettingTest {

	private MinifierSetting minifierSettings;
	private Map<String, String> settings;
	
	@Before
	public void setUp()
	{
		settings =  new HashMap<String, String>();
	}
	
	@Test
	public void MinifierSettingsHasDefaultValues()
	{
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("none", minifierSettings.devSetting());
		assertEquals("combined", minifierSettings.prodSetting());
	}
	
	@Test
	public void MinifierSettingsCanBeOverriden()
	{
		settings.put("dev", "combined");
		settings.put("prod", "closure-simple");
		minifierSettings = new MinifierSetting(settings);
		
		assertEquals("combined", minifierSettings.devSetting());
		assertEquals("closure-simple", minifierSettings.prodSetting());
	}
}
