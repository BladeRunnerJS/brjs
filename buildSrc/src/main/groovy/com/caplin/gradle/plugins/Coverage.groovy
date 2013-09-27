
package com.caplin.gradle.plugins

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class Coverage implements Plugin<Project>
{
	
	/* from http://docs.codehaus.org/display/GRADLE/Cookbook#Cookbook-usingCobertura */
	void apply(Project project)
	{
		if (project.plugins.hasPlugin(JavaPlugin.class)) 
		{
			def mainSrcDir = "${project.sourceSets.main.allSource}"
			def coberturaSerFile = "${project.buildDir}/cobertura.ser"
			def originalClassesDir = "${project.sourceSets.main.output.classesDir}"
			def coberturaClassesDir = "${originalClassesDir}-coberturaClasses"
			def coverageReportsDir = "${project.buildDir.path}/reports/coverage"
			def coberturaSerFileCopy = "${coberturaSerFile}-copy"
			
			project.configurations {
				instrumentation
			}
			
    		project.dependencies {
    			testRuntime 'net.sourceforge.cobertura:cobertura:1.9.3'
				instrumentation project.files(coberturaClassesDir)
    		}    		
    	
			project.sourceSets.test {
				runtimeClasspath = project.configurations.instrumentation + runtimeClasspath
			}
			
    		project.task('coberturaSetup') {
				description "Instruments each class and writes the .ser file."
				inputs.dir originalClassesDir
				outputs.file coberturaSerFile
    		} << {
    			project.delete coberturaSerFile
				project.delete coberturaClassesDir
				project.copy {
    				from originalClassesDir
					into coberturaClassesDir
    			}
    			project.ant {
					/* http://cobertura.sourceforge.net/anttaskreference.html */
					taskdef(resource:'tasks.properties', classpath: project.configurations.testRuntime.asPath)
    				'cobertura-instrument'(datafile:coberturaSerFile) {
    					fileset(dir: coberturaClassesDir,
							includes:"**/*.class",
							excludes:"**/*Test.class" 
						)
    				}
    			}
				project.copy { 
					from coberturaSerFile 
					into project.file(coberturaSerFileCopy).parent
					rename project.file(coberturaSerFile).name, project.file(coberturaSerFileCopy).name
				}
    		}
    			
    		project.tasks.withType(JavaCompile).each { compileTask ->
    			project.tasks['coberturaSetup'].dependsOn compileTask
    		}
    	
    		project.tasks.withType(Test).each { testTask ->
    			testTask.dependsOn project.tasks['coberturaSetup']
    			testTask.systemProperty 'net.sourceforge.cobertura.datafile', coberturaSerFileCopy
    			testTask.jvmArgs '-XX:-UseSplitVerifier'
    		}
    	
    		project.task('coberturaReports') {
				description "Writes the Cobertura coverage report."
				inputs.file coberturaSerFile
				outputs.dir coverageReportsDir
    		} << {
				/* create cobertura reports */
				project.ant {
					taskdef(resource:'tasks.properties', classpath: project.configurations.testRuntime.asPath)
					'cobertura-report'(destdir:coverageReportsDir,
						format:'xml', srcdir:"src/main/java", datafile:coberturaSerFileCopy)
					'cobertura-report'(destdir:coverageReportsDir,
						format:'html', srcdir:"src/main/java", datafile:coberturaSerFileCopy)
				}
    		}
			
    		project.tasks.withType(Test).each { testTask ->
    			project.tasks['coberturaReports'].dependsOn testTask
    		}
    	
    		project.tasks['check'].dependsOn project.tasks['coberturaReports']
		}
		
	}
	
}