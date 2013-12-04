package com.caplin.cutlass.bundler.css;

import static com.caplin.cutlass.bundler.BundlerConstants.BUNDLE_EXT;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.bladerunnerjs.core.plugin.AbstractPlugin;
import com.caplin.cutlass.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.CutlassConfig;

import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.BundlerFileUtils;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class CssBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private static final String CSS_BUNDLE_EXT = "_css" + BUNDLE_EXT;
	private final Map<String, BladeRunnerSourceFileProvider> providers = new HashMap<String, BladeRunnerSourceFileProvider>();
	private final ContentPathParser requestParser = RequestParserFactory.createCssBundlerRequestParser();
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "css.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return requestParser.getRequestForms();
	}
	
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		List<String> requests = new ArrayList<String>();
		for (String theme : appMetaData.getThemes())
		{
			requests.add(BundlePathsFromRoot.CSS + theme + CSS_BUNDLE_EXT);
			for (String locale : appMetaData.getLocales())
			{
				requests.add(BundlePathsFromRoot.CSS + theme + "_" + locale + CSS_BUNDLE_EXT);
			}

			for (String browser : appMetaData.getBrowsers())
			{
				requests.add(BundlePathsFromRoot.CSS + theme + "_" + browser + CSS_BUNDLE_EXT);
			}
		}
		
		String commonCSSRequest = BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT;
		if (!requests.contains(commonCSSRequest))
		{
			requests.add(BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT);
		}
		
		return requests;
	}

	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		
		try
		{
			for (File file : sourceFiles)
			{
				try
				{
					CssFileRewriter processor = new CssFileRewriter(file);
					writer.append(processor.getFileContents());
				}
				catch (IOException e)
				{
					throw new BundlerFileProcessingException(file, e, "Error while bundling file.");
				}
			}
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}

	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{		
		ParsedContentPath request = requestParser.parse(requestName);
		String theme = request.properties.get("theme");
		String languageCode = request.properties.get("languageCode");
		String countryCode = request.properties.get("countryCode");
		String locale = null;
		if (languageCode != null && countryCode != null)
		{
			locale = languageCode + "_" + countryCode;
		}
		else if (languageCode != null)
		{
			locale = languageCode;
		}
		String browser = request.properties.get("browser");
		
		final String pattern = getFilePattern(locale, browser);

		List<File> files = getProvider(theme).getSourceFiles(baseDir, testDir);
		List<File> bundleFiles = new ArrayList<File>();		
		
		for (File file : files)
		{
			BundlerFileUtils.recursiveListFiles(file, bundleFiles, new RegexFileFilter(pattern));
		}
		
		return bundleFiles;
	}

	private BladeRunnerSourceFileProvider getProvider(String theme)
	{
		if (!providers.containsKey(theme))
		{
			providers.put(theme, new BladeRunnerSourceFileProvider(new CssBundlerFileAppender(theme)));
		}

		return providers.get(theme);
	}

	private String getFilePattern(String locale, String browser)
	{
		String pattern = "";
		if (locale != null)
		{
			// .*_en_GB.css
			pattern = ".*" + locale;
		}
		else if (browser != null)
		{
			// .*_ie7.css
			pattern = ".*" + browser;
		}
		else
		{
			// If we are looking for a CSS file without the locale or browser,
			// then
			// we can assume that it does not have underscores in it.
			pattern = "[^_]+";
		}

		return pattern + "\\.css";
	}
}
