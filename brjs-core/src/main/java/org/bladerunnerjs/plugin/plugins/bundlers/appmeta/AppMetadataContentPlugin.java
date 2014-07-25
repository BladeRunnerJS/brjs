package org.bladerunnerjs.plugin.plugins.bundlers.appmeta;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.compositejs.CompositeJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.AppMetadataUtility;

import com.google.common.base.Joiner;


public class AppMetadataContentPlugin extends AbstractContentPlugin
{

	private static final String APP_META_REQUEST = "app-meta-request";
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("app-meta/version.js").as(APP_META_REQUEST);
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "app-meta";
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException
	{
		if (contentPath.formName.equals(APP_META_REQUEST))
		{
			try
			{
				App app = bundleSet.getBundlableNode().app();
				//NOTE: this metadata is used by the BRAppMetaService
				return new CharResponseContent( brjs, "// these variables should not be used directly but accessed via the 'br.app-meta-service' instead\n" + 
						"window.$BRJS_APP_VERSION = '"+version+"';\n" +
						"window.$BRJS_VERSIONED_BUNDLE_PATH = '"+AppMetadataUtility.getRelativeVersionedBundlePath(version, "")+"';\n" +
						"window.$BRJS_LOCALE_COOKIE_NAME = '"+app.appConf().getLocaleCookieName()+"';\n" +
						"window.$BRJS_APP_LOCALES = {'" + Joiner.on("':true, '").join(app.appConf().getLocales()) + "':true};\n" );
			}
			catch (ConfigException ex)
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
			return Arrays.asList( contentPathParser.createRequest(APP_META_REQUEST) );
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
