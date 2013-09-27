package com.caplin.cutlass.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.ServletModelAccessor;
import com.caplin.cutlass.bundler.css.CssBundler;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import com.caplin.cutlass.bundler.exception.UnknownBundlerException;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.image.ImageBundler;
import com.caplin.cutlass.bundler.js.JsBundler;
import com.caplin.cutlass.bundler.thirdparty.ThirdPartyBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;

public class BundlerServlet extends HttpServlet
{
	private static final long serialVersionUID = -3359840788356811425L;
	
	protected List<LegacyFileBundlerPlugin> bundlers = null;
	
	private BRJS brjs;
	private Logger logger;
	private ServletContext servletContext;
	
	public BundlerServlet() throws Exception
	{
		bundlers = Arrays.asList(new JsBundler(), new XmlBundler(), new HtmlBundler(),
			new I18nBundler(), new CssBundler(), new ImageBundler(), new ThirdPartyBundler());
	}

	protected BundlerServlet(List<LegacyFileBundlerPlugin> bundlers)
	{
		this.bundlers = bundlers;
	}

	@Override
	public void init(final ServletConfig servletConfig) throws ServletException
	{
		brjs = ServletModelAccessor.initializeModel(servletConfig.getServletContext());
		logger = brjs.logger(LoggerType.SERVLET, BundlerServlet.class);
		
		servletContext = servletConfig.getServletContext();
		servletContext.setAttribute(CutlassConfig.DEV_MODE_FLAG, "true");
		super.init(servletConfig);
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
	{
		String requestPath = "";
		ByteArrayOutputStream cachedBundlerOutputStream = new ByteArrayOutputStream();
		
		try
		{
			BladerunnerUri bladerunnerUri = new BladerunnerUri(brjs, servletContext, request);
			requestPath = bladerunnerUri.logicalPath;
			File baseDir = new File(servletContext.getRealPath(bladerunnerUri.scopePath));
			
			logger.debug("BASE DIR " + baseDir);
			logger.debug("REQUEST PATH " + requestPath);
			
			LegacyFileBundlerPlugin theBundler = getBundlerForRequest(request);
			List<File> bundleFiles = getBundleFiles(theBundler, baseDir, requestPath);
			
			writeBundle(theBundler, bundleFiles, cachedBundlerOutputStream, requestPath);
		} 
		catch (MalformedRequestException ex)
		{
			sendErrorResponse(response, 400, ex);
		}
		catch (ResourceNotFoundException ex)
		{
			sendErrorResponse(response, 404, ex);
		}
		catch (BundlerProcessingException ex)
		{
			sendErrorResponse(response, 500, ex);
		}
		catch (RequestHandlingException bundlerException)
		{
			throw new UnknownBundlerException(bundlerException);
		} 
		
		
		try {
			cachedBundlerOutputStream.writeTo(response.getOutputStream());
			cachedBundlerOutputStream.close();
			response.getOutputStream().close();
		}
		catch (IOException ex)
		{
			logger.debug("Output stream closed: unable to write response for '" + requestPath + "'.");
		}
		
	}
	

	private void sendErrorResponse(HttpServletResponse response, int code, RequestHandlingException bundlerException)
	{
		try {
			response.sendError(code, bundlerException.toString());
		}
		catch (IOException ex)
		{
			/* we wrap the bundler exception so if we cant write to the output it still goes to the console */
			throw new UnknownBundlerException(bundlerException);
		}
	}
	
	private LegacyFileBundlerPlugin getBundlerForRequest(HttpServletRequest request) throws MalformedRequestException
	{
		String requestPath = request.getRequestURI();
		String queryString = (request.getQueryString() != null) ? "?" + request.getQueryString() : "";
		String requestUrl = request.getRequestURL().append(queryString).toString();
		String requestFilename = StringUtils.substringAfterLast(requestPath, "/");
		for (LegacyFileBundlerPlugin bundler : bundlers)
		{
			String bundlerFileExtension = bundler.getBundlerExtension();
			
			if (requestFilename.equals(bundlerFileExtension) || requestFilename.endsWith("_" + bundlerFileExtension))
			{
				return bundler;
			}
		}
		throw new MalformedRequestException(requestUrl, getUnknownRequestHandlingExceptionString());
	}

	private String getUnknownRequestHandlingExceptionString()
	{
		StringBuilder exceptionString = new StringBuilder();
		exceptionString.append("\n");
		exceptionString.append("No bundler was found to handle the request. Valid bundler paths are:\n");
		
		for(LegacyFileBundlerPlugin bundler : bundlers)
		{
			for(String requestForm : bundler.getValidRequestForms())
			{
				exceptionString.append("	" + requestForm + "\n");
			}
		}
		
		return exceptionString.toString();
	}

	private List<File> getBundleFiles(LegacyFileBundlerPlugin theBundler, File baseDir, String requestPath) throws RequestHandlingException
	{
		long startTime = System.nanoTime();
		List<File> bundleFiles = theBundler.getBundleFiles(baseDir, null, requestPath);
		logExecutionTime(theBundler.getClass().getSimpleName() + ".getBundleFiles()", requestPath, startTime, System.nanoTime());
		
		return bundleFiles;
	}

	private void writeBundle(LegacyFileBundlerPlugin theBundler, List<File> bundleFiles, OutputStream outputStream, String requestPath) throws RequestHandlingException
	{
		long startTime = System.nanoTime();
		theBundler.writeBundle(bundleFiles, outputStream);
		logExecutionTime(theBundler.getClass().getSimpleName() + ".writeBundle()", requestPath, startTime, System.nanoTime());
	}
	
	private void logExecutionTime(String methodName, String requestPath, long startTime, long endTime)
	{
		long totalTimeMs = (endTime - startTime)/1000000;
		logger.debug(methodName + " took " + totalTimeMs + " ms for requestPath: " + requestPath);
	}
}
