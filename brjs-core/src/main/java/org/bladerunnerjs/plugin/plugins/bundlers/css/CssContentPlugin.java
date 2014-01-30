package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssContentPlugin extends AbstractContentPlugin
{
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("css/<theme>_css.bundle").as("simple-request")
				.and("css/<theme>_<languageCode>_css.bundle").as("language-request")
				.and("css/<theme>_<languageCode>_<countryCode>_css.bundle").as("locale-request")
				.and("css/<theme>_<browser>_css.bundle").as("browser-request")
			.where("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("languageCode").hasForm("[a-z]{2}")
				.and("countryCode").hasForm("[A-Z]{2}")
				.and("browser").hasForm("[a-z]+[0-9]+");
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "css";
	}
	
	@Override
	public String getGroupName() {
		return "text/css";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	private List<String> getValidContentPaths(BundleSet bundleSet, List<String> locales) {
		List<String> contentPaths = new ArrayList<>();
		
//		for (String theme : bundleSet.themes()) {
//			contentPaths.add(BundlePathsFromRoot.CSS + theme + CSS_BUNDLE_EXT);
//			
//			for (String locale : appMetaData.getLocales()) {
//				contentPaths.add(BundlePathsFromRoot.CSS + theme + "_" + locale + CSS_BUNDLE_EXT);
//			}
//		}
//		
//		String commonCSSRequest = BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT;
//		if (!contentPaths.contains(commonCSSRequest))
//		{
//			contentPaths.add(BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT);
//		}
		
		return contentPaths;
	}
	
	
	
	
	
	
	
	
	
	
	












//	private final Map<String, BladeRunnerSourceFileProvider> providers = new HashMap<String, BladeRunnerSourceFileProvider>();
//	
//	@Override
//	public String getBundlerExtension()
//	{
//		return "css.bundle";
//	}
//	
//	@Override
//	public List<String> getValidRequestForms()
//	{
//		return contentPathParser.getRequestForms();
//	}
//	
//	public List<String> getValidRequestStrings(AppMetaData appMetaData)
//	{
//		List<String> requests = new ArrayList<String>();
//		for (String theme : appMetaData.getThemes())
//		{
//			requests.add(BundlePathsFromRoot.CSS + theme + CSS_BUNDLE_EXT);
//			for (String locale : appMetaData.getLocales())
//			{
//				requests.add(BundlePathsFromRoot.CSS + theme + "_" + locale + CSS_BUNDLE_EXT);
//			}
//
//			for (String browser : appMetaData.getBrowsers())
//			{
//				requests.add(BundlePathsFromRoot.CSS + theme + "_" + browser + CSS_BUNDLE_EXT);
//			}
//		}
//		
//		String commonCSSRequest = BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT;
//		if (!requests.contains(commonCSSRequest))
//		{
//			requests.add(BundlePathsFromRoot.CSS + CutlassConfig.COMMON_CSS + CSS_BUNDLE_EXT);
//		}
//		
//		return requests;
//	}
//
//	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
//	{
//		Writer writer = BundleWriterFactory.createWriter(outputStream);
//		
//		try
//		{
//			for (File file : sourceFiles)
//			{
//				try
//				{
//					CssFileRewriter processor = new CssFileRewriter(file);
//					writer.append(processor.getFileContents());
//				}
//				catch (IOException e)
//				{
//					throw new BundlerFileProcessingException(file, e, "Error while bundling file.");
//				}
//			}
//		}
//		finally
//		{
//			BundleWriterFactory.closeWriter(writer);
//		}
//	}
//
//	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
//	{		
//		ParsedContentPath request = contentPathParser.parse(requestName);
//		String theme = request.properties.get("theme");
//		String languageCode = request.properties.get("languageCode");
//		String countryCode = request.properties.get("countryCode");
//		String locale = null;
//		if (languageCode != null && countryCode != null)
//		{
//			locale = languageCode + "_" + countryCode;
//		}
//		else if (languageCode != null)
//		{
//			locale = languageCode;
//		}
//		String browser = request.properties.get("browser");
//		
//		final String pattern = getFilePattern(locale, browser);
//
//		List<File> files = getProvider(theme).getSourceFiles(baseDir, testDir);
//		List<File> bundleFiles = new ArrayList<File>();		
//		
//		for (File file : files)
//		{
//			BundlerFileUtils.recursiveListFiles(file, bundleFiles, new RegexFileFilter(pattern));
//		}
//		
//		return bundleFiles;
//	}
//
//	private BladeRunnerSourceFileProvider getProvider(String theme)
//	{
//		if (!providers.containsKey(theme))
//		{
//			providers.put(theme, new BladeRunnerSourceFileProvider(new CssBundlerFileAppender(theme)));
//		}
//
//		return providers.get(theme);
//	}
//
//	private String getFilePattern(String locale, String browser)
//	{
//		String pattern = "";
//		if (locale != null)
//		{
//			// .*_en_GB.css
//			pattern = ".*" + locale;
//		}
//		else if (browser != null)
//		{
//			// .*_ie7.css
//			pattern = ".*" + browser;
//		}
//		else
//		{
//			// If we are looking for a CSS file without the locale or browser,
//			// then
//			// we can assume that it does not have underscores in it.
//			pattern = "[^_]+";
//		}
//
//		return pattern + "\\.css";
//	}
}
