package com.caplin.cutlass.structure;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public class BladerunnerConfTest
{
	@After
	public void tearDown()
	{
		BladerunnerConf.initialize(new File("no-such-file.conf"));
	}
	
	@Test
	public void valuesAreDefaultedWhenThereIsNoBladerunnerConfFile()
	{
		BladerunnerConf.initialize(new File("no-such-file.conf"));
		
		assertEquals(7070, BladerunnerConf.getJettyPort());
		assertEquals("UTF-8", BladerunnerConf.getDefaultInputEncoding());
		assertEquals("UTF-8", BladerunnerConf.getDefaultOutputEncoding());
	}
	
	@Test
	public void valuesCanBeOverriddenByProvidingAValidFile()
	{
		BladerunnerConf.initialize(new File("src/test/resources/BladerunnerConfTest/brjs.conf"));
		
		assertEquals(8080, BladerunnerConf.getJettyPort());
		assertEquals("UTF-16", BladerunnerConf.getDefaultInputEncoding());
		assertEquals("UTF-16", BladerunnerConf.getDefaultOutputEncoding());
	}
	
	@Test
	public void valuesCanBePartiallyOverridden()
	{
		BladerunnerConf.initialize(new File("src/test/resources/BladerunnerConfTest/partial-brjs.conf"));
		
		assertEquals(7070, BladerunnerConf.getJettyPort());
		assertEquals("UTF-16", BladerunnerConf.getDefaultInputEncoding());
		assertEquals("UTF-8", BladerunnerConf.getDefaultOutputEncoding());
	}
}
