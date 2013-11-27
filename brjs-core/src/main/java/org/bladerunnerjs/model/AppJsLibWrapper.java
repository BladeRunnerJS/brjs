package org.bladerunnerjs.model;


public class AppJsLibWrapper extends JsLib
{

	private App jsLibApp;
	private JsLib jsLib;
	
	public AppJsLibWrapper(App jsLibApp, JsLib jsLib)
	{
		super(jsLib.root(), jsLib.parentNode(), jsLib.dir(), jsLib.getName());
		this.jsLibApp = jsLibApp;
		this.jsLib = jsLib;
	}

	@Override
	public App getApp()
	{
		return jsLibApp;
	}
	
	public JsLib getWrappedLib()
	{
		return jsLib;
	}
	
}
