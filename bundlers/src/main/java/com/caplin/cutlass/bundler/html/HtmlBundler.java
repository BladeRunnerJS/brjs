package com.caplin.cutlass.bundler.html;

import static com.caplin.cutlass.bundler.BundlerConstants.BUNDLE_EXT;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.LegacyFileBundlerPlugin;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;

import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.BundlerFileUtils;
import com.caplin.cutlass.bundler.SourceFileProvider;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.AbstractPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class HtmlBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private final IOFileFilter htmlFilter = new SuffixFileFilter(".html");
	private final IOFileFilter htmFilter = new SuffixFileFilter(".htm");
	private final IOFileFilter htmlOrHtmFileFilter = new OrFileFilter(htmlFilter, htmFilter);
	private final ContentPathParser contentPathParser = RequestParserFactory.createHtmlBundlerContentPathParser();
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "html.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return contentPathParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{
		SourceFileProvider htmlSourceFileProvider = new BladeRunnerSourceFileProvider(new HtmlBundlerFileAppender());

		contentPathParser.parse(requestName);
		
		List<File> sourceFiles = htmlSourceFileProvider.getSourceFiles(baseDir, testDir);
		List<File> htmlSourceFiles = new ArrayList<File>();
		for (File sourceFile : sourceFiles)
		{
			BundlerFileUtils.recursiveListFiles(sourceFile, htmlSourceFiles, htmlOrHtmFileFilter);
		}

		return htmlSourceFiles;
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		HtmlFileProcessor htmlFileProcessor = new HtmlFileProcessor();
		try
		{
			for (File file : sourceFiles)
			{
				htmlFileProcessor.bundleHtml(file, writer);
			}
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}

	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		return Arrays.asList(BundlePathsFromRoot.HTML + "html" + BUNDLE_EXT);
	}
}
