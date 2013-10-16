
package com.caplin.gradle.plugins

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin

class CompileOnly implements Plugin<Project>
{

	void apply(Project project)
	{
		if (project.plugins.hasPlugin(JavaPlugin.class)) 
		{
    		project.configurations {
    			compileOnly
    			testCompileOnly { extendsFrom compileOnly }
    		}
    		project.sourceSets {
    			main {
    				compileClasspath = project.configurations.compile + project.configurations.compileOnly
    			}
    			test {
    				compileClasspath = compileClasspath + project.configurations.testCompileOnly  + project.configurations.compileOnly
    			}
    		}
    		project.afterEvaluate {
				if (project.plugins.hasPlugin(EclipsePlugin.class))
				{
					project.eclipse.classpath.plusConfigurations += project.configurations.compileOnly
					project.eclipse.classpath.plusConfigurations += project.configurations.testCompileOnly
				}
				if (project.plugins.hasPlugin('idea'))
				{
					project.idea {
						module {
							scopes.PROVIDED.plus += project.configurations.compileOnly
						}
					}
				}
    		}
		}
	}
	
}