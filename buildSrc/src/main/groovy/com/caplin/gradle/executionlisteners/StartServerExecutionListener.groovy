package com.caplin.gradle.executionlisteners

import com.caplin.gradle.util.ProcessStreamGobbler
import com.caplin.gradle.util.HttpResponseChecker
import org.gradle.api.GradleException
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.tasks.TaskState;


class StartServerExecutionListener implements TaskExecutionListener
{
	
	Task taskToListenFor = null
	File workingDir = null
	List<String> startCommand = []
	List<String> stopCommand = []
	boolean printOutput = false
    HttpResponseChecker serverStopResponseChecker = null
	
	public StartServerExecutionListener(Task taskToListenFor, File workingDir, List<String> startCommand, List<String> stopCommand)
	{
		this(taskToListenFor, workingDir, startCommand, stopCommand, false, null);
	}
	public StartServerExecutionListener(Task taskToListenFor, File workingDir, List<String> startCommand, List<String> stopCommand, HttpResponseChecker serverStopResponseChecker)
	{
		this(taskToListenFor, workingDir, startCommand, stopCommand, false, serverStopResponseChecker);
	}
	
	public StartServerExecutionListener(Task taskToListenFor, File workingDir, List<String> startCommand, List<String> stopCommand, boolean printProcessOutput, HttpResponseChecker serverStopResponseChecker)
	{
		this.taskToListenFor = taskToListenFor
    	this.workingDir = workingDir
    	this.startCommand = startCommand
    	this.stopCommand = stopCommand
		this.printOutput = printProcessOutput
        this.serverStopResponseChecker = serverStopResponseChecker
	}

	@Override
	public void beforeExecute(Task task)
	{
		if (!task.getEnabled()) {
			return
		}
		if (task == taskToListenFor && startCommand.size() > 0)
		{
			println "StartServerExecutionListener running ${startCommand}"
    		ProcessBuilder pb = new ProcessBuilder(startCommand);
    		pb.directory(workingDir );
    		def thisProcess = pb.start();
			
			ProcessStreamGobbler errorGobbler = new ProcessStreamGobbler(thisProcess.getErrorStream(), printOutput);
			ProcessStreamGobbler outputGobbler = new ProcessStreamGobbler(thisProcess.getInputStream(), printOutput);
			errorGobbler.start();
			outputGobbler.start();
    		try {
    			if (thisProcess.exitValue() != 0) {
    				throw new GradleException("Error starting server.");
    			}
    		} catch (Exception ex) {
    			/* do nothing - the process hasnt stopped yet (which is good) */
    		}
		}
	}
	
	@Override
	public void afterExecute(Task task, TaskState state)
	{
		if (!task.getEnabled()) {
			return
		}
		if (task == taskToListenFor && stopCommand.size() > 0)
		{
			println "StartServerExecutionListener running ${stopCommand}"
    		ProcessBuilder pb = new ProcessBuilder(stopCommand);
    		pb.directory(workingDir);
    		def thisProcess = pb.start();
    		try {
    			if (thisProcess.exitValue() != 0) {
    				throw new GradleException("Error stopping server.");
    			}
    		} catch (Exception ex) {
    			/* do nothing */
    		}
            if (serverStopResponseChecker != null)
            {
                if (!serverStopResponseChecker.confirmNoResponse())
                {
                    throw new GradleException("Error stopping server.");
                }
            }

		}
	}

}
