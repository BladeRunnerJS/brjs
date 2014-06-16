package org.bladerunnerjs.plugin.plugins.bundlers.appversion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.compositejs.CompositeJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class BundlePathJsContentPlugin extends AbstractContentPlugin
{

	private static final String APP_VERSION_REQUEST = "app-version-request";
	private ContentPathParser contentPathParser;
	private BRJS brjs;
	private TagHandlerPlugin bundlePathTagHandler;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("app-version/version.js").as(APP_VERSION_REQUEST);
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "app-version";
	}

	@Override
	public String getCompositeGroupName()
	{
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os, String version) throws ContentProcessingException
	{
		if (contentPath.formName.equals(APP_VERSION_REQUEST))
		{
			try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
			{
				StringWriter bundlePathWriter = new StringWriter();
				bundlePathTagHandler.writeDevTagContent(new HashMap<>(), bundleSet, "", bundlePathWriter, version);
				writer.write( "window.$BRJS_BUNDLE_PATH = '"+bundlePathWriter.toString()+"';" );
			}
			catch (ConfigException | IOException ex)
			{
				throw new ContentProcessingException(ex);
			}
		}
		else 
		{
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return getValidProdContentPaths(bundleSet, locales);
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		try
		{
			return Arrays.asList( contentPathParser.createRequest(APP_VERSION_REQUEST) );
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.bundlePathTagHandler = brjs.plugins().tagHandlerPlugin("bundle.path");
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(
				NodeJsContentPlugin.class.getCanonicalName(),
				CompositeJsContentPlugin.class.getCanonicalName()
		);
	}

}
