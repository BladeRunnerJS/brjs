package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.bundler.js.aliasing.AliasRegistry;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class ClassProcessorFactoryTest {

	private final File file = new File( "" );
	private final List<String> thirdpartyClassnames = new ArrayList<String>();
	private final String testBase = "src/test/resources/js-bundler/class-aliasing/apps";
	private final File baseDir = new File(testBase, "single-alias-file/default-aspect");
	private final List<ClassnameFileMapping> patchFiles = new ArrayList<ClassnameFileMapping>();
	private final List<ClassnameFileMapping> sourceFiles = new ArrayList<ClassnameFileMapping>(){
		private static final long serialVersionUID = 1978224660453330876L;
		{
    		add( new ClassnameFileMapping( "novox.LibClass", file ) );
    		add( new ClassnameFileMapping( "novox.g1.AliasClass1", file ) );
    		add( new ClassnameFileMapping( "novox.g1.AliasClass2", file ) );
    		add( new ClassnameFileMapping( "novox.g2.AliasClass1", file ) );
		}
	};
	
	private AliasRegistry aliasRegistry;
	
	@Before
	public void setUp() throws BundlerProcessingException {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		
		List<ClassnameFileMapping> allSourceFiles = SourceFileLocator.getAllSourceFiles( baseDir, null );
		Set<String> validClasses = SourceFileLocator.createValidClasses( allSourceFiles );
		
		aliasRegistry = new AliasRegistry( baseDir, null, validClasses );
	}
	
	@Test
	public void classDictionaryIncludesAliases() {
		ClassProcessor classProcessor = ClassProcessorFactory.createClassProcessor( sourceFiles, patchFiles, thirdpartyClassnames, aliasRegistry );
		
		ClassDictionary classDictionary = classProcessor.getDictionary();
		
		assertTrue( classDictionary.contains( "novox.LibClass" ) );
		assertTrue( classDictionary.contains( "aliasWithNoDefaultClass" ) );
		assertTrue( classDictionary.contains( "alias #1" ) );
	}
}
