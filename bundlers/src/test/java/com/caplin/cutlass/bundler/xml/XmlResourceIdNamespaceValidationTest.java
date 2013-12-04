package com.caplin.cutlass.bundler.xml;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.Writer;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.caplin.cutlass.CutlassConfig;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class XmlResourceIdNamespaceValidationTest
{
	private static final String TEST_BASE = "src/test/resources/xml-bundler/id-scope";
	private static final String APPLICATIONS_DIR = TEST_BASE + "/" + CutlassConfig.APPLICATIONS_DIR;
	private final Writer writer = mock(Writer.class);
	
	private XmlBundleWriter bundleWriter;
	
	@Before
	public void setUp() throws Exception
	{
		bundleWriter = new XmlBundleWriter();
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(TEST_BASE)));
	}
	
	// Tests for specific to the blade id's
	@Test
	public void testValidBladeIds() throws Exception
	{
		File validGridDefinitionsFile = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/blades/blade1/resources/xml/gridDefinitions.xml");
		bundleWriter.writeBundle(Arrays.asList(validGridDefinitionsFile), writer);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testValidBladeIdDuplicatedInDifferentFilesAtBladeLevel() throws Exception
	{
		File validFile1 = new File(APPLICATIONS_DIR + "/test-app7/fx-bladeset/blades/blade1/resources/xml/blade1valid1.xml");
		File validFile2 = new File(APPLICATIONS_DIR + "/test-app7/fx-bladeset/blades/blade1/resources/xml/blade1valid2.xml");
		
		bundleWriter.writeBundle(Arrays.asList(validFile1, validFile2), writer);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testValidBladeIdDuplicatedInDifferentFilesAtBladesetLevel() throws Exception
	{
		File validFile1 = new File(APPLICATIONS_DIR + "/test-app7/fx-bladeset/resources/xml/blade1valid1.xml");
		File validFile2 = new File(APPLICATIONS_DIR + "/test-app7/fx-bladeset/resources/xml/blade1valid2.xml");
		
		bundleWriter.writeBundle(Arrays.asList(validFile1, validFile2), writer);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithNoNamespaceOrBladesetOrBladeName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app/testbladeset-bladeset/blades/testblade-blade/resources/xml/gridDefinitionsInvalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithTypoBladeName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/blade1/resources/xml/blade1Invalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithSiblingBladeName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app3/fx-bladeset/blades/blade1/resources/xml/blade1Invalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testBladeAttemptsToOverrideParentBladesetId() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app4/fx-bladeset/blades/blade1/resources/xml/blade1Invalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithTypoParentBladesetName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app5/fx-bladeset/blades/blade1/resources/xml/blade1Invalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithWithNoNamespaceName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app6/fx-bladeset/blades/blade1/resources/xml/blade1Invalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	// Tests for specific to the bladeset id's
	
	@Test
	public void testValidBladesetIds() throws Exception
	{
		File validGridDefinitionsFile = new File(APPLICATIONS_DIR + "/test-app1/fx-bladeset/resources/xml/rendererDefinitions.xml");
		bundleWriter.writeBundle(Arrays.asList(validGridDefinitionsFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdNoNamespaceOrBladesetName() throws Exception
	{
		File validGridDefinitionsFile = new File(APPLICATIONS_DIR + "/test-app/testbladeset-bladeset/resources/xml/rendererDefinitionsInvalid.xml");
		bundleWriter.writeBundle(Arrays.asList(validGridDefinitionsFile), writer);
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdNoNamespace() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app8/fx-bladeset/resources/xml/fxbladesetInvalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Ignore
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithChildBladeName() throws Exception
	{
		File invalidFile = new File(APPLICATIONS_DIR + "/test-app9/fx-bladeset/resources/xml/fxbladesetInvalid.xml");
		bundleWriter.writeBundle(Arrays.asList(invalidFile), writer);
	}
	
	@Test
	public void testValidBladesetIdWhichIsNotChildBladeName() throws Exception
	{
		File validFile = new File(APPLICATIONS_DIR + "/test-app10/fx-bladeset/resources/xml/fxbladesetValid.xml");
		bundleWriter.writeBundle(Arrays.asList(validFile), writer);
	}
	
}
