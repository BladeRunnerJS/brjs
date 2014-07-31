package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;

public class TestCompiler
{

	public List<File> compileTestDirs(BRJS brjs, List<File> testContainerDirs) throws CommandOperationException 
	{
		List<File> classRoots = new ArrayList<File>();
		
		for (File testContainerDir : testContainerDirs) 
		{
			File commonSrcDir = brjs.locateAncestorNodeOfClass(testContainerDir, App.class).file("test-integration-src");
			commonSrcDir = (commonSrcDir.exists()) ? commonSrcDir : null;
			
			File testDir = new File(testContainerDir, "tests");
			File srcDir = new File(testContainerDir, "src-test");
			srcDir = (srcDir.exists()) ? srcDir : null;
			String sourcePath = getSourcePath(commonSrcDir, srcDir);
			File compiledClassDir = null;
			
			verifyClassNames(testDir, true);
			if(srcDir != null)
			{
				verifyClassNames(srcDir, false);
			}
			
			try
			{
				compiledClassDir = getCompiledClassDir(brjs, testContainerDir);
			}
			catch (IOException ex)
			{
				throw new CommandOperationException("Error creating directory for compiled tests.", ex);
			}
			
			// see http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.isv/guide/jdt_api_compile.htm for command line args
			String[] compilerArgs = new String[]{ "-1.6", "-sourcepath", sourcePath.toString(), "-d", compiledClassDir.getPath(), "-encoding", "UTF-8", "-nowarn", testContainerDir.getPath() };
			
			boolean compileReturnValue = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(
					compilerArgs, 
					new PrintWriter(System.out), 
					new PrintWriter(System.err), 
					null );
			if (!compileReturnValue)
			{
				throw new CommandOperationException("Error compiling files in '" + testContainerDir.getPath() + "'.");
			}
			classRoots.add(compiledClassDir);
		}
		return classRoots;
	}
	
	private void verifyClassNames(File classesDir, boolean isTestDir) throws CommandOperationException
	{
		List<File> sourceFiles = recursiveListFiles(classesDir, new SuffixFileFilter(".java"));
		
		for(File sourceFile : sourceFiles)
		{
			boolean isTestClass = sourceFile.getName().endsWith("Test.java");
			
			if(isTestDir != isTestClass)
			{
				if(isTestDir)
				{
					throw new CommandOperationException("'" + sourceFile.getName() +
						"' doesn't end 'Test.java' but has been placed into a 'tests' directory ('" + sourceFile.getAbsolutePath() + "').");
				}
				else
				{
					throw new CommandOperationException("'" + sourceFile.getName() +
						"' ends with 'Test.java' but has been placed outside of 'tests' directory ('" + sourceFile.getAbsolutePath() + "').");
				}
			}
		}
	}

	private String getSourcePath(File commonSrcDir, File srcDir)
	{
		StringBuilder sourcePath = new StringBuilder();
		
		if(commonSrcDir != null)
		{
			sourcePath.append(commonSrcDir.getAbsolutePath());
		}
		
		if(srcDir != null)
		{
			if(sourcePath.length() > 0)
			{
				sourcePath.append(";");
			}
			
			sourcePath.append(srcDir.getAbsolutePath());
		}
		
		return sourcePath.toString();
	}

	public List<Class<?>> loadClasses(List<File> classDirs) throws CommandOperationException
	{
		List<File> classFiles = recursiveListFiles(classDirs, new SuffixFileFilter(".class"));
   		List<Class<?>> loadedClasses = new ArrayList<Class<?>>();
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		for (File classFile : classFiles)
		{
			if(classFile.getName().endsWith("Test.class"))
			{
				try
				{
					@SuppressWarnings("resource")
					URLClassLoader classloader = new URLClassLoader( new URL[] { classFile.getParentFile().toURI().toURL() }, classLoader);
					Class<?> nextClass = classloader.loadClass(getTestClassName(classFile));
					loadedClasses.add(nextClass);
				}
				catch (MalformedURLException ex)
				{
					throw new CommandOperationException(ex);
				}
				catch (ClassNotFoundException ex)
				{
					throw new CommandOperationException(ex);
				}
			}
		}
		return loadedClasses;
	}

	public File getCompiledClassDir(BRJS brjs, File testDir) throws IOException 
	{
		App app = brjs.locateAncestorNodeOfClass(testDir, App.class);
		String relativePath = RelativePathUtility.get(brjs, app.dir(), testDir);
		
		return new File(getClassesRoot(testDir), relativePath + "/test-integration/webdriver/tests");
	}
	
	public String getTestClassName(File testFile) 
	{
		String testPath = testFile.getAbsolutePath().replace("\\", "/");
		String classNamePath = StringUtils.substringAfter(testPath, CutlassConfig.TEST_INTEGRATION_PATH+"/");
		String className = StringUtils.substringBeforeLast(classNamePath, ".").replace("/", ".");
		
		return className;
	}
	
	public File getClassesRoot(File root) throws IOException
	{
		File temporaryClassesDir = FileUtility.createTemporaryDirectory("cutlass-compiled-tests");
		
		return new File(temporaryClassesDir, CutlassConfig.TEST_INTEGRATION_CLASSES_DIRNAME);
	}
	
	
	
	private static List<File> recursiveListFiles(List<File> roots, IOFileFilter filter)
	{
		List<File> files = new ArrayList<File>();
		for (File root : roots)
		{
			recursiveListFiles(root, files, filter);
		}
		return files;
	}
	
	private static List<File> recursiveListFiles(File root, IOFileFilter filter)
	{
		List<File> files = new ArrayList<File>();
		recursiveListFiles(root, files, filter);
		return files;
	}
	
	private static void recursiveListFiles(File root, List<File> files, IOFileFilter filter)
	{
		if(!root.isHidden() && root.getName().charAt(0) != '.')
		{
			if (root.isDirectory())
			{
				for (File child : FileUtility.sortFiles(root.listFiles()))
				{
					recursiveListFiles(child, files, filter);
				}
			}
			else if (root.isFile() && filter.accept(root, root.getName()))
			{
				files.add(root.getAbsoluteFile());
			}
		}
	}
	
}
