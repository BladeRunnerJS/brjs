package com.caplin.cutlass.structure;

public enum BundlePathsFromRoot {
	CSS("css/"),
	HTML(""),
	IMAGES("images/"),
	THIRDPARTY("thirdparty-libraries/"),
	I18N("i18n/"),
	JS("js/"),
	XML("");
	
	private BundlePathsFromRoot(final String path)
	{
		this.path = path;
	}
	
	private final String path;
	
	@Override
	public String toString()
	{
		return path;
	}
}