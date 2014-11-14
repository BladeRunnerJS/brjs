package org.bladerunnerjs.testing.utility;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;


public class ScriptedRequestGeneratingTagHandlerPlugin extends AbstractTagHandlerPlugin
{
	private List<String> usedUrls;
	private List<String> dependenctContentPlugins;
	private String tagName;
	
	public ScriptedRequestGeneratingTagHandlerPlugin(String tagName, List<String> dependenctContentPlugins, List<String> usedUrls)
	{
		this.tagName = tagName;
		this.dependenctContentPlugins = dependenctContentPlugins;
		this.usedUrls = usedUrls;
	}

	@Override
	public String getTagName()
	{
		return tagName;
	}

	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException
	{
		writer.write( this.getClass().getSimpleName() );
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public List<String> getGeneratedContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		return usedUrls;
	}
	
	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		return dependenctContentPlugins;
	}
	
}
