package org.bladerunnerjs.api.spec.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.AppSdkJsLib;

import com.google.common.base.Joiner;

import static org.junit.Assert.*;


public class AppVerifier extends NodeVerifier<App> {
	
	App app;
	
	public AppVerifier(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
	}

	public void hasLibs(JsLib... libs)
	{
		List<JsLib> appLibs = new ArrayList<>();
		
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
		assertEquals(getLibNames(libs), getLibNames(appLibs));
		
		for (int i = 0; i < libs.size(); i++)
		{
			assertSame("list entries (index "+i+") dont match, expected entries: "+StringUtils.join(libs, ", ")+",  actual: "+StringUtils.join(appLibs, ", "), libs.get(i), appLibs.get(i));
		}
	}

	private String getLibNames(List<JsLib> libs) {
		List<String> libNames = new ArrayList<>();
		
		for(JsLib lib : libs) {
			libNames.add(lib.getName());
		}
		
		return Joiner.on(", ").join(libNames);
	}

	public void libWithNameIs(String libName, JsLib appOverriddenNonBRLib)
	{
		JsLib appJsLib = app.jsLib(libName);
		appJsLib = (appJsLib instanceof AppSdkJsLib) ? ((AppSdkJsLib) appJsLib).getWrappedJsLib() : appJsLib;
		assertSame(appJsLib, appOverriddenNonBRLib);
	}
}
