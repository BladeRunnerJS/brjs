package com.caplin.cutlass.bundler.xml.merge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;

import com.caplin.cutlass.EncodingAccessor;
import com.caplin.cutlass.bundler.xml.XmlBundler;
import com.caplin.cutlass.bundler.xml.utils.TeeOutputStream;
import com.caplin.cutlass.bundler.xml.utils.XmlDocumentLocator;

public abstract class MergeTestRunnerTest
{
	private XmlBundler resourceBundler;
	private String suiteFolder;
	
	{
		try
		{
			resourceBundler = new XmlBundler();
			resourceBundler.outputContinuously();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected MergeTestRunnerTest(String suiteFolder)
	{
		this.suiteFolder = suiteFolder;
	}
	
	protected void runBundlerTest(String testPath, String[] excludePaths) throws Exception {
		String basePath = "src/test/resources/xml-bundler/" + suiteFolder + "/" + testPath;
		String[] inputPaths = {basePath + "/input"};
		List<File> documents = XmlDocumentLocator.locateXmlDocuments(Arrays.asList(inputPaths), "./", excludePaths);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		OutputStream teeOutputStream = new TeeOutputStream(byteArrayOutputStream, new ByteArrayOutputStream());
		
		resourceBundler.writeBundle(documents, teeOutputStream);
		compareXmls(new File(basePath + "/expected-output.xml"), byteArrayOutputStream);
	}
	
	protected void runBundlerTest(String testPath) throws Exception
	{
		runBundlerTest(testPath, new String[0]);
	}
	
	protected void compareXmls(File sourceXmlFile, ByteArrayOutputStream generatedXml) throws Exception
	{
		Reader expectedReader = new InputStreamReader(new FileInputStream(sourceXmlFile), EncodingAccessor.getDefaultInputEncoding());
		Reader generatedReader = new InputStreamReader(new ByteArrayInputStream(generatedXml.toByteArray()), EncodingAccessor.getDefaultOutputEncoding());
		
		XMLUnit.setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
		XMLUnit.setIgnoreWhitespace(true);
		Diff diff = new Diff(expectedReader, generatedReader);
		
		if (!diff.identical()) {
			System.err.println("\nGenerated XML does not match expected:");
			DetailedDiff dd = new DetailedDiff(diff);
			@SuppressWarnings("rawtypes")
			List l = dd.getAllDifferences();
			for (Object i : l) {
				System.err.println(i);
			}
		}
		
		Assert.assertTrue(diff.identical());
	}
}
