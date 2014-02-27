package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.naming.InvalidNameException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtil;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.WebXmlCompiler;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class WarCommand extends ArgsParsingCommandPlugin
{
	private static List<String> indexFiles = Arrays.asList(new String[] {"index.html", "index.jsp"});
	private ConsoleWriter out;
	private BRJS brjs;
	private String defaultFileCharacterEncoding;
	
	@Override
	public void setBRJS(BRJS brjs) {
		try {
			this.brjs = brjs;
			out = brjs.getConsoleWriter();
			defaultFileCharacterEncoding = brjs.bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
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
				
				if (warApp.file("WEB-INF/web.xml").exists()) {
					WebXmlCompiler.compile(warApp.file("WEB-INF/web.xml"));
				}
				
				FileUtility.copyFileIfExists(origApp.file("app.conf"), warApp.file("app.conf"));
				
				FileUtil fileUtil = new FileUtil(defaultFileCharacterEncoding);
				for(Aspect origAspect : origApp.aspects()) {
					Aspect warAspect = warApp.aspect(origAspect.getName());
					
					for(String indexFile : indexFiles) {
						if(origAspect.file(indexFile).exists()) {
							FileUtils.copyFile(origAspect.file(indexFile), warAspect.file(indexFile));
							try(Writer writer = new OutputStreamWriter(new FileOutputStream(warAspect.file(indexFile)), brjs.bladerunnerConf().getDefaultFileCharacterEncoding())) {
								// TODO: stop only supporting the English locale within wars
								origAspect.filterIndexPage(fileUtil.readFileToString(origAspect.file(indexFile)), "en", writer, RequestMode.Prod);
							}
						}
					}
					
					FileUtility.copyDirectoryIfExists(origAspect.unbundledResources().dir(), warAspect.unbundledResources().dir());
					
					createAspectBundles(origAspect, warAspect, contentPlugins, origApp.appConf().getLocales().split(","));
				}
				
				FileUtility.zipFolder(warApp.dir(), warFile, true);
			}
			catch (ConfigException | ModelOperationException e) {
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
			BundleSet bundleSet = origAspect.getBundleSet();
			
			for(ContentPlugin contentPlugin : contentPlugins) {
				for(String contentPath : contentPlugin.getValidProdContentPaths(bundleSet, locales)) {
					BladerunnerUri requestPath = new BladerunnerUri(brjs, origAspect.getApp().dir(), contentPath);
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
			warFile = brjs.workingDir().file(app.getName() + ".war");
		}
		else {
			String warLocation = config.getString("war-location");
			File warLocationFile = new File(warLocation);
			
			if(!warLocationFile.isAbsolute()) {
				warLocationFile = brjs.workingDir().file(warLocation);
			}
			
			if(warLocation.endsWith(".war")) {
				warFile =  warLocationFile;
			}
			else if(!warLocationFile.isDirectory()) {
				warFile = new File(warLocationFile.getPath() + ".war");
			}
			else {
				warFile = new File(warLocationFile, app.getName() + ".war");
			}
		}
		
		return warFile;
	}
	
	private OutputStream createBundleSpecificOutputStream(String validBundlerRequest, File targetFile) throws CommandOperationException {		
		try {
			targetFile.getParentFile().mkdirs();
			FileOutputStream fileOutput = new FileOutputStream(targetFile);
			OutputStream fileStream = new BufferedOutputStream(fileOutput);
			
			//TODO: the war command shouldnt need knowledge of image.bundles - do we need a shouldBeGZipped method?
			if (validBundlerRequest.endsWith("image.bundle"))
			{
				return new GZIPOutputStream(fileStream);
			}
			
			return fileStream;
		}
		catch(IOException e) {
			throw new CommandOperationException(e);
		}
	}
}
