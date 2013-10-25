package com.caplin.cutlass.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;

//import com.caplin.cutlass.filter.productionFilePreventionFilter.ProductionFilePreventionFilter;

import com.caplin.cutlass.ServletModelAccessor;

public class BladerunnerDevFilters implements Filter
{
	private final List<Filter> immutableFilters;
	private Logger logger;
	
	public BladerunnerDevFilters() throws ServletException
	{
		List<Filter> mutableFilters = new ArrayList<Filter>();
		//mutableFilters.add(new ProductionFilePreventionFilter());
		
		immutableFilters = Collections.unmodifiableList(mutableFilters);
	}
	
	public void destroy()
	{
		for (Filter filter : immutableFilters)
		{
			filter.destroy();
		}
	}
	
	public void init(FilterConfig filterConfig) throws ServletException
	{
		BRJS brjs = ServletModelAccessor.initializeModel(filterConfig.getServletContext());
		logger = brjs.logger(LoggerType.FILTER, BladerunnerDevFilters.class);
		
		for (Filter filter : immutableFilters)
		{
			filter.init(filterConfig);
		}
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		logger.debug("=> " + this.getClass().getSimpleName() + " processing request for: " + httpRequest.getRequestURI());
		new VirtualFilterChain(chain, immutableFilters).doFilter(request, response);
	}
}
