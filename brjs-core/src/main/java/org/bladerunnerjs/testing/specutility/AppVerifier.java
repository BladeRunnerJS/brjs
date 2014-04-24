package org.bladerunnerjs.testing.specutility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppSdkJsLib;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

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
			if (lib instanceof AppSdkJsLib) // assert against the unwrapped libs
			{
				appLibs.add( ((AppSdkJsLib) lib).getWrappedJsLib() );
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
			assertSame(app, lib.app());
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
		appJsLib = (appJsLib instanceof AppSdkJsLib) ? ((AppSdkJsLib) appJsLib).getWrappedJsLib() : appJsLib;
		assertSame(appJsLib, appOverriddenNonBRLib);
	}
}
