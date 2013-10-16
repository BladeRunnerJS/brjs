
package com.caplin.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.GradleException


class CheckDirectoryStructure extends DefaultTask
{	
	@Input
	File checkRoot = project.projectDir
	
	@OutputFile
	File emptyOutputFile = project.file("${project.buildDir}/${name}.output")
	
	def expectedPaths = []
	def bannedPaths = [] 
			
	@TaskAction
	def run()
	{
		expectedPaths.each { expectedPath ->
			def fileTreeFiles = project.fileTree(dir:checkRoot, include:expectedPath).getFiles()
			if (fileTreeFiles.size() < 1) {
				throw new GradleException("Didnt find expected files for ${expectedPath}, using root directory ${checkRoot.path}.") 
			}
		}
		bannedPaths.each { bannedPath ->
			def fileTreeFiles = project.fileTree(dir:checkRoot, include:bannedPath).getFiles()
			if (fileTreeFiles.size() > 0) {
				throw new GradleException("Unexpected files for ${bannedPath}, using root directory ${checkRoot.path}.") 
			}
		}
		emptyOutputFile.createNewFile()
	}
	
}