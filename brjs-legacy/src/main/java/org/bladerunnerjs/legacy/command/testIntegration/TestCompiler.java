package org.bladerunnerjs.legacy.command.testIntegration;

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
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.utility.FileUtils;


public class TestCompiler
{

	public List<MemoizedFile> compileTestDirs(BRJS brjs, List<File> testContainerDirs) throws CommandOperationException 
	{
		List<MemoizedFile> classRoots = new ArrayList<>();
		
		for (File testContainerDir : testContainerDirs) 
		{
			MemoizedFile testContainerMemoizedDir = brjs.getMemoizedFile(testContainerDir);
			MemoizedFile commonSrcDir = brjs.locateAncestorNodeOfClass(testContainerDir, App.class).file("test-integration-src");
			commonSrcDir = (commonSrcDir.exists()) ? commonSrcDir : null;
		
			MemoizedFile testDir = testContainerMemoizedDir.file("tests");
			MemoizedFile srcDir = testContainerMemoizedDir.file("src-test");
			srcDir = (srcDir.exists()) ? srcDir : null;
			String sourcePath = getSourcePath(commonSrcDir, srcDir);
			MemoizedFile compiledClassDir = null;
			
			verifyClassNames(testDir, true);
			if(srcDir != null)
			{
				verifyClassNames(srcDir, false);
			}
			
			try
			{
				compiledClassDir = brjs.getMemoizedFile( getCompiledClassDir(brjs, testContainerMemoizedDir) );
			}
			catch (IOException ex)
			{
				throw new CommandOperationException("Error creating directory for compiled tests.", ex);
			} finally {
				org.apache.commons.io.FileUtils.deleteQuietly(compiledClassDir);
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
	
	private void verifyClassNames(MemoizedFile classesDir, boolean isTestDir) throws CommandOperationException
	{
		List<MemoizedFile> sourceFiles = recursiveListFiles(classesDir, new SuffixFileFilter(".java"));
		
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

	private String getSourcePath(File commonSrcDir, MemoizedFile srcDir)
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

	public List<Class<?>> loadClasses(List<MemoizedFile> classDirs) throws CommandOperationException
	{
		List<MemoizedFile> classFiles = recursiveListFiles(classDirs, new SuffixFileFilter(".class"));
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

	public File getCompiledClassDir(BRJS brjs, MemoizedFile testDir) throws IOException 
	{
		App app = brjs.locateAncestorNodeOfClass(testDir, App.class);
		String relativePath = app.dir().getRelativePath(testDir);
		
		return new File(getClassesRoot(testDir), relativePath + "/test-integration/webdriver/tests");
	}
	
	public String getTestClassName(File testFile) 
	{
		String testPath = testFile.getAbsolutePath().replace("\\", "/");
		String classNamePath = StringUtils.substringAfter(testPath, "test-integration/webdriver/tests/");
		String className = StringUtils.substringBeforeLast(classNamePath, ".").replace("/", ".");
		
		return className;
	}
	
	public File getClassesRoot(File root) throws IOException
	{
		File temporaryClassesDir = FileUtils.createTemporaryDirectory( this.getClass() );
		
		return new File(temporaryClassesDir, "classes");
	}
	
	
	
	private static List<MemoizedFile> recursiveListFiles(List<MemoizedFile> roots, IOFileFilter filter)
	{
		List<MemoizedFile> files = new ArrayList<>();
		for (MemoizedFile root : roots)
		{
			recursiveListFiles(root, files, filter);
		}
		return files;
	}
	
	private static List<MemoizedFile> recursiveListFiles(MemoizedFile root, IOFileFilter filter)
	{
		List<MemoizedFile> files = new ArrayList<>();
		recursiveListFiles(root, files, filter);
		return files;
	}
	
	private static void recursiveListFiles(MemoizedFile root, List<MemoizedFile> files, IOFileFilter filter)
	{
		if(!root.isHidden() && root.getName().charAt(0) != '.')
		{
			if (root.isDirectory())
			{
				for (MemoizedFile child : root.listFiles())
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
