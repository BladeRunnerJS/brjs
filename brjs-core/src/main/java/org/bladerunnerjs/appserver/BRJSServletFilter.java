package org.bladerunnerjs.appserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.plugin.ContentPlugin;

import com.google.common.base.Joiner;


public class BRJSServletFilter implements Filter
{	
	private ServletContext servletContext;
	private Pattern contentPluginPrefixPattern;
	private BRJS brjs;
	private App app;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		servletContext = filterConfig.getServletContext();
		ServletModelAccessor.initializeModel(servletContext);
		
		try {
			brjs = ServletModelAccessor.aquireModel();
			List<String> pluginRequestPrefixes = new ArrayList<>();
			
			app = brjs.locateAncestorNodeOfClass(new File(servletContext.getRealPath(".")), App.class);
			
			for(ContentPlugin  contentPlugin : brjs.plugins().contentProviders()) {
				pluginRequestPrefixes.add(contentPlugin.getRequestPrefix());
			}
			
			contentPluginPrefixPattern = Pattern.compile("^.*/([a-zA-Z0-9_-]+-aspect|workbench)/" + "(" + Joiner.on("|").join(pluginRequestPrefixes) + ")(/.*)?$");
		}
		finally {
			ServletModelAccessor.releaseModel();
		}
	}
	
	@Override
	public void destroy()
	{
		ServletModelAccessor.destroy();
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String servletPath = request.getServletPath();
		
		// TODO: get rid of the `servletPath.endsWith(".bundle")` guard once we drop support for the old bundlers
		if(servletPath.equals("/brjs") || servletPath.endsWith(".bundle")) {
			chain.doFilter(request, response);
		}
		else {
			String requestPath = request.getRequestURI().replaceFirst("^" + request.getContextPath(), "");
			Matcher contentPluginPrefixMatcher = contentPluginPrefixPattern.matcher(requestPath);
			
			if(requestPath.endsWith("/index.html") || requestPath.endsWith("/index.jsp")) {
				filterIndexPage(request, response, chain);
			}
			else if(contentPluginPrefixMatcher.matches()) {
				request.getRequestDispatcher("/brjs" + requestPath).forward(request, response);
			}
			else {
				chain.doFilter(request, response);
			}
		}
	}
	
	private void filterIndexPage(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
		try
		{
			CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
			
			chain.doFilter(request, responseWrapper);
			
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, request);
			String locale = LocaleHelper.getLocaleFromRequest(app, request);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, brjs.bladerunnerConf().getDefaultOutputEncoding()))
			{
				BrowsableNode browsableNode = (BrowsableNode) app.getBundlableNode(bladerunnerUri);
				
				browsableNode.filterIndexPage(getIndexPage(responseWrapper), locale, writer, RequestMode.Dev);
			}
			
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			response.setContentLength(byteArray.length);
			response.getOutputStream().write(byteArray);
		}
		catch (Exception ex)
		{
			response.sendError(500, ex.toString());
		}
	}
	
	private String getIndexPage(CharResponseWrapper responseWrapper) throws IOException, UnsupportedEncodingException {
		StringWriter bufferedResponseStringWriter = new StringWriter();
		
		try(Reader reader = responseWrapper.getReader()) {
			IOUtils.copy(reader, bufferedResponseStringWriter);
		}
		
		return bufferedResponseStringWriter.toString();
	}
}
