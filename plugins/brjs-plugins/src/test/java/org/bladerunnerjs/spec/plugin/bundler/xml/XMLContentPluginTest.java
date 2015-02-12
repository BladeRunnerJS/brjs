package org.bladerunnerjs.spec.plugin.bundler.xml;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.plugin.bundlers.aliasing.NamespaceException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.sun.xml.stream.XMLStreamException2;

public class XMLContentPluginTest extends SpecTest{
	private DirNode brjsConf;
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade = null;
	private BladeWorkbench workbench = null;
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
		
		brjsConf = brjs.conf();
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
	public void ifThereAreNoXmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("xml");
	}
	
	@Test
	public void ifThereIsABundleConfigButNoXmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(brjsConf).containsFile("bundleConfig.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("xml");
	}
	
	@Test
	public void ifThereIsBothABundleConfigAndXmlFilesThenRequestsWillBeGenerated() throws Exception {
		given(brjsConf).containsFile("bundleConfig.xml")
			.and(aspect).containsResourceFile("config.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml", "xml/bundle.xml");
	}
	
	@Test
	public void ifThereAreXmlFilesButNoBundleConfigThenRequestsWillStillBeGenerated() throws Exception {
		given(aspect).containsResourceFile("config.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml", "xml/bundle.xml");
	}
	
	@Test
	public void anXmlWithUnknownRootNodeFails() throws Exception {
		String config = bundleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("config.xml", rootElem("<unknownRootElem/>"));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(exceptions).verifyException(ContentProcessingException.class, "unknownRootElem");
	}
	
	@Test
	public void badlyFormedXMLFails() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config.xml", rootElem("<xxx=\">"));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(exceptions).verifyException(XMLStreamException2.class);
	}
	
	@Test
	public void xmlFilesAreConcatenatedIfNoXmlConfigExists() throws Exception {
		given(aspect).containsResourceFileWithContents("file1.xml", rootElem(mergeElem("id1")))
			.and(aspect).containsResourceFileWithContents("file2.xml", rootElem(mergeElem("id2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText( rootElem(mergeElem("id1")) + rootElem(mergeElem("id2")) );
	}
	
	@Test
	public void aspectXmlFilesAreBundled() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("config.xml", rootElem(mergeElem("id1")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem(bundleResourceElem("rootElem", rootElem(mergeElem("id1")))));
	}
	
	@Test
	public void aspectXmlFilesAreBundledFromNestedDirectory() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("xml/config.xml", rootElem(mergeElem("id1")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id1")));
	}
	
	@Test
	public void xmlPrologIsStrippedFromXmlDocuments() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("config.xml", rootElemWithXmlProlog(mergeElem("id1")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem(bundleResourceElem("rootElem", rootElem(mergeElem("id1")))));
	}
	
	@Test
	public void xmlPrologOnlyAppearsOnce() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config.xml", rootElemWithXmlProlog(mergeElem("id1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem2WithXmlProlog(mergeElem("id1")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce("<?xml ");
	}
	
	@Test
	public void xmlFilesAreJustConcatenatedIfNoBundleConfigExists_EvenIfItResultsInMultipleXmlPrologs() throws Exception {
		given(aspect).containsResourceFileWithContents("config.xml", rootElemWithXmlProlog(mergeElem("id1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem2WithXmlProlog(mergeElem("id1")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextANumberOfTimes("<?xml ", 2);
	}
	
	@Test
	public void bundlingFailsWhenInvalidNamespaceIsUsed() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("config.xml", rootElem(mergeElem("xxxxx.Provider")))
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.Provider", "appns.bs.b1.*" );
	}
	
	@Test
	public void xmlFilesWithinTheAspectHaveNoNamespaceRestrictions() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("config.xml", rootElem(mergeElem("xxxxx.Provider")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void emptyXmlDocumentsAreMerged() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(""))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(""));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem(""));
	}
	
	@Test
	public void mergeElementsWithDifferentIdsAreMergedTogether() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(mergeElem("id1", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(mergeElem("id2", "Class2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id1", "Class1"), mergeElem("id2", "Class2")));
	}
	
	@Test
	public void duplicateMergeElementsWithTheSameIdAreMergedToASingleElement() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(mergeElem("id", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(mergeElem("id", "Class2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id", "Class1")))
			.and(response).doesNotContainText(mergeElem("id", "Class2"));
	}
	
	@Test
	public void onlyASingleTemplateElementOfTheSameTypeIsKept() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem("")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem("")));
	}
	
	@Test
	public void templateElementsOfDifferentTypesAreSequenced() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem2("")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(""), templateElem2("")));
	}
	
	@Test
	public void templateElementsAreWrittenOutInDefinitionOrderRatherThanTheOrderTheyAreEncountered() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem2("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem(""), templateElem2("")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(""), templateElem2("")));
	}
	
	@Test
	public void mergeElementsWithinTemplatesAreMerged() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem(mergeElem("id2"))));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(mergeElem("id1"), mergeElem("id2"))));
	}
	
	@Test
	public void mergeElementsWithinDifferentTemplateElementsAreKeptApart() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem2(mergeElem("id2"))));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(mergeElem("id1")), templateElem2(mergeElem("id2"))));
	}
	
	@Test public void mergeElemsWithCustomIdsAreSupported() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(alternateMergeElem("id1", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(alternateMergeElem("id2", "Class2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(alternateMergeElem("id1", "Class1"), alternateMergeElem("id2", "Class2")));
	}
	
	@Test public void mergingAnonymousElemsIsSupported() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("config1.xml", rootElem(anonymousMergeElem("Class1")))
			.and(blade).containsResourceFileWithContents("config2.xml", rootElem(anonymousMergeElem("Class2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(anonymousMergeElem("Class1"), anonymousMergeElem("Class2")));
	}
	
	@Test public void mergingAnonymousElemsWhereOneHasAnIDIsSupported() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("config1.xml", rootElem(anonymousMergeElemWithID("appns.bs.b1.ID1", "Class1")))
			.and(blade).containsResourceFileWithContents("config2.xml", rootElem(anonymousMergeElem("Class2")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(anonymousMergeElemWithID("appns.bs.b1.ID1", "Class1"), anonymousMergeElem("Class2")));
	}
	
	@Test public void mergingAnonymousElemsWhereOneHasAnIDAndBothDontHaveClassesIsSupported() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).containsResourceFileWithContents("config1.xml", rootElem(anonymousMergeElemWithID("appns.bs.b1.ID1", null)))
			.and(blade).containsResourceFileWithContents("config2.xml", rootElem(anonymousMergeElem(null)));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(rootElem(anonymousMergeElemWithID("appns.bs.b1.ID1", null), anonymousMergeElem(null)));
	}
	
	@Test public void documentsWithDifferentRootElementsAreKeptApart() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem2(templateElem(mergeElem("id2"))));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem(
				bundleResourceElem("rootElem", rootElem(templateElem(mergeElem("id1")))),
				bundleResourceElem("rootElem2", rootElem2(templateElem(mergeElem("id2"))))
			));
	}
	
	@Test
	public void bladeXmlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		String config = bundleConfig();
		given(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
		    .and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config);
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		String config = bundleConfig();
		given(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.bs.b1.Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		String config = bundleConfig();
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		String config = bundleConfig();
		given(blade).hasNamespacedJsPackageStyle()
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)	
			.and(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void arbritaryXMLIsUnchangedWhenNoBundlerConfig() throws Exception {
		given(aspect).containsResourceFileWithContents("config.xml", rootElem(arbitraryElem()));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(arbitraryElem());
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(refElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem(id)));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByAClass() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classRequires("appns/Class", "appns/bs1/b1/gridname")
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem("appns.bs1.b1.gridname")));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(elem("appns.bs1.b1.gridname"));
	}
	
	@Test
	public void xmlInBladesetResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(refElem(id)))
			.and(bladeset).containsResourceFileWithContents("bladeset-config.xml", rootElem(elem(id)))
			.and(bladeset).hasClass("appns/bs/Class")
			.and(aspect).indexPageRefersTo("appns.bs1.Class");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInWorkbench() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(workbench).containsResourceFileWithContents("workbench-config.xml", rootElem(refElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem(id)));
		when(workbench).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	@Test public void bundlePathTagIsReplacedForDev() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).containsResourceFileWithContents("config.xml", templateElem("@bundlePath@/some/path"));
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem("\n",templateElem("v/dev/some/path")));
	}
	
	@Test public void bundlePathTagIsReplacedForProd() throws Exception {
		given(brjs).hasProdVersion("1234")
		.and(aspect).containsResourceFileWithContents("config.xml", templateElem("@bundlePath@/some/path"));
		when(aspect).requestReceivedInProd("xml/bundle.xml", response);
		then(response).containsText(bundleElem("\n", templateElem("v/1234/some/path")));
	}
	
	@Test
	public void bundlePathTagIsReplacedForWorkbench() throws Exception {
		given(blade).containsResourceFileWithContents("xml/myconfig.xml", "@bundlePath@/some/path")
			.and(brjs).hasDevVersion("dev")
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(workbench).indexPageRequires("appns/bs/b1/Class1");
		when(workbench).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText("v/dev/some/path"); 
	}
	
	@Test
	public void bladeXmlInDefaultBladesetCanBeBundled() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
    		.and(bladeInDefaultBladeset).hasClass("appns/b1/BladeClass")
			.and(bladeInDefaultBladeset).containsResourceFileWithContents("config.xml", rootElem(mergeElem("appns.b1.ID")))
			.and(aspect).indexPageRequires("appns/b1/BladeClass");
		when(aspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem(bundleResourceElem("rootElem", rootElem(mergeElem("appns.b1.ID")))));
	}
	
	@Test
	public void bladeXmlInDefaultAspectCanBeBundled() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
    		.and(defaultAspect).hasClass("appns/AspectClass")
    		.and(defaultAspect).containsResourceFileWithContents("config.xml", rootElem(mergeElem("appns.ID")))
    		.and(defaultAspect).indexPageRequires("appns/AspectClass");
		when(defaultAspect).requestReceivedInDev("xml/bundle.xml", response);
		then(response).containsText(bundleElem(bundleResourceElem("rootElem", rootElem(mergeElem("appns.ID")))));
	}
	
	
	
	/*
	 * PRIVATE METHODS
	 */
	
	private String bundleConfig(){
		String content = "<?xml version=\"1.0\"?> "
		 + "<bundleConfig xmlns=\"http://schema.caplin.com/CaplinTrader/bundleConfig\">"
			+ "<resource rootElement=\"rootElem\""
			+ "	 templateElements=\"templateElem1, templateElem2\""
			+ "	 mergeElements=\"mergeElem, alternateMergeElem@custom-id, anonymousMergeElem\"/>"
			+ "<resource rootElement=\"rootElem2\""
			+ "	 templateElements=\"templateElem1\""
			+ "	 mergeElements=\"mergeElem\"/>"
		+ "</bundleConfig>";
		return content;
	}
	
	private String bundleElem(String... input) {
		String result = ""
			+ "<bundle>"
			+ Joiner.on("").join(input)
			+ "</bundle>";
			return result;
	}
	
	private String bundleResourceElem(String rootElem, String... input) {
		String result = ""
			+ "<resource name=\"" + rootElem + "\">"
			+ Joiner.on("").join(input)
			+ "</resource>";
			return result;
	}
	
	private String rootElem(String... input) {
		String result = ""
			+ "<rootElem xmlns=\"http://schema.acme.org/schema\">"
			+ Joiner.on("").join(input)
			+ "</rootElem>";
			return result;
	}
	
	private String rootElemWithXmlProlog(String... input) {
		return "<?xml version='1.0' encoding='UTF-8'?>\n" + rootElem(input);
	}
	
	private String rootElem2WithXmlProlog(String... input) {
		return "<?xml version='1.0' encoding='UTF-8'?>\n" + rootElem2(input);
	}
	
	private String rootElem2(String... input) {
		String result = ""
			+ "<rootElem2 xmlns=\"http://schema.acme.org/schema\">"
			+ Joiner.on("").join(input)
			+ "</rootElem2>";
			return result;
	}
	
	private String templateElem(String... input) {
		return "<templateElem1>" + Joiner.on("").join(input) + "</templateElem1>";
	}
	
	private String templateElem2(String... input) {
		return "<templateElem2>" + Joiner.on("").join(input) + "</templateElem2>";
	}
	
	private String mergeElem(String id) {
		return mergeElem(id, "TheClass");
	}
	
	private String mergeElem(String id, String className){
		return "<mergeElem id=\"" + id + "\" className=\"" + className + "\"></mergeElem>";
	}
	
	private String alternateMergeElem(String id, String className){
		return "<alternateMergeElem custom-id=\"" + id + "\" className=\"" + className + "\"></alternateMergeElem>";
	}
	
	private String anonymousMergeElemWithID(String id, String className){
		if (className != null) {
			return "<anonymousMergeElem id=\"" + id + "\" className=\"" + className + "\"></anonymousMergeElem>";
		}
		return "<anonymousMergeElem id=\"" + id + "\"></anonymousMergeElem>";
	}
	
	private String anonymousMergeElem(String className){
		if (className != null) {
			return "<anonymousMergeElem className=\"" + className + "\"></anonymousMergeElem>";
		}
		return "<anonymousMergeElem></anonymousMergeElem>";		
	}
	
	private String arbitraryElem() {
		return "<a>content</a>";
	}
	
	private String elem(String id) {
		return "<elem id='" + id + "'/>";
	}
	
	private String refElem(String id) {
		return "<elem ref-id='" + id + "'/>";
	}
}
