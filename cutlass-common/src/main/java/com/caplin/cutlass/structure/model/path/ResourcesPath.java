package com.caplin.cutlass.structure.model.path;

import java.io.File;

public class ResourcesPath extends AbstractPath
{
	public ResourcesPath(File path)
	{
		super(path);
	}
	
	public HtmlPath htmlPath()
	{
		return new HtmlPath(new File(path, "html"));
	}
	
	public XmlPath xmlPath()
	{
		return new XmlPath(new File(path, "xml"));
	}
	
	public I18nPath i18nPath()
	{
		return new I18nPath(new File(path, "i18n"));
	}
}
