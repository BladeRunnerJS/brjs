package com.caplin.gradle.executionlisteners

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.tasks.TaskState;


public class StartDerbyExecutionListener implements TaskExecutionListener
{

	private Task taskToListenFor
	private File derbyRoot
	private File tomcatRoot
	
	def APP_COPY_SUFFIX = '_BUILD.COPY'
	def hibernateDialectRegexFind = '<property name="hibernate.dialect">[a-zA-Z0-9\\.]+<\\/property>'
	def hibernateDialectReplace = ''
	
	def derbyProcess = null
	
	public StartDerbyExecutionListener(Task taskToListenFor, File derbyRoot, File tomcatRoot, String dialectString)
	{
		this.taskToListenFor = taskToListenFor
		this.derbyRoot = derbyRoot
		this.tomcatRoot = tomcatRoot
		hibernateDialectReplace = '<property name="hibernate.dialect">'+dialectString+'<\\/property>'
	}

	void beforeExecute(Task task)
	{
		def project = task.project
		if (task == taskToListenFor) 
		{
			println "Starting Derby..."
			project.delete project.file('apps').listFiles().findAll { it.name.endsWith(APP_COPY_SUFFIX) }
			project.copy {
				from project.file("conf/lib")
				into new File(tomcatRoot, "lib")
			}
			
			ProcessBuilder pb = new ProcessBuilder('java','-jar', 'derbyrun.jar', 'server', 'start' );
			pb.directory( new File(derbyRoot, "lib") );
			derbyProcess = pb.start();
			try {
				if (derbyProcess.exitValue() != 0) {
					throw new GradleException("Error starting derby db");
				}
			} catch (Exception ex) {
				/* do nothing - the process hasnt stopped yet (which is good) */
			}
			
			def applications = project.file('apps').listFiles().findAll { it.isDirectory() }
			applications.each { app ->
				def appCopy = project.file(app.path+'_BUILD.COPY')
				project.copy {
						from app
						into app.parent+'/'+appCopy.name
						rename app.name, appCopy.name
						fileMode = 0775	
					}
				
				
				def hibernateConfig = new File(appCopy, "WEB-INF/classes/hibernate.cfg.xml")
				
				if (hibernateConfig.isFile())
				{
					hibernateConfig.text = hibernateConfig.text.replaceFirst(hibernateDialectRegexFind, hibernateDialectReplace)
				}
			
    			project.exec {
    				workingDir 'sdk'
    				def cutlassCommand = org.bladerunnerjs.OSCalculator.getOSSpecificCommand('bladerunner')
    				commandLine cutlassCommand + ['reset-db', appCopy.name, '--jdbc', 
						'jdbc:derby://localhost:1527/'+app.name+';create=true', 'org.apache.derby.jdbc.ClientDriver', 'derbyuser', 'derbyuser']
    			}
			}
			project.delete project.file('apps').listFiles().findAll { it.name.endsWith(APP_COPY_SUFFIX) }
		}
	}
	
	void afterExecute(Task task, TaskState state)
	{
		def project = task.project
		if (task == taskToListenFor) 
		{
			println "Stopping Derby..."
			derbyProcess.destroy()
			project.delete project.file('apps').listFiles().findAll { it.name.endsWith(APP_COPY_SUFFIX) }
		}
	}
	
}
