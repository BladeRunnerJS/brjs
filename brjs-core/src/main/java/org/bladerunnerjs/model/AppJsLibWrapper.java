package org.bladerunnerjs.model;

import java.util.List;

//TODO: this should be a class that implements an Interface - we keep getting issues where methods don't go to the right implementation of JsLib
public class AppJsLibWrapper extends JsLib
{

	private App jsLibApp;
	private JsLib wrappedJsLib;
	
	public AppJsLibWrapper(App jsLibApp, JsLib jsLib)
	{
		super(jsLibApp.root(), jsLibApp, jsLib.dir());
		this.jsLibApp = jsLibApp;
		this.wrappedJsLib = jsLib;
	}

	@Override
	public App getApp()
	{
		return jsLibApp;
	}
	
	public JsLib getWrappedJsLib()
	{
		return wrappedJsLib;
	}
	
	@Override
	public List<AssetLocation> getAllAssetLocations()
	{
		return wrappedJsLib.getAllAssetLocations();
	}
	
	@Override
	public SourceAssetLocation src()
	{
		return wrappedJsLib.src();
	}
	
}
