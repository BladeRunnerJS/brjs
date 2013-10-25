package com.caplin.cutlass.bundler.js;

import java.io.File;

import com.caplin.cutlass.exception.NamespaceException;

import org.junit.Before;
import org.junit.Test;


public class JsNamespaceVerifierTest {

	private JsNamespaceVerifier verifier;
		
	@Before
	public void setup() {
		verifier = new JsNamespaceVerifier();
	}
	
	@Test(expected=NamespaceException.class)
	public void testMultipleNamespacesInBladesetThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/amultiplenamespace-bladeset");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testWrongNamespacesInBladesetThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/awrongtoplevelnamespace-bladeset");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testMultipleNamespacesAtBladesetLevelThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/amultiplebladesetnamespace-bladeset");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testWrongNamespacesAtBladesetLevelThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/awrongbladesetnamespace-bladeset");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testMultipleNamespacesInBladeThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/amultiplenamespace-bladeset/blades/blade");
		
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testWrongNamespacesInBladeThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/awrongtoplevelnamespace-bladeset/blades/blade");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testMultipleNamespacesAtBladeLevelThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/amultiplebladesetnamespace-bladeset/blades/blade");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testWrongNamespacesAtBladesetLevelInBladeThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/awrongbladesetnamespace-bladeset/blades/blade");
				
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
	@Test(expected=NamespaceException.class)
	public void testWrongNamespacesAtBladeLevelThrowsError() throws Exception {
		File baseDir = new File("src/test/resources/js-bundler/id-scope/apps/test-app1/awrongbladesetnamespace-bladeset/blades/blade1");
		
		verifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
	}
	
}
	

