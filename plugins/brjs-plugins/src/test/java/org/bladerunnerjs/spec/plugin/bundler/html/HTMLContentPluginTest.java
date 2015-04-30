package org.bladerunnerjs.spec.plugin.bundler.html;


import static org.bladerunnerjs.plugin.bundlers.html.HTMLTemplateUtility.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.api.BladeWorkbench;
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
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>TESTCONTENT</template>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("html/view.html", "<template id='xxxxx.view'>TESTCONTENT</template>")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.view", "appns.bs.b1.*");
	}
	
	@Test
	public void htmlTemplatesWithinAspectArentNamespaced() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='xxxxx.view'>TESTCONTENT</template>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template>TESTCONTENT</template>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<template>");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithDuplicateIDs() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>TESTCONTENT</template>").
		and(aspect).containsResourceFileWithContents("html/view2.html", "<template id='appns.view'>TESTCONTENT</template>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class,  "appns.view");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.b1.view'>TESTCONTENT</template>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.b1.view'>TESTCONTENT</template>")
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
			.and(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.b1.view'>TESTCONTENT</template>")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.b1.view'>TESTCONTENT</template>")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<template id='appns.stuff'>appns.bs.b1.Class1</template>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.badnamespace.view'>TESTCONTENT</template>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.*");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<template>TESTCONTENT</template>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<template>", "appns.bs.b1.*");
	}
	
	@Test
	public void enforcedNamespacesAreCalculatedUsingTheAssetLocation() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/some/pkg/view.html", "<template id='appns.bs.badnamespace.view'>TESTCONTENT</template>")
			.and(blade).hasClass("appns/bs/b1/some/pkg/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.some.pkg.Class1");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.some.pkg.*");
	}
	
	@Test
	public void bundlePathTagIsReplacedForDev() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='some.id'>@bundlePath@/some/path</template>")
			.and(brjs).hasVersion("dev");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("v/dev/some/path");
	}
	
	@Test
	public void bundlePathTagIsReplacedForProd() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='some.id'>@bundlePath@/some/path</template>")
		.and(brjs).hasVersion("1234");
		when(aspect).requestReceivedInProd("html/bundle.html", response);
		then(response).containsText("v/1234/some/path");
	}
	
	@Test
	public void bundlePathTagIsReplacedForWorkbench() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<template id='appns.bs.b1.id'>@bundlePath@/some/path</template>")
			.and(brjs).hasVersion("dev")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(workbench).indexPageRequires("appns/bs/b1/Class1");
		when(workbench).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("v/dev/some/path");
	}
	
	@Test
	public void bladeHtmlInDefaultBladesetCanBeBundled() throws Exception {
		given(bladeInDefaultBladeset).hasClass("appns/b1/BladeClass")
			.and(bladeInDefaultBladeset).containsResourceFileWithContents("html/view.html", "<template id='appns.b1.my.view'>Blade Content</template>")
			.and(aspect).indexPageRequires("appns/b1/BladeClass");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("Blade Content");
	}
	
	@Test
	public void htmlInDefaultAspectCanBeBundled() throws Exception {
		given(defaultAspect).hasClass("appns/AspectClass")
			.and(defaultAspect).containsResourceFileWithContents("html/view.html", "<template id='appns.my.view'>Aspect Content</template>")
			.and(defaultAspect).indexPageRequires("appns/AspectClass");
		when(defaultAspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("Aspect Content");
	}
	
	@Test
	public void templateTagsArentWrapped() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='the-id'>TESTCONTENT</template>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("<!-- view.html -->\n<template id='the-id'>TESTCONTENT</template>");
	}
	
	@Test
	public void nonTemplateTagsAreAutomaticallyWrapped() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='the-id'>TESTCONTENT</div>");
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("<!-- view.html -->\n<template id='the-id' data-auto-wrapped='true'>\n<div id='the-id'>TESTCONTENT</div></template>");
	}
	
	@Test
	public void scriptTagsGenerateAWarning() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<script type='text/html' id='the-id'>TESTCONTENT</script>")
			.and(logging).enabled();
		when(aspect).requestReceivedInDev("html/bundle.html", response);
		then(response).containsText("<!-- view.html -->\n<template id='the-id' data-auto-wrapped='true'>\n<script type='text/html' id='the-id'>TESTCONTENT</script></template>")
			.and(logging).warnMessageReceived(SCRIPT_TEMPLATE_WARNING, "the-id");
	}
}
