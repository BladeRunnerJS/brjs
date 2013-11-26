
package com.caplin.gradle.executionlisteners

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import com.caplin.gradle.util.ProcessStreamGobbler


public class StartCutlassExecutionListener implements TaskExecutionListener
{
	
	private static final String SHOW_PROCESS_OUTPUT_FLAG = 'showProcessOutput'
	
	private Task taskToListenFor
	private File sdkDir;
	private File classesDir;
	
	private Process cutlassProcess = null;
	
	
	public StartCutlassExecutionListener(Task taskToListenFor, File sdkDir, File classesDir)
	{
		this.taskToListenFor = taskToListenFor
		this.sdkDir = sdkDir
		this.classesDir = classesDir
	}
	
	void beforeExecute(Task task) {
		if (!task.getEnabled()) {
			return
		}
		def project = task.project
		if (task == taskToListenFor) {
			println "Starting BladeRunnerJS..."
			
			def classpath = (classesDir.path+"/*").replace("/",File.separator)
			classpath = (org.bladerunnerjs.OSCalculator.isWin32()) ? "\""+classpath+";\"" : classpath
					
			//TODO: use the brjs script instead here
			def command = ['java', '-cp', classpath, 'org.bladerunnerjs.CommandRunner', sdkDir.path, 'start']
			
			ProcessBuilder pb = new ProcessBuilder( command );
			pb.directory( sdkDir );
			cutlassProcess = pb.start();
 
			def printProcessOutput = project.hasProperty(SHOW_PROCESS_OUTPUT_FLAG)
			ProcessStreamGobbler errorGobbler = new ProcessStreamGobbler(cutlassProcess.getErrorStream(), printProcessOutput);
			ProcessStreamGobbler outputGobbler = new ProcessStreamGobbler(cutlassProcess.getInputStream(), printProcessOutput);
			errorGobbler.start();
			outputGobbler.start();
			
			if (!printProcessOutput) {
				println '== Ignoring Cutlass output and error streams, use -P'+SHOW_PROCESS_OUTPUT_FLAG+' to view the cutlass process\'s output =='
			}
			
			try {
				if (cutlassProcess.exitValue() != 0) {
					throw new GradleException("Error starting cutlass server");
				}
			} catch (Exception ex) {
				/* do nothing - the process hasnt stopped yet (which is good) */
			}
			
		}
	}
	
	void afterExecute(Task task, TaskState state) {
		if (!task.getEnabled()) {
			return
		}
		def project = task.project
		if (task == taskToListenFor) {
			println "Stopping BladeRunnerJS..."
			cutlassProcess.destroy()
		}
	}
	
}
