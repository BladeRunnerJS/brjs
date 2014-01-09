package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.utility.TagPluginUtility;


public class BRJSServletFilter implements Filter
{
	private List<String> filterForUrlFilenames = Arrays.asList("index.html");

	private ServletContext servletContext;
	private BRJSServletUtils servletUtils;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		servletContext = filterConfig.getServletContext();
		ServletModelAccessor.initializeModel(servletContext);
		servletUtils = new BRJSServletUtils();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		try {
			BRJS brjs = ServletModelAccessor.aquireModel();
			
			if ( !(req instanceof HttpServletRequest))
			{
				throw new ServletException(this.getClass().getSimpleName()+" can only handle HTTP requests.");
			}
			HttpServletRequest request = (HttpServletRequest) req;		
			HttpServletResponse response = (HttpServletResponse) resp;		
			
			BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(brjs, servletContext, request);
			boolean brjsPluginCanHandleRequest = servletUtils.getContentPluginForRequest(brjs, bladerunnerUri) != null;
			
			if (brjsPluginCanHandleRequest && !BladerunnerUri.isBrjsUriRequest(request))
			{
				request.getRequestDispatcher("/brjs"+request.getRequestURI()).forward(request, response);
			}
			else
			{
				doFiltering(brjs, request, response, chain);
			}
		}
		finally {
			ServletModelAccessor.releaseModel();
		}
	}

	@Override
	public void destroy()
	{
	}
	
	
	
	
	private void doFiltering(BRJS brjs, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if (shouldFilterResponse(request))
		{
			try
			{
				StringWriter tagPluginStringWriter = new StringWriter();
				BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(brjs, servletContext, request);
				App app = servletUtils.getAppForRequest(brjs, bladerunnerUri, response);
				BundlableNode bundleableNode = servletUtils.getBundableNodeForRequest(brjs, bladerunnerUri, response);

				if (bundleableNode != null)
				{
					CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
					
					chain.doFilter(request, responseWrapper);
					
					String responseData = getResponseData(responseWrapper);
					String locale = LocaleHelper.getLocaleFromRequest(app, request);
					TagPluginUtility.filterContent(responseData, bundleableNode.getBundleSet(), tagPluginStringWriter, RequestMode.Dev, locale);
					
					byte[] filteredData = tagPluginStringWriter.toString().getBytes();
					response.setContentLength(filteredData.length);
					response.getOutputStream().write(filteredData);
				}
				else
				{
					chain.doFilter(request, response);
				}
			}
			catch (MalformedRequestException ex)
			{
				servletUtils.sendErrorResponse(response, 404, ex);				
			}
			catch (Exception ex)
			{
				servletUtils.sendErrorResponse(response, 500, ex);
			}
		}
		else
		{
			chain.doFilter(request, response);
		}
	}
	
	private String getResponseData(CharResponseWrapper responseWrapper) throws IOException, UnsupportedEncodingException
	{
		StringWriter bufferedResponseStringWriter = new StringWriter();
		IOUtils.copy(responseWrapper.getReader(), bufferedResponseStringWriter);
		String responseData = bufferedResponseStringWriter.toString();
		return responseData;
	}

	private boolean shouldFilterResponse(HttpServletRequest request)
	{
		String urlFileName = FilenameUtils.getName(request.getRequestURI());
		return filterForUrlFilenames.contains(urlFileName);
	}

}
