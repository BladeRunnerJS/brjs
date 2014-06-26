package org.bladerunnerjs.plugin.plugins.bundlers.appversion;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentOutputStream;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.compositejs.CompositeJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.ServedAppMetadataUtility;


public class BundlePathJsContentPlugin extends AbstractContentPlugin
{

	private static final String APP_VERSION_REQUEST = "app-version-request";
	private ContentPathParser contentPathParser;
	private BRJS brjs;

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
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentOutputStream os, String version) throws ContentProcessingException
	{
		if (contentPath.formName.equals(APP_VERSION_REQUEST))
		{
			try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding()))
			{
				writer.write( ServedAppMetadataUtility.getBundlePathJsData(bundleSet.getBundlableNode().app(), version) );
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
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return getValidProdContentPaths(bundleSet, locales);
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
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
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(
				CommonJsContentPlugin.class.getCanonicalName(),
				CompositeJsContentPlugin.class.getCanonicalName()
		);
	}
	
}
