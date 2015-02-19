package org.bladerunnerjs.spec.plugin.bundler.html;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.Before;
import org.junit.Test;

public class HTMLContentPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench workbench;
	private NamedDirNode workbenchTemplate;
	private Bladeset defaultBladeset;
	private Blade bladeInDefaultBladeset;
	private Aspect defaultAspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			defaultAspect = app.defaultAspect();
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			workbenchTemplate = brjs.sdkTemplateGroup("default").template("workbench");
			defaultBladeset = app.defaultBladeset();
			bladeInDefaultBladeset = defaultBladeset.blade("b1");
			
			given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
				.and(workbenchTemplate).containsFolder("resources")
				.and(workbenchTemplate).containsFolder("src");
	}
	
	@Test
	public void ifThereAreNoHtmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("html");
	}
	
	@Test
	public void ifThereAreHtmlFilesThenASingleRequestWillBeGenerated() throws Exception {
		given(aspect).containsResourceFile("template.html");
		then(aspect).prodAndDevRequestsForContentPluginsAre("html", "html/bundle.html");
	}
	
	@Test
	public void aspectHTMlFilesAreBundled() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.view", "appns.bs.b1.*");
	}
	
	@Test
	public void htmlTemplatesWithinAspectArentNamespaced() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div>TESTCONTENT</div>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithDuplicateIDs() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='appns.view'>TESTCONTENT</div>").
		and(aspect).containsResourceFileWithContents("html/view2.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class,  "appns.view");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}

	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.*");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.bs.b1.*");
	}
	
	@Test
	public void enforcedNamespacesAreCalculatedUsingTheAssetLocation() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/some/pkg/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/some/pkg/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.some.pkg.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.some.pkg.*");
	}
	
	@Test
	public void bundlePathTagIsReplacedForDev() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='some.id'>@bundlePath@/some/path</div>")
			.and(brjs).hasDevVersion("dev");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("v/dev/some/path");
	}
	
	@Test
	public void bundlePathTagIsReplacedForProd() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='some.id'>@bundlePath@/some/path</div>")
		.and(brjs).hasProdVersion("1234");
		when(aspect).requestReceivedInProd("html/bundle.html", response);
		then(response).containsText("v/1234/some/path");
	}
	
	@Test
	public void bundlePathTagIsReplacedForWorkbench() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.id'>@bundlePath@/some/path</div>")
			.and(brjs).hasDevVersion("dev")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(workbench).indexPageRequires("appns/bs/b1/Class1");
		when(workbench).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("v/dev/some/path");
	}
	
	@Test
	public void bladeHtmlInDefaultBladesetCanBeBundled() throws Exception {
		given(bladeInDefaultBladeset).hasClass("appns/b1/BladeClass")
			.and(bladeInDefaultBladeset).containsResourceFileWithContents("html/view.html", "<div id='appns.b1.my.view'>Blade Content</div>")
			.and(aspect).indexPageRequires("appns/b1/BladeClass");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("Blade Content");
	}
	
	@Test
	public void htmlInDefaultAspectCanBeBundled() throws Exception {
		given(defaultAspect).hasClass("appns/AspectClass")
			.and(defaultAspect).containsResourceFileWithContents("html/view.html", "<div id='appns.my.view'>Aspect Content</div>")
			.and(defaultAspect).indexPageRequires("appns/AspectClass");
		when(defaultAspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("Aspect Content");
	}
	
}
