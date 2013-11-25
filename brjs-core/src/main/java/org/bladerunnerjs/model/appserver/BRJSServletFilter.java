package org.bladerunnerjs.model.appserver;

import java.io.IOException;
import java.io.StringWriter;
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
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.utility.TagPluginUtility;


public class BRJSServletFilter implements Filter
{
	private List<String> filterForUrlFilenames = Arrays.asList("index.html");

	private ServletContext servletContext;
	private BRJSServletUtils servletUtils;
	private BRJS brjs;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		servletContext = filterConfig.getServletContext();
		brjs = ServletModelAccessor.initializeModel(servletContext);
		servletUtils = new BRJSServletUtils(brjs);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		if ( !(req instanceof HttpServletRequest))
		{
			throw new ServletException(this.getClass().getSimpleName()+" can only handle HTTP requests.");
		}
		HttpServletRequest request = (HttpServletRequest) req;		
		HttpServletResponse response = (HttpServletResponse) resp;		
		
		
		BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(servletContext, request);
		boolean brjsPluginCanHandleRequest = servletUtils.getContentPluginForRequest(bladerunnerUri) != null;
		
		if (brjsPluginCanHandleRequest && !BladerunnerUri.isBrjsUriRequest(request))
		{
			request.getRequestDispatcher("/brjs"+request.getRequestURI()).forward(request, response);
		}
		else
		{
			doFiltering(request, response, chain);
		}
		
	}

	private void doFiltering(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		List<TagHandlerPlugin> tagHandlers = brjs.tagHandlers();
		
		CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
		chain.doFilter(request, responseWrapper);
		
		doIndexTagHandlerFiltering(tagHandlers, request, response, responseWrapper);
	}

	private void doIndexTagHandlerFiltering(List<TagHandlerPlugin> tagHandlers, HttpServletRequest request, HttpServletResponse response, CharResponseWrapper responseWrapper)
	{
		String urlFileName = FilenameUtils.getName(request.getRequestURI());
		if (filterForUrlFilenames.contains(urlFileName))
		{
			StringWriter bufferedResponseStringWriter = new StringWriter();
			try
			{
				IOUtils.copy(responseWrapper.getReader(), bufferedResponseStringWriter);
				String responseData = bufferedResponseStringWriter.toString();
			
				StringWriter tagPluginStringWriter = new StringWriter();
				BladerunnerUri bladerunnerUri = servletUtils.createBladeRunnerUri(servletContext, request);
				TagPluginUtility.filterContent(responseData, servletUtils.getBundableNodeForRequest(bladerunnerUri, response).getBundleSet(), tagPluginStringWriter, RequestMode.Dev, "");
				
				byte[] filteredData = tagPluginStringWriter.toString().getBytes();
				response.setContentLength(filteredData.length);
				response.getOutputStream().write(filteredData);
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void destroy()
	{
	}

}
