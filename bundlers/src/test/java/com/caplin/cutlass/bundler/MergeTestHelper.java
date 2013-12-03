package com.caplin.cutlass.bundler;

import static com.caplin.cutlass.bundler.BundlerTestUtils.convertToList;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.caplin.cutlass.EncodingAccessor;
import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.util.FileUtility;

public class MergeTestHelper
{
	private String actualBundle;
	private LegacyFileBundlerPlugin bundler;
	
	public MergeTestHelper(LegacyFileBundlerPlugin bundler)
	{
		this.bundler = bundler;
	}
	
	public MergeTestHelper givenInputFiles(String[] inputFileArray) throws IOException, RequestHandlingException
	{
		List<File> inputFiles = convertToList(inputFileArray);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bundler.writeBundle(inputFiles, outputStream);
		actualBundle = FileUtility.normalizeLineEndings(outputStream.toString(EncodingAccessor.getDefaultOutputEncoding()));
		
		return this;
	}
	
	public void thenBundleIsCreated(String expectedBundleFile) throws IOException
	{
		String expectedBundle = FileUtility.normalizeLineEndings(FileUtils.readFileToString(new File(expectedBundleFile)));
		assertEquals("1a", expectedBundle, actualBundle);
	}
}
