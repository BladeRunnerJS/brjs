package org.bladerunnerjs.spec.plugin.bundler.xml;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
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
	private Workbench workbench = null;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
		.and(brjs).automaticallyFindsMinifierPlugins()
		.and(brjs).hasBeenCreated();
		
		brjsConf = brjs.conf();
		app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
	}
	
	@Test
	public void ifThereAreNoXmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		then(aspect).prodAndDevRequestsForContentPluginsAreEmpty("xml");
	}
	
	@Test
	public void ifThereAreXmlFilesButNoBundleConfigThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).containsResourceFile("config.xml");
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
	public void anXmlWithUnknownRootNodeFails() throws Exception {
		String config = bundleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("config.xml", rootElem("<unknownRootElem/>"));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(ContentProcessingException.class, "unknownRootElem");
	}
	
	@Test
	public void badlyFormedXMLFails() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config.xml", rootElem("<xxx=\">"));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(XMLStreamException2.class);
	}
	
	@Test
	public void aspectXmlFilesAreBundled() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("config.xml", rootElem(mergeElem("id1")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(bundleElem(bundleResourceElem("rootElem", rootElem(mergeElem("id1")))));
	}
	
	@Test
	public void aspectXmlFilesAreBundledFromNestedDirectory() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("xml/config.xml", rootElem(mergeElem("id1")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id1")));
	}
	
	@Test
	public void bundlingFailsWhenInvalidNamespaceIsUsed() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("config.xml", rootElem(mergeElem("xxxxx.Provider")))
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.Provider", "appns.bs.b1.*" );
	}
	
	@Test
	public void xmlFilesWithinTheAspectHaveNoNamespaceRestrictions() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("config.xml", rootElem(mergeElem("xxxxx.Provider")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void emptyXmlDocumentsAreMerged() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(""))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(""));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem(""));
	}
	
	@Test
	public void mergeElementsWithDifferentIdsAreMergedTogether() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(mergeElem("id1", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(mergeElem("id2", "Class2")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id1", "Class1"), mergeElem("id2", "Class2")));
	}
	
	@Test
	public void duplicateMergeElementsWithTheSameIdAreMergedToASingleElement() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(mergeElem("id", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(mergeElem("id", "Class2")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(mergeElem("id", "Class1")))
			.and(response).doesNotContainText(mergeElem("id", "Class2"));
	}
	
	@Test
	public void onlyASingleTemplateElementOfTheSameTypeIsKept() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem("")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem("")));
	}
	
	@Test
	public void templateElementsOfDifferentTypesAreSequenced() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem2("")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(""), templateElem2("")));
	}
	
	@Test
	public void templateElementsAreWrittenOutInDefinitionOrderRatherThanTheOrderTheyAreEncountered() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem2("")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem(""), templateElem2("")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(""), templateElem2("")));
	}
	
	@Test
	public void mergeElementsWithinTemplatesAreMerged() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem(mergeElem("id2"))));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(mergeElem("id1"), mergeElem("id2"))));
	}
	
	@Test
	public void mergeElementsWithinDifferentTemplateElementsAreKeptApart() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(templateElem2(mergeElem("id2"))));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElem(mergeElem("id1")), templateElem2(mergeElem("id2"))));
	}
	
	@Test public void mergeElemsWithCustomIdsAreSupported() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(alternateMergeElem("id1", "Class1")))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem(alternateMergeElem("id2", "Class2")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(alternateMergeElem("id1", "Class1"), alternateMergeElem("id2", "Class2")));
	}
	
	@Test public void documentsWithDifferentRootElementsAreKeptApart() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("config1.xml", rootElem(templateElem(mergeElem("id1"))))
			.and(aspect).containsResourceFileWithContents("config2.xml", rootElem2(templateElem(mergeElem("id2"))));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(bundleElem(
				bundleResourceElem("rootElem2", rootElem2(templateElem(mergeElem("id2")))),
				bundleResourceElem("rootElem", rootElem(templateElem(mergeElem("id1"))))
			));
	}
	
	@Test
	public void bladeXmlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		String config = bundleConfig();
		given(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
		    .and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config);
		when(aspect).requestReceived("xml/bundle.xml", response);
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
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		String config = bundleConfig();
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		String config = bundleConfig();
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)	
			.and(blade).containsResourceFileWithContents("xml/config.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1")))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void arbritaryXMLIsUnchangedWhenNoBundlerConfig() throws Exception {
		given(aspect).containsResourceFileWithContents("config.xml", rootElem(arbitraryElem()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(arbitraryElem());
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(refElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem(id)));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByAClass() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classRequires("appns/Class", "appns/bs1/b1/gridname")
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem("appns.bs1.b1.gridname")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(elem("appns.bs1.b1.gridname"));
	}
	
	@Test
	public void xmlInBladesetResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(refElem(id)))
			.and(bladeset).containsResourceFileWithContents("bladeset-config.xml", rootElem(elem(id)))
			.and(bladeset).hasClass("appns/bs/Class")
			.and(aspect).indexPageRefersTo("appns.bs1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInWorkbench() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(workbench).containsResourceFileWithContents("workbench-config.xml", rootElem(refElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(elem(id)));
		when(workbench).requestReceived("xml/bundle.xml", response);
		then(response).containsText(refElem(id))
			.and(response).containsText(elem(id));
	}
	
	private String bundleConfig(){
		String content = "<?xml version=\"1.0\"?> "
		 + "<bundleConfig xmlns=\"http://schema.caplin.com/CaplinTrader/bundleConfig\">"
			+ "<resource rootElement=\"rootElem\""
			+ "	 templateElements=\"templateElem1, templateElem2\""
			+ "	 mergeElements=\"mergeElem, alternateMergeElem@custom-id\"/>"
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
