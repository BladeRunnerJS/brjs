package org.bladerunnerjs.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;


public class ProcessLogger {
	private static final int SLEEP_TIME = 25;
	
	private Logger logger;
	private int childThreads = 2;
	private boolean running = true;
	private final LogLevel standardOutLogLevel;
	private final LogLevel standardErrorLogLevel;
	
	public ProcessLogger(BRJS brjs, Process process, LogLevel standardOutLogLevel, LogLevel standardErrorLogLevel, String processName) throws IOException {
		logger = brjs.logger(ProcessLogger.class);
		this.standardOutLogLevel = standardOutLogLevel;
		this.standardErrorLogLevel = standardErrorLogLevel;
		processName = (processName == null) ? "" : processName;
		
		logProcessStream(processName, new InfoLogger(), process.getInputStream());
		logProcessStream(processName, new ErrorLogger(), process.getErrorStream());
	}
	
	public void stop() {
		running = false;
	}
	
	public void waitFor() throws InterruptedException {
		stop();
		while(childThreads > 0) {
			logger.debug("waiting for logger threads (" + childThreads + ")...");
			Thread.sleep(SLEEP_TIME);
		}
	}
	
	private synchronized void loggerStopped() {
		--childThreads;
	}
	
	private void logProcessStream(final String processName, final StreamLogger streamLogger, final InputStream inputStream) {
		Thread thread = new Thread() {
			public void run() {
				try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
					while(running) {
						try {
							Thread.sleep(SLEEP_TIME);
							
							String nextLine;
							
							do {
								nextLine = bufferedReader.readLine();
								
								if(nextLine != null) {
									streamLogger.log(processName + nextLine);
								}
							} while(nextLine != null);
						}
						catch (InterruptedException threadException) {
							logger.error(processName + "logging thread interupted: " + threadException.getMessage());
						}
						catch(IOException ioException) {
							// do nothing: process closed
						}
					}
					
					loggerStopped();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		thread.start();
	}
	
	private interface StreamLogger {
		void log(String message, Object... params);
	}
	
	private class InfoLogger implements StreamLogger {
		@Override
		public void log(String message, Object... params) {
			logMessage(standardOutLogLevel, message, params);
		}
	}
	
	private class ErrorLogger implements StreamLogger {
		@Override
		public void log(String message, Object... params) {
			logMessage(standardErrorLogLevel, message, params);
		}
	}
	
	private void logMessage(LogLevel logLevel, String message, Object[] params) {
		switch(logLevel) {			
			case ERROR:
				logger.error(message, params);
				break;
			
			case WARN:
				logger.warn(message, params);
				break;
			
			case INFO:
				logger.info(message, params);
				break;
			
			case DEBUG:
				logger.debug(message, params);
				break;
		}
	}
}
