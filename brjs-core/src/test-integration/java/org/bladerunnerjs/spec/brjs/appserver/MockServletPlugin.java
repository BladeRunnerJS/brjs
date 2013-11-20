package org.bladerunnerjs.spec.brjs.appserver;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class MockServletPlugin implements ServletPlugin
{
	private RequestParser requestParser;
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
	public String getMimeType()
	{
		return "some/mime";
	}

	@Override
	public RequestParser getRequestParser()
	{
		return requestParser;
	}

	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		PrintWriter out = new PrintWriter(os);
		out.print(this.getClass().getCanonicalName());
		out.flush();
	}

}
