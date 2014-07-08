package org.bladerunnerjs.appserver.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class VirtualFilterChain implements FilterChain {
	private final FilterChain originalChain;
	private final List<Filter> additionalFilters;
	private int currentPosition = 0;
	
	public VirtualFilterChain(FilterChain chain, List<Filter> additionalFilters) {
		this.originalChain = chain;
		this.additionalFilters = additionalFilters;
	}
	
	public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
		if (currentPosition == additionalFilters.size()) {
			originalChain.doFilter(request, response);
		}
		else {
			currentPosition++;
			Filter nextFilter = additionalFilters.get(currentPosition - 1);
			nextFilter.doFilter(request, response, this);
		}
	}
}
