package com.caplin.cutlass.filter.bundlerfilter.token;

import java.util.Map;

import com.caplin.cutlass.filter.bundlerfilter.TokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.TokenProcessorException;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class JSBundleTokenProcessor implements TokenProcessor
{

	@Override
	public String process(String locale, String browser, Map<String, String> attributes) throws TokenProcessorException
	{
		return "<script type=\"text/javascript\" src=\"" + BundlePathsFromRoot.JS + "js.bundle\"></script>";
	}

}
