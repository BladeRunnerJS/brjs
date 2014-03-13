package com.caplin.cutlass.filter.bundlerfilter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bladerunnerjs.appserver.CharResponseWrapper;

import com.caplin.cutlass.ServletModelAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.filter.bundlerfilter.token.CSSBundleTokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.token.I18nBundleTokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.token.JSBundleTokenProcessor;

public class BundlerTokenFilter implements Filter
{
	private static final String INDEX_HTML = "index.html";
	
	private final BundlerTokenProcessor tokenProcessor;
	private ServletContext servletContext;
	private BRJS brjs;
	private Logger logger;
	
	public BundlerTokenFilter()
	{
		tokenProcessor = new BundlerTokenProcessor();
		tokenProcessor.addTokenProcessor(CutlassConfig.CSS_BUNDLE_TOKEN, new CSSBundleTokenProcessor());
		tokenProcessor.addTokenProcessor(CutlassConfig.JS_BUNDLE_TOKEN, new JSBundleTokenProcessor());
		tokenProcessor.addTokenProcessor(CutlassConfig.I18N_BUNDLE_TOKEN, new I18nBundleTokenProcessor());
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeAndGetModel(filterConfig.getServletContext());
		servletContext = filterConfig.getServletContext();
		logger = brjs.logger(LoggerType.FILTER, BundlerTokenFilter.class);
	}
	
	@Override
	public void destroy()
	{
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		File appRoot = new File(servletContext.getRealPath("/"));
		
		try
		{
			if(shouldProcessResponse(httpRequest, appRoot))
			{
				try(ServletOutputStream out = response.getOutputStream())
				{
					AppConf appConf = null;
					
					appConf = AppConf.getConf(appRoot);
					
					CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse) response);			
					chain.doFilter(request, responseWrapper);
					
					logger.debug("processing and replacing bundler tokens within response.");
					
					StringBuffer filteredResponse = tokenProcessor.replaceTokens(appConf, httpRequest, responseWrapper.getReader());
					byte[] filteredData = filteredResponse.toString().getBytes("UTF-8");
					response.setContentLength(filteredData.length);
					out.write(filteredData);
				}
			}
			else
			{
				logger.debug("bundler token replacement not applicable for this resource.");
				
				chain.doFilter(request, response);
			}
		}
		catch(IOException|ServletException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}
	
	private boolean shouldProcessResponse(HttpServletRequest httpRequest, File appRoot) throws MalformedRequestException
	{
		BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, httpRequest);
		String requestUrl = bladerunnerUri.getInternalPath();
		
		return requestUrl.endsWith(INDEX_HTML) || requestUrl.endsWith("/");
	}
}