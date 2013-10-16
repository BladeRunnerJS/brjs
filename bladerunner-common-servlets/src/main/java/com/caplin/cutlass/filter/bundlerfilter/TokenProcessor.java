package com.caplin.cutlass.filter.bundlerfilter;

import java.util.Map;


public interface TokenProcessor
{
	public String process(String locale, String browser, Map<String, String> attributes) throws TokenProcessorException;
}