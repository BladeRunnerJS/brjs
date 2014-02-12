package com.caplin.cutlass.bundler.i18n;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.RegexFileFilter;

import com.caplin.cutlass.LegacyFileBundlerPlugin;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.BundlerFileUtils;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.BundlePathsFromRoot;
import com.caplin.cutlass.structure.NamespaceCalculator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class I18nBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private final ContentPathParser contentPathParser = RequestParserFactory.createI18nBundlerContentPathParser();
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "i18n.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return contentPathParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestString) throws RequestHandlingException
	{
		BladeRunnerSourceFileProvider sourceFileProvider = new BladeRunnerSourceFileProvider(new I18nBundlerFileAppender());
		
		List<File> bundleFiles = new ArrayList<File>();
		
		String pattern = getI18nPropertiesFilePattern(requestString);
		
		for (File file : sourceFileProvider.getSourceFiles(baseDir, testDir))
		{
			BundlerFileUtils.recursiveListFiles(file, bundleFiles, new RegexFileFilter(pattern));
		}
		
		return bundleFiles;
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws ContentProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		
		try
		{
			Properties bundle = generateBundle(sourceFiles);
			String jsonMap = convertPropertiesToJson(bundle);
			
			writeJson(writer, jsonMap);
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}
	
	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		List<String> requests = new ArrayList<String>();
		
		for(String locale : appMetaData.getLocales())
		{
			requests.add(BundlePathsFromRoot.I18N + locale + "_i18n.bundle");
		}
		
		for(String language : appMetaData.getLanguages())
		{
			requests.add(BundlePathsFromRoot.I18N + language + "_i18n.bundle");
		}
		
		return requests;
	}
	
	private void writeJson(Writer writer, String jsonMap) throws ContentProcessingException
	{
		try
		{
			writer.append(jsonMap + "\n");
		}
		catch (IOException e)
		{
			throw new ContentProcessingException(e, "Unable to write to output stream.");
		}
	}
	
	private String getI18nPropertiesFilePattern(String requestString) throws MalformedRequestException
	{
		ParsedContentPath request = contentPathParser.parse(requestString);
		String languageCode = request.properties.get("languageCode");
		String countryCode = request.properties.get("countryCode");
		
		String pattern = languageCode + "_" + countryCode + "\\.properties";
		
		if(countryCode == null)
		{
			pattern = languageCode + "\\.properties";
		}
		
		return pattern;
	}

	private Properties generateBundle(List<File> sourceFiles) throws ContentFileProcessingException
	{
		Properties bundle = new Properties();
		for (File file : sourceFiles)
		{
			Properties override = new Properties();
			try(Reader fileReader = BundlerFileReaderFactory.getBundlerFileReader(file))
			{
				override.load(fileReader);				
				verifyThatI18NTokensAreInCorrectNamespace(file, override);
			}
			catch (Exception e)
			{
				throw new ContentFileProcessingException(file, e, "Error while bundling file.");
			}
			bundle.putAll(override);
		}
		return bundle;
	}

	private void verifyThatI18NTokensAreInCorrectNamespace(File file, Properties override) throws Exception
	{
		String namespace = NamespaceCalculator.getPackageNamespaceForBladeLevelResources(file);
		
		if(namespace.length() > 0)
		{
			for(String identifier : override.stringPropertyNames())
			{
				if(!identifier.startsWith(namespace))
				{
					throw new ContentFileProcessingException(file, "The identifier '" + identifier +
						"' is not correctly namespaced, namespace '" + namespace + "*' was expected.");
				}
			}
		}
	}

	private String convertPropertiesToJson(Properties bundle)
	{
		String i18nBundle = "";
		
		if(bundle.size() > 0)
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			SortedMap<String, String> sortedBundle = new TreeMap(bundle);
	
			Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			String jsonMap = gson.toJson(sortedBundle, new TypeToken<TreeMap<String, String>>() {}.getType());
			
			i18nBundle =
				"pUnprocessedI18NMessages = (!window.pUnprocessedI18NMessages) ? [] : pUnprocessedI18NMessages;\n" +
				"pUnprocessedI18NMessages.push(" + jsonMap + ");\n";
		}
		
		return i18nBundle;
	}
}
