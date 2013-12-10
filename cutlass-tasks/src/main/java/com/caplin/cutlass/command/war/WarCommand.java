package com.caplin.cutlass.command.war;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.naming.InvalidNameException;
import javax.xml.parsers.ParserConfigurationException;

import org.bladerunnerjs.console.ConsoleWriter;

import com.caplin.cutlass.LegacyFileBundlerPlugin;

import org.bladerunnerjs.model.AbstractAssetContainer;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;

import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.utility.WebXmlCompiler;
import org.xml.sax.SAXException;

import com.caplin.cutlass.bundler.css.CssBundler;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.image.ImageBundler;
import com.caplin.cutlass.bundler.js.JsBundler;
import com.caplin.cutlass.bundler.thirdparty.ThirdPartyBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;
import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class WarCommand extends AbstractPlugin implements LegacyCommandPlugin
{
	private final JSAP argsParser = new JSAP();
	private ConsoleWriter out;
	private BRJS brjs;
	
	{
		try {
			argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the name of the app being exported"));
			argsParser.registerParameter(new UnflaggedOption("war-location").setHelp("the name of the war file to create"));
			argsParser.registerParameter(new FlaggedOption("minifier").setShortFlag('m').setDefault("default").setHelp("the name of the minifier that will be used to compress the javascript"));
		}
		catch (Exception ex) {
			throw new RuntimeException("Error initialising argument parser", ex);
		}
	}
	
	public WarCommand(BRJS brjs) {
		this.brjs = brjs;
		out = brjs.getConsoleWriter();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		out = brjs.getConsoleWriter();
	}
	
	@Override
	public String getCommandName() {
		return "war";
	}
	
	@Override
	public String getCommandDescription()  {
		return "Package a specified application as a war file for deployment.";
	}
	
	@Override
	public String getCommandUsage() {
		return argsParser.getUsage();
	}

	@Override
	public String getCommandHelp() {
		return argsParser.getHelp();
	}
	
	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException  {
		// TODO: remove the need for these two lines by doing the argument parsing in BRJS.runCommand()
		JSAPResult config = argsParser.parse(args);
		if(!config.success()) throw new CommandArgumentsException("Invalid arguments provided.", this);
		
		App app = brjs.app(config.getString("app-name"));
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		exportWar(app, getWarLocation(app, config), config.getString("minifier"));
	}
	
	private void exportWar(App origApp, File warFile, String minifierName) throws CommandOperationException {
		try {
			ArrayList<LegacyFileBundlerPlugin> bundlers = new ArrayList<LegacyFileBundlerPlugin>(Arrays.asList(new JsBundler(minifierName),
				new XmlBundler(), new CssBundler(), new I18nBundler(), new HtmlBundler(), new ImageBundler(), new ThirdPartyBundler()));
			AppMetaData appMetaData = new AppMetaData(origApp);
			App warApp = brjs.app(origApp.getName() + "-war");
			
			try {
				warApp.create();
				
				FileUtility.copyDirectoryIfExists(origApp.file("WEB-INF"), warApp.file("WEB-INF"));
				warApp.file("WEB-INF/jetty-env.xml").delete();
				warApp.file("WEB-INF/lib/bladerunner-dev-servlets.jar").delete();
				
				WebXmlCompiler.compile(warApp.file("WEB-INF/web.xml"));
				FileUtility.copyFileIfExists(origApp.file("app.conf"), warApp.file("app.conf"));
				
				for(Aspect origAspect : origApp.aspects()) {
					Aspect warAspect = warApp.aspect(origAspect.getName());
					
					FileUtility.copyFileIfExists(origAspect.file("index.html"), warAspect.file("index.html"));
					FileUtility.copyFileIfExists(origAspect.file("index.jsp"), warAspect.file("index.jsp"));
					FileUtility.copyDirectoryIfExists(origAspect.unbundledResources().dir(), warAspect.unbundledResources().dir());
					
					createAspectBundles(origAspect, warAspect, bundlers, appMetaData);
				}
				
				FileUtility.zipFolder(warApp.dir(), warFile, true);
			}
			finally {
				warApp.delete();
			}
			
			out.println("Successfully created war file");
			out.println(" " + warFile.getAbsolutePath());
		}
		catch(InvalidNameException | ModelUpdateException | IOException | SAXException | ParserConfigurationException | ParseException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private void createAspectBundles(AbstractAssetContainer origAspect, AbstractAssetContainer warAspect, ArrayList<LegacyFileBundlerPlugin> bundlers, AppMetaData appMetaData) throws CommandOperationException {
		try {
			for(LegacyFileBundlerPlugin bundler : bundlers) {
				for(String bundleRequest : bundler.getValidRequestStrings(appMetaData)) {
					List<File> sourceFilesForBundling = bundler.getBundleFiles(origAspect.dir(), null, bundleRequest);
					
					if(sourceFilesForBundling.size() > 0) {
						try(OutputStream outputStream = createBundleSpecificOutputStream(bundleRequest, warAspect.file(bundleRequest))) {
							bundler.writeBundle(sourceFilesForBundling, outputStream);
						}
					}
				}
			}
		}
		catch(IOException | RequestHandlingException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private File getWarLocation(App app, JSAPResult config) {
		File warFile;
		
		if(!config.contains("war-location")) {
			warFile = new File(app.getName() + ".war");
		}
		else {
			String warPath = config.getString("war-location");
			
			if(warPath.endsWith(".war")) {
				warFile = new File(warPath);
			}
			else if(new File(warPath).isDirectory()) {
				warFile = new File(new File(warPath), app.getName() + ".war");
			}
			else {
				warFile = new File(warPath + ".war");
			}
		}
		
		return warFile;
	}
	
	private OutputStream createBundleSpecificOutputStream(String validBundlerRequest, File targetFile) throws CommandOperationException {
		OutputStream bundleStream = null;
		
		try {
			targetFile.getParentFile().mkdirs();
			@SuppressWarnings("resource")
			OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(targetFile));
			bundleStream = (validBundlerRequest.endsWith("image.bundle")) ? fileStream : new GZIPOutputStream(fileStream);
		}
		catch(IOException e) {
			throw new CommandOperationException(e);
		}
		
		return bundleStream;
	}
}
