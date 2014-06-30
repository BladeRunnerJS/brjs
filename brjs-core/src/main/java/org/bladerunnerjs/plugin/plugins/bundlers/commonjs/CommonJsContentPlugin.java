package org.bladerunnerjs.plugin.plugins.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.i18n.I18nContentPlugin;
import org.bladerunnerjs.plugin.utility.InstanceFinder;
import org.bladerunnerjs.utility.AdhocTimer;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import com.Ostermiller.util.ConcatReader;


public class CommonJsContentPlugin extends AbstractContentPlugin
{

	private static final String SINGLE_MODULE_REQUEST = "single-module-request";

	private static final String BUNDLE_REQUEST = "bundle-request";

	public static final String JS_STYLE = "node.js";

	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		try
		{
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder.accepts("node-js/bundle.js").as(BUNDLE_REQUEST).and("node-js/module/<module>.js").as(SINGLE_MODULE_REQUEST).where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN);

			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest(BUNDLE_REQUEST));
		}
		catch (MalformedTokenException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public String getRequestPrefix()
	{
		return "node-js";
	}

	@Override
	public String getCompositeGroupName()
	{
		return "text/javascript";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(I18nContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();

		try
		{
			for (SourceModule sourceModule : bundleSet.getSourceModules())
			{
				if (sourceModule instanceof CommonJsSourceModule)
				{
					requestPaths.add(contentPathParser.createRequest(SINGLE_MODULE_REQUEST, sourceModule.getPrimaryRequirePath()));
				}
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}

		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return (InstanceFinder.containsInstance(bundleSet.getSourceModules(), CommonJsSourceModule.class)) ? prodRequestPaths : Collections.emptyList();
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginOutput output, String version) throws ContentProcessingException
	{
		try
		{
			if (contentPath.formName.equals(SINGLE_MODULE_REQUEST))
			{
				SourceModule jsModule = (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(contentPath.properties.get("module"));
				output.setReader(jsModule.getReader());
				
			}
			else if (contentPath.formName.equals(BUNDLE_REQUEST))
			{
				List<Reader> readerList = new ArrayList<Reader>();
				for (SourceModule sourceModule : bundleSet.getSourceModules())
				{
					if (sourceModule instanceof CommonJsSourceModule)
					{
						readerList.add(new StringReader("// " + sourceModule.getPrimaryRequirePath() + "\n"));
						readerList.add(sourceModule.getReader());
						readerList.add(new StringReader("\n\n"));
					}
				}
				Reader[] readers = new Reader[readerList.size()];
				readerList.toArray(readers);
				output.setReader(new ConcatReader(readers));
			}
			else
			{
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (  IOException | RequirePathException e)
		{
			throw new ContentProcessingException(e);
		}
	}
}
