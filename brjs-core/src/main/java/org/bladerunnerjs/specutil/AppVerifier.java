package org.bladerunnerjs.specutil;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppJsLibWrapper;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.hamcrest.collection.IsIterableContainingInOrder;

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
			if (lib instanceof AppJsLibWrapper)
			{
				appLibs.add( ((AppJsLibWrapper) lib).getWrappedLib() );
			}
			else
			{
				appLibs.add( lib );				
			}
		}
		
		assertThat(appLibs, IsIterableContainingInOrder.contains(libs)); // assert against the unwrapped libs
	}

	public void libsReturnCorrectApp()
	{
		for (JsLib lib : app.jsLibs())
		{
			assertSame(app, lib.getApp());
		}
	}
}
