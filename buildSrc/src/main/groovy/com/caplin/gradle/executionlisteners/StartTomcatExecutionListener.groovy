package com.caplin.gradle.executionlisteners

import com.caplin.gradle.util.HttpResponseChecker
import org.gradle.api.GradleException
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.tasks.TaskState;

public class StartTomcatExecutionListener implements TaskExecutionListener
{
	
	def hibernateDialectRegexFind = '<property name="hibernate.dialect">[a-zA-Z0-9\\.]+<\\/property>'
	def hibernateDialectReplace = ''
	
	private Task taskToListenFor
	private File tomcatRoot
	private File warRoot
	private File appConfRoot
	
	public StartTomcatExecutionListener(Task taskToListenFor, File tomcatRoot, File warRoot, File appConfRoot, String dialectString)
	{
		this.taskToListenFor = taskToListenFor
		this.tomcatRoot = tomcatRoot
		this.warRoot = warRoot
		this.appConfRoot = appConfRoot
		hibernateDialectReplace = '<property name="hibernate.dialect">'+dialectString+'<\\/property>'
	}

	void beforeExecute(Task task)
	{
		def project = task.project
		if (task == taskToListenFor)
		{
			println "Starting Tomcat..."
			File logsDir = new File(tomcatRoot, "logs")
			logsDir.mkdir()
			
			File webappsDir = new File(tomcatRoot, "webapps")
			project.delete project.fileTree(dir:webappsDir, include:"**/*", exclude:"**/ROOT/**/*")
			
			project.copy {
				from warRoot
				into webappsDir
				fileMode = 0775
			}
			project.fileTree(dir: webappsDir, include:'*.war').visit { element ->
				def appName = element.file.name.replace(".war", "")
				def appDir = project.file(webappsDir.path+"/"+appName)
				if (appDir.exists())
				{
					project.delete appDir
				}
				appDir.mkdir()
				project.copy {
					from project.zipTree(element.file)
					into appDir
					fileMode = 0775
				}
				def hibernateConfig = new File(appDir, "WEB-INF/classes/hibernate.cfg.xml")
				if (hibernateConfig.isFile())
				{
					hibernateConfig.text = hibernateConfig.text.replaceFirst(hibernateDialectRegexFind, hibernateDialectReplace)										
				}
			}
			project.copy {
				from appConfRoot
				into tomcatRoot
				fileMode = 0775
			}
			project.copy {
				from project.file('conf/keymaster')
				into new File(tomcatRoot, "conf/keymaster")
				fileMode = 0775
			}
			
			def command = org.bladerunnerjs.OSCalculator.getOSSpecificCommand('startup.bat')
			ProcessBuilder pb = new ProcessBuilder( command );
			pb.directory( new File(tomcatRoot, "bin") );
			pb.start();			
		}
	}
	
	void afterExecute(Task task, TaskState state)
	{
		def project = task.project
		if (task == taskToListenFor)
		{
			println "Stopping Tomcat..."
			def command = org.bladerunnerjs.OSCalculator.getOSSpecificCommand('shutdown.bat')
			ProcessBuilder pb = new ProcessBuilder( command );
			pb.directory( new File(tomcatRoot, "bin") );
			pb.start();			
		}
	}
	
}
