
package com.caplin.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileCollection

import org.apache.commons.io.FilenameUtils


class RunJstdTests extends DefaultTask
{	
	
	@OutputFile
	File taskOutputFile = project.file("${project.buildDir}/${name}.output")

	@InputFiles
	FileCollection inputFiles = null
	
	String workingDir = "sdk"
	String testDir = "libs/javascript"
	String testType = "ALL"
	String testArg = "test"
	String browsers = ""
	String command = "brjs"
	
	def RunJstdTests()
	{			
		final String thisTaskName = name
		project.afterEvaluate {	
			project.tasks[thisTaskName].inputFiles = project.fileTree(dir: "${workingDir}/${testDir}", exclude:"**/bundles/**")
		}
	}
	
	@TaskAction
	def runTests() 
	{
		def browserArgs = (!browsers.equals("")) ? ['-b', browsers] : []
		def testArgs = (!testArg.equals("")) ? [testArg] : []
		
		def commandToRun = org.bladerunnerjs.OSCalculator.getOSSpecificCommand(command) 
		
		project.exec {
			commandLine commandToRun + testArgs + [testDir, testType] + browserArgs
			workingDir this.workingDir
		}
		taskOutputFile.createNewFile()
		taskOutputFile.text = System.currentTimeMillis()
	}
	
}