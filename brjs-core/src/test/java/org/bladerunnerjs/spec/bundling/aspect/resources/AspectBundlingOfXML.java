package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfXML extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
	}
	
	// Aspect XML
	@Test
	public void aspectClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}

	@Test
	public void weBundleClassesReferredToByResourcesInAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1", "appns.Class2");
	}
	
	@Test
	public void weDontBundleClassesReferredToByResourcesInAssetLocationsThatDoNotContainClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/pkg/config.xml", "appns.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void weBundleClassesReferredToByResourcesInAncestorAssetLocationsOfTheClassesWeAreBundling() throws Exception {
		given(aspect).hasClasses("appns/pkg/Class1", "appns/pkg/Class2")
			.and(aspect).indexPageRefersTo("appns.pkg.Class1")
			.and(aspect).sourceResourceFileRefersTo("appns/config.xml", "appns.pkg.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.pkg.Class1", "appns.pkg.Class2");
	}
	
	@Test
	public void resourcesCanBeInTheRootOfTheResourcesDir() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void resourcesCanBeInMultipleDirLevels() throws Exception {
		given(exceptions).arentCaught();
		
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("config.xml", "appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.Class1")
			.and(aspect).resourceFileRefersTo("xml/dir1/config.xml", "appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	@Test
	public void classesInXmlCommentsAreNotBundled() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "<!-- appns.Class1 -->");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).doesNotContainClasses("appns.Class1");
	}
	
	@Test
	public void xmlContentIsNotIgnoredAfterComments() throws Exception {
		given(aspect).hasClasses("appns/Class1")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "<!-- appns.ClassDoesNotExist -->\n appns.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.Class1");
	}
	
	// Bladeset XML
	@Test
	public void bladesetClassesReferredToInAspectXMlFilesAreBundled() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(aspect).resourceFileRefersTo("xml/config.xml", "appns.bs.Class1");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1");
	}

	@Test
	public void longFilesDontPreventCalculatingDependencies() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(aspect).containsResourceFileWithContents("xml/config.xml", zeroPad(4090)+"\n appns.bs.Class1\n"+zeroPad(4090)+"\n appns.bs.Class2");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1", "appns.bs.Class2");
	}
	
	@Test
	public void commentsInLongFilesDontPreventCalculatingSubsequentDependencies() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1", "appns/bs/Class2")
			.and(aspect).containsResourceFileWithContents("xml/config.xml", zeroPad(4090)+"\n appns.bs.Class1 <!-- some comment -->\n"+zeroPad(4090)+"\n appns.bs.Class2 ");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("appns.bs.Class1", "appns.bs.Class2");
	}
	
	private String zeroPad(int size) {
		return StringUtils.leftPad("", size, '0');
	}
}
