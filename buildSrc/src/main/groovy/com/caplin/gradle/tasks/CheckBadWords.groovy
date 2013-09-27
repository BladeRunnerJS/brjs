
package com.caplin.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree

import org.apache.commons.io.FilenameUtils


class CheckBadWords extends DefaultTask
{	
	@Input
	FileTree checkFiles = project.fileTree(dir:project.projectDir)
	
	@OutputFile
	File emptyOutputFile = project.file("${project.buildDir}/${name}.output")
	
	def ignoreExtensions = []
	
	def failWords = []
	def warningWords = []
	
	def extensionFailWords = [ : ]
	def extensionWarningWords = [ : ]
	
	boolean addSpacesAfter = true;
			
	@TaskAction
	def run()
	{
		failWords = (failWords == null) ? [] : failWords
		warningWords = (warningWords == null) ? [] : warningWords
		extensionFailWords = (extensionFailWords == null) ? [] : extensionFailWords
		extensionWarningWords = (extensionWarningWords == null) ? [] : extensionWarningWords
	
	
		def foundFailWord = false
		def foundWarnWord = false
		
		checkFiles.each { checkFile ->
			def fileExtension = FilenameUtils.getExtension(checkFile.name)
			if (!checkFile.isDirectory() && !ignoreExtensions.contains(fileExtension)) {
				def failWordsForThisExtension = failWords + ((extensionFailWords[fileExtension] != null) ? extensionFailWords[fileExtension] : [])
				def warningWordsForThisExtension = warningWords + ((extensionWarningWords[fileExtension] != null) ? extensionWarningWords[fileExtension] : [])
								
				checkFile.eachLine { line ->
					failWordsForThisExtension.each { badWord ->
						def thisBadWord = badWord.toLowerCase()
						thisBadWord = (addSpacesAfter) ? thisBadWord+" " : thisBadWord
						if (line.toLowerCase().contains(thisBadWord)) {
							println "--ERROR: found " + badWord + " in " + project.relativePath(checkFile)
							foundFailWord = true
						}
					}
					warningWordsForThisExtension.each { badWord ->
						def thisBadWord = badWord.toLowerCase()
						thisBadWord = (addSpacesAfter) ? thisBadWord+" " : thisBadWord
						if (line.toLowerCase().contains(thisBadWord)) {
							if (!foundWarnWord) {
								println "--WARNING: there were warning words found in some files, use --info to see the output"
							}
							logger.info( "--WARNING: found " + badWord + " in " + project.relativePath(checkFile) )
							foundWarnWord = true
						}
					}
				}
			}
		}
		emptyOutputFile.createNewFile()
		if (foundFailWord) {
			throw new GradleException("Found bad words.")
		}
	}
	
}