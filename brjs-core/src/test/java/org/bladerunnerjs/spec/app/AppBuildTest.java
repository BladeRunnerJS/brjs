package org.bladerunnerjs.spec.app;

import java.io.File;

import org.apache.commons.lang3.mutable.MutableLong;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class AppBuildTest extends SpecTest {
	private App app;
	private DirNode sdkLibsDir;
	private Aspect defaultAspect;
	private Aspect nonDefaultAspect;
	private File targetDir;
	private MutableLong versionNumber = new MutableLong();
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			sdkLibsDir = brjs.sdkLibsDir();
			defaultAspect = app.aspect("default");
			nonDefaultAspect = app.aspect("aspect2");
			targetDir = FileUtility.createTemporaryDirectory(AppBuildTest.class.getSimpleName());
	}
	
	@Test
	public void builtAppHasLocaleForwardingPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(sdkLibsDir).containsFileWithContents("locale-forwarder.js", "Locale Forwarder")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("index.html", "Locale Forwarder");
	}
	
	@Test
	public void builtAppHasAspectIndexPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("en/index.html");
	}
	
	@Test
	public void indexPageHasLogicalTagsReplaced() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("en/index.html", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void builtAppHasLocalizedIndexPagePerLocale() throws Exception {
		given(app).containsFileWithContents("app.conf", "requirePrefix: app\nlocales: en, de")
			.and(defaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("en/index.html", "/i18n/en.js")
			.and(targetDir).containsFileWithContents("de/index.html", "/i18n/de.js");
	}
	
	@Test
	public void jspIndexPagesAreUnprocessedAndKeepTheJspSuffix() throws Exception {
		given(defaultAspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>\n<@js.bundle@/>")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("en/index.jsp", "<%= 1 + 2 %>")
			.and(targetDir).containsFileWithContents("en/index.jsp", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void nonDefaultAspectsHaveTheSameIndexPagesButWithinANamedDirectory() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(nonDefaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("en/index.html", "/js/prod/combined/bundle.js")
			.and(targetDir).containsFileWithContents("aspect2/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("aspect2/en/index.html", "/i18n/en.js");
	}
	
	@Test
	public void aSingleSetOfBundlesAreCreated() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir, versionNumber);
		then(targetDir).containsFile("v/" + versionNumber + "/bundle.html")
			.and(targetDir).containsFile("v/" + versionNumber + "/i18n/en.js");
	}
	
	@Test
	public void theWebInfDirectoryIsCopiedIfThereIsOne() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(sdkLibsDir).containsFile("locale-forwarder.js")
			.and(app).hasDir("WEB-INF/lib")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsDir("WEB-INF/lib");
	}
	
	// TODO: add tests that show we only emit bundles for content-plugins which don't depend on a tag, or otherwise if the tag has been used in any of the aspect index pages
}
