package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.WebXmlCompiler;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class WarCommand extends ArgsParsingCommandPlugin
{
	private ConsoleWriter out;
	private BRJS brjs;
	
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
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the name of the app being exported"));
		argsParser.registerParameter(new UnflaggedOption("war-location").setHelp("the name of the war file to create"));
		argsParser.registerParameter(new FlaggedOption("minifier").setShortFlag('m').setDefault("default").setHelp("the name of the minifier that will be used to compress the javascript"));
	}

	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		App app = brjs.app(parsedArgs.getString("app-name"));
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		exportWar(app, getWarLocation(app, parsedArgs), parsedArgs.getString("minifier"));
	}
	
	private void exportWar(App origApp, File warFile, String minifierName) throws CommandOperationException {
		try {
			List<ContentPlugin> contentPlugins = brjs.plugins().contentProviders();
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
					
					createAspectBundles(origAspect, warAspect, contentPlugins, origApp.appConf().getLocales().split(","));
				}
				
				FileUtility.zipFolder(warApp.dir(), warFile, true);
			}
			catch (ConfigException e) {
				throw new RuntimeException(e);
			}
			finally {
				warApp.delete();
			}
			
			out.println("Successfully created war file");
			out.println(" " + warFile.getAbsolutePath());
		}
		catch(InvalidNameException | ModelUpdateException | IOException | ParseException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private void createAspectBundles(Aspect origAspect, Aspect warAspect, List<ContentPlugin> contentPlugins, String[] locales) throws CommandOperationException {
		try {
			BundleSet bundleSet = warAspect.getBundleSet();
			
			for(ContentPlugin contentPlugin : contentPlugins) {
				for(String contentPath : contentPlugin.getValidProdContentPaths(bundleSet, locales)) {
					BladerunnerUri requestPath = new BladerunnerUri(brjs, warAspect.getApp().dir(), contentPath);
					ParsedContentPath parsedContentPath = contentPlugin.getContentPathParser().parse(requestPath);
					
					try(OutputStream outputStream = createBundleSpecificOutputStream(contentPath, warAspect.file(contentPath))) {
						contentPlugin.writeContent(parsedContentPath, bundleSet, outputStream);
					}
				}
			}
		}
		catch(IOException | RequestHandlingException | ModelOperationException e) {
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
