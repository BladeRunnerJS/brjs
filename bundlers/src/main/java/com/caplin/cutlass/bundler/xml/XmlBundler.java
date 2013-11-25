package com.caplin.cutlass.bundler.xml;

import static com.caplin.cutlass.bundler.BundlerConstants.BUNDLE_EXT;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.xml.sax.SAXException;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.sinbin.AppMetaData;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.BundlerFileUtils;
import com.caplin.cutlass.bundler.SourceFileProvider;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.bundler.xml.reader.XmlSiblingReaderException;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class XmlBundler implements LegacyFileBundlerPlugin
{
	private XmlBundleWriter bundleWriter;
	private final IOFileFilter xmlFilter = new SuffixFileFilter(".xml");
	private final ContentPathParser requestParser = RequestParserFactory.createXmlBundlerRequestParser();
	
	public XmlBundler() throws ParserConfigurationException, SAXException, IOException
	{		
		bundleWriter = new XmlBundleWriter();
	}

	public void outputContinuously()
	{
		bundleWriter.outputContinuously();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "xml.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return requestParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{
		requestParser.parse(requestName);
		
		SourceFileProvider sourceFileProvider = new BladeRunnerSourceFileProvider(new XmlBundlerFileAppender());
		
		List<File> sourceFiles = sourceFileProvider.getSourceFiles(baseDir, testDir);
		List<File> xmlSourceFiles = new ArrayList<File>();
		for (File sourceFile : sourceFiles)
		{
			BundlerFileUtils.recursiveListFiles(sourceFile, xmlSourceFiles, xmlFilter);
		}

		return xmlSourceFiles;
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		
		try
		{
			bundleWriter.writeBundle(sourceFiles, writer);
		}
		catch (XMLStreamException e)
		{
			throw new BundlerFileProcessingException(null, e.getLocation().getLineNumber(), e.getLocation().getColumnNumber(), e.getMessage());
		}
		catch (IOException e)
		{
			throw new BundlerProcessingException(e, "Error bundling files.");
		}
		catch (XmlSiblingReaderException e)
		{
			throw new BundlerProcessingException(e, "Error bundling files.");
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}

	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		return Arrays.asList(BundlePathsFromRoot.XML + "xml" + BUNDLE_EXT);
	}
	
	@Override
	public boolean instanceOf(Class<? extends Plugin> otherPluginCLass)
	{
		throw new InstanceOfShouldntBeInvokedException();
	}
}
