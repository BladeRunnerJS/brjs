package org.bladerunnerjs.spec.plugin.bundler.html;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class HTMLContentPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	
	@Test
	public void aspectHTMlFilesAreBundled() throws Exception {
		given(aspect).resourceFileContains("html/view.html", "<div id='appns.view'>TESTCONTENT</div>");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsText("TESTCONTENT");
	}
	
	@Test
	public void aspectHTMlFilesBundleFailsWithWrongNamespace() throws Exception {
	
		//given(logging).echoEnabled();
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
}
