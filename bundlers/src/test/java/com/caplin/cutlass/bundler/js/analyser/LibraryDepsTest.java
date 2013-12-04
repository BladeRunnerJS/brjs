package com.caplin.cutlass.bundler.js.analyser;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static org.junit.Assert.assertEquals;

import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class LibraryDepsTest
{
	private String rootLibDirName = "src/test/resources/generic-bundler/bundler-structure-tests/" + SDK_DIR;
	private String packageLibDirName = rootLibDirName + "/libs/javascript/caplin/src/caplin/package1";
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(rootLibDirName)));
	}
	
	@Test
	public void testPackageDeps() throws Exception
	{
		File packageDir = new File(packageLibDirName);
		
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getLibraryCodeAnalyser(BRJSAccessor.root.app("test-app1"), packageDir);
		PackageDepsCodeUnitVisitor visitor = new PackageDepsCodeUnitVisitor("caplin.package1", false);
		codeAnalyser.emit(visitor);
		
		String expected = "caplin.package2.Pack2Class" 
						+ "\ncaplin.package2.Pack2Class2"
						+ "\ncaplin.package2.Pack2Class3";
		
		String result = visitor.getResult().trim();
		assertEquals(expected, result);
	}
	
	@Test
	public void testPackageDepsSummary() throws Exception
	{
		File packageDir = new File(packageLibDirName);
		
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getLibraryCodeAnalyser(BRJSAccessor.root.app("test-app1"), packageDir);
		PackageDepsCodeUnitVisitor visitor = new PackageDepsCodeUnitVisitor("caplin.package1", true);
		codeAnalyser.emit(visitor);
		
		String expected = "caplin.package2";
		
		String result = visitor.getResult().trim();
		assertEquals(expected, result);
	}
	
	@Test
	public void testPackageDepsContainingThirdpartyDependency() throws Exception
	{
		rootLibDirName = "src/test/resources/js-bundler/lib-with-thirdparty-dep/" + SDK_DIR;
		File packageDir = new File(rootLibDirName + File.separator + "libs/javascript/caplin/src/caplin/package1");
		
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getLibraryCodeAnalyser(BRJSAccessor.root.app("test-app1"), packageDir);
		PackageDepsCodeUnitVisitor visitor = new PackageDepsCodeUnitVisitor("caplin.package1", false);
		codeAnalyser.emit(visitor);
		
		String expected = "caplin.package2.Pack2Class"
						+ "\ncaplin.package2.Pack2Class2"
						+ "\ncaplin.package2.Pack2Class3"
						+ "\njquery";
		
		String result = visitor.getResult().trim();
		assertEquals(expected, result);
	}
	
	@Test
	public void testPackageDepsSummaryContainingThirdpartyDependency() throws Exception
	{
		rootLibDirName = "src/test/resources/js-bundler/lib-with-thirdparty-dep/" + SDK_DIR;
		File packageDir = new File(rootLibDirName + File.separator + "libs/javascript/caplin/src/caplin/package1");
		
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getLibraryCodeAnalyser(BRJSAccessor.root.app("test-app1"), packageDir);
		PackageDepsCodeUnitVisitor visitor = new PackageDepsCodeUnitVisitor("caplin.package1", true);
		codeAnalyser.emit(visitor);
		
		String expected = "caplin.package2"
						+ "\njquery";
		
		String result = visitor.getResult().trim();
		assertEquals(expected, result);
	}
	
}
