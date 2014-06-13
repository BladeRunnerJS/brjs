package org.bladerunnerjs.spec.plugin.bundler.html;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class HTMLContentPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}
	
	@Test
	public void ifThereAreNoHtmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		then(aspect).prodAndDevRequestsForContentPluginsAre("html");
	}
	
	@Test
	public void ifThereAreHtmlFilesThenASingleRequestWillBeGenerated() throws Exception {
		given(aspect).containsResourceFile("template.html");
		then(aspect).prodAndDevRequestsForContentPluginsAre("html", "html/bundle.html");
	}
	
	@Test
	public void aspectHTMlFilesAreBundled() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(aspect).requestReceived("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.view", "appns.bs.b1.*");
	}
	
	@Test
	public void htmlTemplatesWithinAspectArentNamespaced() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div>TESTCONTENT</div>");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithDuplicateIDs() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<div id='appns.view'>TESTCONTENT</div>").
		and(aspect).containsResourceFileWithContents("html/view2.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class,  "appns.view");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceived("html/bundle.html", response);
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
		when(aspect).requestReceived("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}

	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceived("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		
		when(aspect).requestReceived("html/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.*");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(blade).containsResourceFileWithContents("html/view.html", "<div>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.bs.b1.*");
	}
	
	@Test
	public void enforcedNamespacesAreCalculatedUsingTheAssetLocation() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/some/pkg/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns/bs/b1/some/pkg/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.some.pkg.Class1");
		when(aspect).requestReceived("html/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.some.pkg.*");
	}
	
}
