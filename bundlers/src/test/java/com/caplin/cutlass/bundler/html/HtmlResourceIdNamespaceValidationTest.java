package com.caplin.cutlass.bundler.html;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class HtmlResourceIdNamespaceValidationTest
{
	private final static String TEST_BASE = "src/test/resources/html-bundler/id-scope/";
	private final static String APPLICATIONS_DIR = TEST_BASE + "/" + CutlassConfig.APPLICATIONS_DIR;
	private final Writer mockWriter = mock(Writer.class);
	private HtmlFileProcessor htmlFileProcessor;
	
	@Before
	public void setUp()
	{
		htmlFileProcessor = new HtmlFileProcessor();
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(TEST_BASE)));
	}
	
	//Blade id tests
	@Test
	public void testValidBladeIds() throws Exception
	{
		File validHtmlFile = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/html/html1.html");
		htmlFileProcessor.bundleHtml(validHtmlFile, mockWriter);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithNoNamespaceOrBladesetName() throws Exception
	{
		File invalidHtmlFileWithoutAnyNameSpacePrefixId = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/html/html2.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFileWithoutAnyNameSpacePrefixId, mockWriter);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithBladeNameTypo() throws Exception
	{
		File invalidHtmlFile = new File(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/blade1/resources/html/blade1Invalid.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFile, mockWriter);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithSiblingBladeName() throws Exception
	{
		File invalidHtmlFile = new File(APPLICATIONS_DIR + "/test-app3/fx-bladeset/blades/blade1/resources/html/blade1Invalid.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFile, mockWriter);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithNoBladesetName() throws Exception
	{
		File invalidHtmlFile = new File(APPLICATIONS_DIR + "/test-app4/fx-bladeset/blades/blade1/resources/html/blade1Invalid.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFile, mockWriter);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithWrongBladesetName() throws Exception
	{
		File invalidHtmlFile = new File(APPLICATIONS_DIR + "/test-app5/fx-bladeset/blades/blade1/resources/html/blade1Invalid.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFile, mockWriter);
	}
	
	@Test
	public void testValidBladeIdInNonDivTags() throws Exception
	{
		ArrayList<File> htmlFilesToValidate = new ArrayList<File>();
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/html/htmlWithScriptTag.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/html/htmlWithSpanTag.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/html/htmlWithSingleQuoteAttribute.html"));
		
		for (File htmlFile : htmlFilesToValidate)
		{
			htmlFileProcessor.bundleHtml(htmlFile, mockWriter);
		}
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testExceptionIsThrownForDuplicateIdsInBlade() throws Exception
	{
		ArrayList<File> htmlFilesToValidate = new ArrayList<File>();
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app9/fx-bladeset/blades/blade1/resources/html/html1.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app9/fx-bladeset/blades/blade1/resources/html/html2.html"));
		
		for (File htmlFile : htmlFilesToValidate)
		{
			htmlFileProcessor.bundleHtml(htmlFile, mockWriter);
		}
	}

	//Bladeset id tests
	@Test
	public void testValidBladesetIds() throws Exception
	{
		File validHtmlFile = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/resources/html/html1.html");
		htmlFileProcessor.bundleHtml(validHtmlFile, mockWriter);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithNoNamespaceOrBladesetName() throws Exception
	{
		File validHtmlFile = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/resources/html/html2.html");
		htmlFileProcessor.bundleHtml(validHtmlFile, mockWriter);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithNoBladesetName() throws Exception
	{
		File validHtmlFile = new File(APPLICATIONS_DIR + "/test-app6/fx-bladeset/resources/html/bladesetInvalid.html");
		htmlFileProcessor.bundleHtml(validHtmlFile, mockWriter);
	}
	
	// TODO PCTCUT-468 - bundler namespace validation does not fail fast if there are duplicate IDs defined
	@Ignore
	@Test (expected=BundlerFileProcessingException.class)
	public void testValidBladesetIdDuplicated() throws Exception
	{
		File htmlFileWithDuplicateIdTemplates = new File(APPLICATIONS_DIR + "/test-app7/fx-bladeset/resources/html/bladesetValid.html");
		htmlFileProcessor.bundleHtml(htmlFileWithDuplicateIdTemplates, mockWriter);
	}
	
	//TODO: why is this commented out??
	@Test //(expected=BundleFileProcessingException.class)
	public void testInvalidBladesetIdWithChildBladeName() throws Exception
	{
		File invalidHtmlFile = new File(APPLICATIONS_DIR + "/test-app8/fx-bladeset/resources/html/bladesetInvalid.html");
		htmlFileProcessor.bundleHtml(invalidHtmlFile, mockWriter);
	}

	@Test
	public void testValidBladesetIdInScriptTag() throws Exception
	{
		ArrayList<File> htmlFilesToValidate = new ArrayList<File>();
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app8/fx-bladeset/resources/html/htmlWithScriptTag.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app8/fx-bladeset/resources/html/htmlWithSpanTag.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app8/fx-bladeset/resources/html/htmlWithSingleQuoteAttribute.html"));
		
		for (File htmlFile : htmlFilesToValidate)
		{
			htmlFileProcessor.bundleHtml(htmlFile, mockWriter);
		}
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testExceptionIsThrownForDuplicateIdsInBladeset() throws Exception
	{
		ArrayList<File> htmlFilesToValidate = new ArrayList<File>();
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app9/fx-bladeset/resources/html/html1.html"));
		htmlFilesToValidate.add(new File(APPLICATIONS_DIR + "/test-app9/fx-bladeset/resources/html/html2.html"));
		
		for (File htmlFile : htmlFilesToValidate)
		{
			htmlFileProcessor.bundleHtml(htmlFile, mockWriter);
		}
	}
}
