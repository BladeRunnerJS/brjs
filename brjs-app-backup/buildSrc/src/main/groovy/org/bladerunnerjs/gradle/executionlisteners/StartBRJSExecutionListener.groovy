package org.bladerunnerjs.gradle.executionlisteners

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

import org.bladerunnerjs.gradle.util.ProcessStreamGobbler
import org.bladerunnerjs.OSCalculator

public class StartBRJSExecutionListener implements TaskExecutionListener {
	private static final String SHOW_PROCESS_OUTPUT_FLAG = 'showProcessOutput'

	private Task taskToListenFor
	private File sdkDir;
	private File classesDir;

	private Process brjsProcess = null;

	public StartBRJSExecutionListener(Task taskToListenFor, File sdkDir, File classesDir) {
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
			println "Starting BRJS server ..."

			def classpath = ("${classesDir.path}/*").replace('/', File.separator)
			classpath = OSCalculator.isWin32() ? "\"" + classpath + ";\"" : classpath

			def command = ['java', '-cp', classpath, 'org.bladerunnerjs.runner.CommandRunner', sdkDir.path, 'serve']

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(sdkDir);
			brjsProcess = pb.start();

			def printProcessOutput = project.hasProperty(SHOW_PROCESS_OUTPUT_FLAG)
			ProcessStreamGobbler errorGobbler = new ProcessStreamGobbler(brjsProcess.getErrorStream(), printProcessOutput);
			ProcessStreamGobbler outputGobbler = new ProcessStreamGobbler(brjsProcess.getInputStream(), printProcessOutput);
			errorGobbler.start();
			outputGobbler.start();

			if (!printProcessOutput) {
				println '== Ignoring BRJS output and error streams, use -P' + SHOW_PROCESS_OUTPUT_FLAG + ' to view the BRJS process\'s output =='
			}

			try {
				if (brjsProcess.exitValue() != 0) {
					throw new GradleException("Error starting BRJS server");
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

		if (task == taskToListenFor) {
			println "Stopping BRJS server ..."
			brjsProcess.destroy()
		}
	}
}
