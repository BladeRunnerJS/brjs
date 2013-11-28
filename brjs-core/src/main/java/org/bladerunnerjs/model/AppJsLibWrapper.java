package org.bladerunnerjs.model;


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
	
}
