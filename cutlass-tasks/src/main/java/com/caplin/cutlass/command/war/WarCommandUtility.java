package com.caplin.cutlass.command.war;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.sinbin.AppMetaData;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.WebXmlCompiler;
import org.xml.sax.SAXException;

import com.caplin.cutlass.bundler.css.CssBundler;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.image.ImageBundler;
import com.caplin.cutlass.bundler.js.JsBundler;
import com.caplin.cutlass.bundler.js.minification.MinifierFactoryException;
import com.caplin.cutlass.bundler.thirdparty.ThirdPartyBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;

public class WarCommandUtility
{
	private String minifierName;
	
	public WarCommandUtility()
	{
		this.minifierName = null;
	}
	
	public WarCommandUtility(String minifierName)
	{
		this.minifierName = minifierName;
	}

	public void copyWEBINFFolderToTemporaryDirectoryForWarCreation(File applicationWEBINFFolder, File temporaryDirectoryForWarCreation) throws IOException
	{
		File WEBINFCopyDirectory = new File(temporaryDirectoryForWarCreation, "WEB-INF");
		FileUtility.copyDirectoryContents(applicationWEBINFFolder, WEBINFCopyDirectory);
	}
	
	public void deleteJettyEnvConfigurationFromTemporaryDirectoryForWarCreation(File temporaryDirectoryForWarCreation)
	{
		File jettyEnvXml = new File(temporaryDirectoryForWarCreation, "WEB-INF/jetty-env.xml");
		
		if(jettyEnvXml.exists())
		{
			jettyEnvXml.delete();
		}
	}
	
	public void deleteBladeRunnerDevServletsFromTemporaryDirectoryForWarCreation(File temporaryDirectoryForWarCreation)
	{
		File bladeRunnerDevServletsJar = new File(temporaryDirectoryForWarCreation, "WEB-INF/lib/bladerunner-dev-servlets.jar");
		
		if(bladeRunnerDevServletsJar.exists())
		{
			bladeRunnerDevServletsJar.delete();
		}
	}
	
	public void rewriteApplicationWebxmlFileToAddInProdAspectsRemoveDevAspectsAndInjectAppVersionToken(File temporaryDirectoryForWarCreation) throws IOException, ParseException
	{
		WebXmlCompiler.compile(new File(temporaryDirectoryForWarCreation, "WEB-INF/web.xml"));
	}
	
	public void copyAppConf(File applicationToWarDirectory, File temporaryDirectoryForWarCreation) throws IOException
	{
		FileUtils.copyFile(new File(applicationToWarDirectory, CutlassConfig.APP_CONF_FILENAME), new File(temporaryDirectoryForWarCreation, CutlassConfig.APP_CONF_FILENAME));
	}
	
	public void copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(List<File> applicationAspects, File temporaryDirectoryForWarCreation) throws IOException
	{
		for(File aspectOfApplicationToWar : applicationAspects)
		{
			File aspectOfApplicationToWarIndexHTML = new File(aspectOfApplicationToWar, "index.html");
			File aspectOfApplicationToWarIndexJSP = new File(aspectOfApplicationToWar, "index.jsp");
			File aspectOfApplicationToWarUnbundledResources = new File(aspectOfApplicationToWar, "unbundled-resources");
			File aspectOfApplicationToWarUnbundledResourcesCopy = new File(temporaryDirectoryForWarCreation, aspectOfApplicationToWar.getName());
			
			aspectOfApplicationToWarUnbundledResourcesCopy.mkdir();
			
			copyApplicationAspectFilesToApplicationAspectInWar(aspectOfApplicationToWarIndexHTML, aspectOfApplicationToWarUnbundledResourcesCopy);
			copyApplicationAspectFilesToApplicationAspectInWar(aspectOfApplicationToWarIndexJSP, aspectOfApplicationToWarUnbundledResourcesCopy);
			copyApplicationAspectFilesToApplicationAspectInWar(aspectOfApplicationToWarUnbundledResources, aspectOfApplicationToWarUnbundledResourcesCopy);
		}
	}
	
	public void writeOutToWarDirectoryGZippedFileBundle(LegacyFileBundlerPlugin bundler, AppMetaData appMetaData, File applicationAspect, File temporaryDirectoryForWarCreation) throws RequestHandlingException, FileNotFoundException, IOException
	{
		List<String> validBundlerRequests = bundler.getValidRequestStrings(appMetaData);
		
		for(String validBundlerRequest : validBundlerRequests)
		{
			List<File> sourceFilesForBundling = bundler.getBundleFiles(applicationAspect, null, validBundlerRequest);
			
			if(sourceFilesForBundling.size() > 0)
			{
				File targetFile = new File(temporaryDirectoryForWarCreation, applicationAspect.getName() + File.separator + validBundlerRequest);
				targetFile.getParentFile().mkdirs();
				OutputStream outputStream = this.createBundleSpecificOutputStream(validBundlerRequest, targetFile);
				
				bundler.writeBundle(sourceFilesForBundling, outputStream);
				outputStream.close();
			}
		}
	}
	
	public void writeOutToWarDirectoryAllGZippedBundles(List<File> applicationAspects, AppMetaData appMetaData, File temporaryDirectoryForWarCreation) throws IOException, RequestHandlingException, ParserConfigurationException, SAXException, CommandArgumentsException
	{
		ArrayList<LegacyFileBundlerPlugin> bundlers = new ArrayList<LegacyFileBundlerPlugin>(Arrays.asList(createJsBundler(), new XmlBundler(), new CssBundler(), 
				new I18nBundler(), new HtmlBundler(), new ImageBundler(), new ThirdPartyBundler()));
		
		for(LegacyFileBundlerPlugin bundler : bundlers)
		{
			for(File applicationAspect : applicationAspects)
			{
				this.writeOutToWarDirectoryGZippedFileBundle(bundler, appMetaData, applicationAspect, temporaryDirectoryForWarCreation);
			}
		}
	}
	
	public void zipUpTemporaryDirectoryIntoWarAndDeleteTemporaryWarCreationDirectory(File temporaryDirectoryForWarCreation, File applicationToWar) throws IOException
	{
		FileUtility.zipFolder(temporaryDirectoryForWarCreation, applicationToWar, true);
		
		FileUtility.deleteDirContent(temporaryDirectoryForWarCreation);
		temporaryDirectoryForWarCreation.delete();
	}

	private void copyApplicationAspectFilesToApplicationAspectInWar(File applicationAspectFilesToCopy, File applicationAspectFilesToWar) throws IOException
	{
		if(applicationAspectFilesToCopy.exists())
		{
			File aspectOfApplicationToWarUnbundledResourcesCopyDirectory = new File(applicationAspectFilesToWar, applicationAspectFilesToCopy.getName());
			FileUtility.copyDirectoryContents(applicationAspectFilesToCopy, aspectOfApplicationToWarUnbundledResourcesCopyDirectory);
		}
	}
	
	@SuppressWarnings("resource")
	private OutputStream createBundleSpecificOutputStream(String validBundlerRequest, File targetFile) throws IOException
	{
		OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(targetFile));
		return (validBundlerRequest.endsWith("image.bundle")) ? fileStream : new GZIPOutputStream(fileStream);
	}
	
	private LegacyFileBundlerPlugin createJsBundler() throws CommandArgumentsException
	{
		try
		{
			return new JsBundler(minifierName);
		}
		catch(MinifierFactoryException e)
		{
			throw new CommandArgumentsException(e.getMessage(), null);
		}
	}
}
