package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.utility.ProcessLogger;

public class CommandRunnerUtility {
	
	public static void runCommand(BRJS brjs, ProcessBuilder processBuilder) throws CommandOperationException {
		try {
			Process process = processBuilder.start();
			ProcessLogger processLogger = new ProcessLogger(brjs, process, LogLevel.INFO, LogLevel.WARN, null);
			int exitCode = waitForProcess(process, brjs.logger(CommandRunnerUtility.class));
			processLogger.waitFor();
			
			if(exitCode != 0) throw new CommandOperationException("Error while running command '" + 
					StringUtils.join(processBuilder.command(), ", ") + "' (" + exitCode + ")");
		}
		catch(IOException | InterruptedException e) {
			throw new CommandOperationException(e);
		}
	}
	
	private static int waitForProcess(Process process, Logger logger) throws IOException, InterruptedException {
		try(InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
		{
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				logger.println(line);
			}
		}
		
		// TODO: this code looks like a bug -- investigate
		int exitCode = process.waitFor();
		process.waitFor();
		
		return exitCode;
	}
}
