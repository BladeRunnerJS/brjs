package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.bladerunnerjs.appserver.filter.BRJSHeaderFilter;
import org.bladerunnerjs.appserver.filter.TokenisingServletFilter;
import org.bladerunnerjs.appserver.util.VirtualFilterChain;

public class BRJSServletFilter implements Filter {
	private final List<Filter> filters = new ArrayList<Filter>();
	
	public BRJSServletFilter() throws ServletException {
		filters.add(new TokenisingServletFilter());
		filters.add(new BRJSHeaderFilter());
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		for (Filter filter : filters) {
			filter.init(filterConfig);
		}
	}
	
	@Override
	public void destroy() {
		for (Filter filter : filters) {
			filter.destroy();
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		new VirtualFilterChain(chain, filters).doFilter(request, response);
	}
}
