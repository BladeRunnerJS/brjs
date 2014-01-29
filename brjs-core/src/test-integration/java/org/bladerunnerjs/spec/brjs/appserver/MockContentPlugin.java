package org.bladerunnerjs.spec.brjs.appserver;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class MockContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("mock-servlet").as("request")
				.and("mock-servlet/some/other/path").as("long-request");
			
			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest("request"));
			prodRequestPaths.add(contentPathParser.createRequest("long-request"));
		}
		catch(MalformedTokenException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getRequestPrefix() {
		return "mock-servlet";
	}

	@Override
	public String getGroupName() {
		return "some/mime";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		PrintWriter out = new PrintWriter(os);
		out.print(this.getClass().getCanonicalName());
		out.flush();
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

}
