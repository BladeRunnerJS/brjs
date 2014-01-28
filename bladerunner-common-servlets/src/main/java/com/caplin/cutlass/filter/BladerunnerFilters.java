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

import com.caplin.cutlass.filter.bundlerfilter.BundlerTokenFilter;
import com.caplin.cutlass.filter.bundlerfilter.contenttype.BundlerContentTypeFilter;
import com.caplin.cutlass.filter.sectionfilter.SectionRedirectFilter;
import com.caplin.cutlass.filter.thirdpartyfilter.ThirdPartyResourceFilter;
import com.caplin.cutlass.filter.tokenfilter.TokenisingServletFilter;
import com.caplin.cutlass.filter.versionfilter.VersionRedirectFilter;

public class BladerunnerFilters implements Filter
{
	private final List<Filter> filters = new ArrayList<Filter>();
	private Logger logger;
	
	public BladerunnerFilters() throws ServletException
	{
		filters.add(new ThirdPartyResourceFilter());
		filters.add(new VersionRedirectFilter());
		filters.add(new SectionRedirectFilter());
		filters.add(new BundlerTokenFilter());
		filters.add(new TokenisingServletFilter());
		filters.add(new BundlerContentTypeFilter());
		filters.add(new CharacterEncodingFilter());
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		BRJS brjs = ServletModelAccessor.initializeAndGetModel(filterConfig.getServletContext());
		logger = brjs.logger(LoggerType.FILTER, SectionRedirectFilter.class);
		
		for (Filter filter : filters)
		{
			filter.init(filterConfig);
		}
	}
	
	@Override
	public void destroy()
	{
		ServletModelAccessor.destroy();
		
		for (Filter filter : filters)
		{
			filter.destroy();
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		if(httpRequest.getAttribute("logicalRequestUri") == null)
		{
			httpRequest.setAttribute("logicalRequestUri", httpRequest.getRequestURI());
		}
		
		logger.debug("=> " + this.getClass().getSimpleName() + " processing request for: " + httpRequest.getRequestURI());
		new VirtualFilterChain(chain, filters).doFilter(request, response);
	}

}
