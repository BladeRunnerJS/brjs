package org.bladerunnerjs.specutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppJsLibWrapper;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;

import static org.junit.Assert.*;


public class AppVerifier extends NodeVerifier<App> {
	
	App app;
	
	public AppVerifier(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
	}

	public void hasLibs(JsLib... libs)
	{
		List<JsLib> appLibs = new ArrayList<JsLib>();
		
		for (JsLib lib : app.jsLibs())
		{
			if (lib instanceof AppJsLibWrapper) // assert against the unwrapped libs
			{
				appLibs.add( ((AppJsLibWrapper) lib).getWrappedLib() );
			}
			else
			{
				appLibs.add( lib );				
			}
		}
		
		assertLibsListsAreSame( Arrays.asList(libs), appLibs ); 
	}

	public void libsReturnCorrectApp()
	{
		for (JsLib lib : app.jsLibs())
		{
			assertSame(app, lib.getApp());
		}
	}
	
	
	private void assertLibsListsAreSame(List<JsLib> libs, List<JsLib> appLibs)
	{
		assertEquals("lists are different sizes, expected entries: "+StringUtils.join(libs, ", ")+",  actual: "+StringUtils.join(appLibs, ", "), libs.size(), appLibs.size());
		for (int i = 0; i < libs.size(); i++)
		{
			assertSame("list entries (index "+i+") dont match, expected entries: "+StringUtils.join(libs, ", ")+",  actual: "+StringUtils.join(appLibs, ", "), libs.get(i), appLibs.get(i));
		}
	}

	public void libWithNameIs(String libName, JsLib appOverriddenNonBRLib)
	{
		JsLib appJsLib = app.nonBladeRunnerLib(libName);
		appJsLib = (appJsLib instanceof AppJsLibWrapper) ? ((AppJsLibWrapper) appJsLib).getWrappedLib() : appJsLib;
		assertSame(appJsLib, appOverriddenNonBRLib);
	}
}
