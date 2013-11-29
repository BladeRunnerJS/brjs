package org.bladerunnerjs.spec.brjs.appserver;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.core.plugin.content.AbstractContentPlugin;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class MockContentPlugin extends AbstractContentPlugin implements ContentPlugin
{
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("mock-servlet").as("request")
			.and("mock-servlet/some/other/path").as("long-request");
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("request"));
		prodRequestPaths.add(requestParser.createRequest("long-request"));
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getRequestPrefix() {
		return "mock";
	}
	
	@Override
	public String getMimeType()
	{
		return "some/mime";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return requestParser;
	}

	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		PrintWriter out = new PrintWriter(os);
		out.print(this.getClass().getCanonicalName());
		out.flush();
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

}
