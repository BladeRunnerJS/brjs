package org.bladerunnerjs.core.plugin.command.standard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.plugin.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.utility.ProcessLogger;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

public class JsDocCommand extends ArgsParsingCommandPlugin {
	public class Messages {
		public static final String API_DOCS_GENERATED_MSG = "API docs correctly generated in '%s'";
	}
	
	private static final Runtime runTime = Runtime.getRuntime();
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	public void setBRJS(BRJS brjs) {	
		this.brjs = brjs;
		out = brjs.getConsoleWriter();
	}
	
	@Override
	public String getCommandName() {
		return "jsdoc";
	}
	
	@Override
	public String getCommandDescription() {
		return "Generate JsDocs for a given application";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application for which jsdoc will be generated"));
		argsParser.registerParameter(new Switch("verbose-flag").setShortFlag('v').setDefault("false").setHelp("display more console output while generating the jsdoc output"));
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		boolean isVerbose = parsedArgs.getBoolean("verbose-flag");
		App app = brjs.app(appName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		File outputDir = app.storageDir("jsdoc-toolkit");
		runCommand(generateCommand(app, isVerbose, outputDir), outputDir);
	}
	
	private void runCommand(List<String> command, File outputDir) throws CommandOperationException {
		try {
			Process process = runTime.exec(command.toArray(new String[0]));
			ProcessLogger processLogger = new ProcessLogger(brjs, process, null);
			int exitCode = waitForProcess(process);
			processLogger.waitFor();
			
			if(exitCode != 0) throw new CommandOperationException("Error while running command '" + command + "' (" + exitCode + ")");
			
			replaceBuildDateToken(new File(outputDir, "index.html"));
			out.println(Messages.API_DOCS_GENERATED_MSG, outputDir.getPath());
		}
		catch(IOException | InterruptedException | ConfigException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private List<String> generateCommand(App app, boolean isVerbose, File outputDir) {
		List<String> command = new ArrayList<>();
		
		try {
			File jsdocToolkitInstallDir = installJsdocToolkit();
			File jsDocToolkitDir = new File(jsdocToolkitInstallDir, "jsdoc-toolkit");
			File jsDocTemplatesDir = new File(jsdocToolkitInstallDir, "jsdoc-templates/Caplin");
			List<String> libraryPaths = new ArrayList<>();
			
			for (JsLib jsLib : app.jsLibs()) {
				libraryPaths.add(jsLib.file("src").getCanonicalFile().getAbsolutePath());
			}
			
			// See <https://code.google.com/p/jsdoc-toolkit/wiki/CmdlineOptions>
			command.add("java");
			command.add("-jar");
			command.add(new File(jsDocToolkitDir, "jsrun.jar").getAbsolutePath());
			command.add(new File(jsDocToolkitDir, "app/run.js").getAbsolutePath());
			command.addAll(libraryPaths);
			command.add("-r=20");
			command.add("-t=" + jsDocTemplatesDir.getAbsolutePath());
			command.add("-d=" + outputDir.getAbsolutePath());
			command.add((isVerbose) ? "-v" : "-q");
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return command;
	}
	
	private File installJsdocToolkit() {
		File installDir = brjs.storageDir("jsdoc-toolkit/install");
		
		if(!installDir.exists()) {
			installDir.mkdirs();
			
			try (InputStream jsdocZipStream = getClass().getClassLoader().getResourceAsStream("org/bladerunnerjs/core/plugin/command/standard/jsdoc-toolkit.zip")) {
				unzipInputStream(jsdocZipStream, installDir);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return installDir;
	}
	
	private void unzipInputStream(InputStream zipArchiveInputStream, File targetDir) {
		try (ZipInputStream zipEntryInputStream = new ZipInputStream(zipArchiveInputStream)) {
			ZipEntry entry;
			
			while ((entry = zipEntryInputStream.getNextEntry()) != null) {
				File targetFile = new File(targetDir, entry.getName());
				
				if(entry.isDirectory()) {
					targetFile.mkdirs();
				}
				else {
					try (FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile)) {
						IOUtils.copy(zipEntryInputStream, targetFileOutputStream);
					}
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private int waitForProcess(Process process) throws IOException, InterruptedException {
		try(InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
		{
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				out.println(line);
			}
		}
		
		// TODO: this code looks like a bug -- investigate
		int exitCode = process.waitFor();
		process.waitFor();
		
		return exitCode;
	}
	
	private void replaceBuildDateToken(File indexFile) throws IOException, ConfigException {
		String fileContent = FileUtils.readFileToString(indexFile, brjs.bladerunnerConf().getDefaultOutputEncoding());
		
		DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
		Date date = new Date();
		
		String resultFileContent = fileContent.replace("@buildDate@", dateFormat.format(date));
		FileUtils.writeStringToFile(indexFile, resultFileContent, brjs.bladerunnerConf().getDefaultOutputEncoding());
	}
}
