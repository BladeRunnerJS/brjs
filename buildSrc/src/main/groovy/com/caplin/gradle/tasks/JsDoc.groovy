
package com.caplin.gradle.tasks

import org.gradle.api.invocation.Gradle
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileCollection

import org.apache.commons.io.FilenameUtils


class JsDoc extends DefaultTask
{	
	private Gradle gradle = project.getGradle()
	
	@Input
	String jsRootPathRelativeToJsDocDir = null

	@InputDirectory
	File templateDir = project.file("jsdoc-templates/Caplin")

	@InputDirectory
	File jsDocToolkitDir = null
	
	@Input
	boolean createEmptyJsDoc = true
	
	@OutputDirectory
	File jsDocsDir = null
	
	
	def JsDoc()
	{		
		
	}
	
	@TaskAction
	def exec() 
	{		
		jsDocsDir.mkdirs()
		def jsDocIndex = project.file("${jsDocsDir}/index.html")
		if (createEmptyJsDoc)
		{
			jsDocIndex.createNewFile()
			jsDocIndex.text = "<h1>jsDocs</h1><p>The jsDoc is empty. It can be built by running the 'buildJsDoc' task.</p>"
		}
		else 
		{    
        	def jsDocArgs = [ "--recurse=20", "--template=${templateDir.getAbsolutePath()}", "--directory=./", "-u" ]
    		
    		if (!gradle.startParameter.logLevel.toString().equals("INFO") && !gradle.startParameter.logLevel.toString().equals("DEBUG")) {
    			jsDocArgs += [ '-q']
    		}
    		if (gradle.startParameter.logLevel.toString().equals("DEBUG")) {
    			jsDocArgs += [ '-v']
    		}
        	
    		project.javaexec {
    			main = '-jar'
    			args = ["${jsDocToolkitDir.getAbsolutePath()}/jsrun.jar", "${jsDocToolkitDir.getAbsolutePath()}/app/run.js"] + jsDocArgs + jsRootPathRelativeToJsDocDir
    			workingDir jsDocsDir
    		}
			
		}
	}
	
}