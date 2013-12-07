package com.caplin.cutlass.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.caplin.cutlass.ServletModelAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.filter.production.GZipContentEncodingFilter;
import com.caplin.cutlass.filter.production.ValidRequestForBundledResourceFilter;

public class BladerunnerProdFilters implements Filter
{
	private final List<Filter> filters = new ArrayList<Filter>();
	private Logger logger;
	
	public BladerunnerProdFilters() throws ServletException
	{
		filters.add(new ValidRequestForBundledResourceFilter());
		filters.add(new GZipContentEncodingFilter());
	}

	@Override
	public void destroy()
	{
		for (Filter filter : filters)
		{
			filter.destroy();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		BRJS brjs = ServletModelAccessor.initializeModel(filterConfig.getServletContext());
		logger = brjs.logger(LoggerType.SERVLET, BladerunnerProdFilters.class);
		
		for (Filter filter : filters)
		{
			filter.init(filterConfig);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		logger.debug("=> " + this.getClass().getSimpleName() + " processing request for: " + httpRequest.getRequestURI());
		new VirtualFilterChain(chain, filters).doFilter(request, response);
	}
}
