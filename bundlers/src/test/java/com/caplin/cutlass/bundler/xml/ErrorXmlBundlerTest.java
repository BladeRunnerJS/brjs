package com.caplin.cutlass.bundler.xml;

import java.io.File;

import org.junit.Test;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;

public class ErrorXmlBundlerTest
{
	@Test(expected=RequestHandlingException.class)
	public void requestsMustBeWellFormed() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new XmlBundler();
		String aspectPath = "src/test/resources/generic-bundler/bundler-structure-tests/" 
				+ APPLICATIONS_DIR +"/app1/main-aspect";
		bundler.getBundleFiles(new File(aspectPath), null, "xml.bundle.foo");
	}
}
