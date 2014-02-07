package org.bladerunnerjs.spec.plugin.bundler.html;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
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
	public void aspectHTMlFilesAreBundled() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='xxxxx.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.view", "appns.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.*");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithDuplicateIDs() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>").
		and(aspect).resourceFileContains("html/view2.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class,  "appns.view");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}

	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		given(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	// TODO this test should pass
	@Ignore 
	@Test
	public void bladeHTMlFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		given(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).resourceFileContains("html/view.html", "<div id='appns.bs.b1.view'>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).resourceFileContains("html/aspect-view.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.*");
	}
	
	@Test
	public void bladeHTMlFilesBundleFailsWithNoIDAttribute() throws Exception {
		given(blade).resourceFileContains("html/view.html", "<div>TESTCONTENT</div>")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "<div>", "appns.bs.b1.*");
	}
	
	@Test
	public void enforcedNamespacesAreCalculatedUsingTheAssetLocation() throws Exception {
		given(blade).containsFileWithContents("src/appns/bs/b1/some/pkg/view.html", "<div id='appns.bs.badnamespace.view'>TESTCONTENT</div>")
			.and(blade).hasClass("appns.bs.b1.some.pkg.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.some.pkg.Class1");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(exceptions).verifyException(NamespaceException.class, "appns.bs.badnamespace.view", "appns.bs.b1.some.pkg.*");
	}
	
}
