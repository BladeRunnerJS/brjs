package com.caplin.cutlass.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.ServletModelAccessor;

public class CharacterEncodingFilter implements Filter
{
	private Logger logger;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		BRJS brjs = ServletModelAccessor.initializeAndGetModel(filterConfig.getServletContext());
		logger = brjs.logger(LoggerType.FILTER, CharacterEncodingFilter.class);
	}
	
	@Override
	public void destroy()
	{
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		logger.debug("setting character encoding to 'UTF-8'");
		
		// TODO: this should actually only be done after the response has come back, and once we know that this is actually character
		// stream data, but this would be too costly using the current filter set-up
		response.setCharacterEncoding("UTF-8");
		chain.doFilter(request, response);
	}
}
