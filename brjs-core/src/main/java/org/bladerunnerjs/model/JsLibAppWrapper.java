package org.bladerunnerjs.model;

public class JsLibAppWrapper extends JsLib
{

	private App jsLibApp;
	private JsLib wrappedJsLib;
	
	public JsLibAppWrapper(App jsLibApp, JsLib jsLib)
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
