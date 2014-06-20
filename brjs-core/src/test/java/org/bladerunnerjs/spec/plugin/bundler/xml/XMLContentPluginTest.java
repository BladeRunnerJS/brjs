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
import org.junit.Ignore;
import org.junit.Test;

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
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml");
	}
	
	@Test
	public void ifThereAreXmlFilesButNoBundleConfigThenNoRequestsWillBeGenerated() throws Exception {
		given(aspect).containsResourceFile("config.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml");
	}
	
	@Test
	public void ifThereIsABundleConfigButNoXmlFilesThenNoRequestsWillBeGenerated() throws Exception {
		given(brjsConf).containsFile("bundleConfig.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml");
	}
	
	@Test
	public void ifThereIsBothABundleConfigAndXmlFilesThenRequestsWillBeGenerated() throws Exception {
		given(brjsConf).containsFile("bundleConfig.xml")
			.and(aspect).containsResourceFile("config.xml");
		then(aspect).prodAndDevRequestsForContentPluginsAre("xml", "xml/bundle.xml");
	}
	
	@Test
	public void aspectXmlFilesAreBundled() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("rootElem.xml", rootElem(templateElems()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElems()));
	}
	
	@Test
	public void aspectXmlFilesAreBundledFromNestedDirectory() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("xml/rootElem.xml", rootElem(templateElems()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(rootElem(templateElems()));
	}
	
	@Test
	public void aspectXmlFilesBundlingFailsWithWrongNamespace() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("xml/rootElem.xml", rootElem(mergeElem("xxxxx.Provider"), true))
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.Provider", "appns.bs.b1.*" );
	}
	
	@Test
	public void xmlFilesWithinTheAspectArenNotNamespaced() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig()).
		and(aspect).containsResourceFileWithContents("xml/rootElem.xml", rootElem(mergeElem("xxxxx.Provider"), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aspectXmMergesEmptyGridDefinitions() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("xml/rootElem1.xml", rootElem("", true))
			.and(aspect).containsResourceFileWithContents("xml/rootElem2.xml", rootElem("", true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem(""));
	}
	
	@Test
	public void aspectXmMergesDuplicateDataMappingElements() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("xml/rootElem1.xml", rootElem( mergeElem("appns.SomeClass1"), true))
			.and(aspect).containsResourceFileWithContents("xml/rootElem2.xml", rootElem( mergeElem("appns.SomeClass1"), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.SomeClass1")));
	}
	
	@Ignore //This test runs in eclipse but fails in gradle build
	@Test 
	//This tests that merge elements - mergeElem are correctly merged within a template elements - dataMappings
	public void aspectXmlDoesNotMergeDifferentDataMappingElements() throws Exception {
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("xml/rootElem1.xml", rootElem( mergeElem("appns.SomeClass1"), true))
			.and(aspect).containsResourceFileWithContents("xml/rootElem2.xml", rootElem( mergeElems1(), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(mergeElems2());
	}
	
	@Test
	public void badlyFormedXMLFails() throws Exception {
		
		String badXml = "<xxx=\">";
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", bundleConfig())
			.and(aspect).containsResourceFileWithContents("xml/rootElem3.xml", rootElem(badXml , true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(XMLStreamException2.class);
	}
	
	@Test
	public void anXmlWithUnknownRootNodeFails() throws Exception {
		
		String badXml = "<unknownRootElem></unknownRootElem>";
		String config = bundleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/rootElem3.xml", rootElem(badXml , true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(ContentProcessingException.class, "unknownRootElem");
	}
	
	@Test
	public void bladeXmlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		String config = bundleConfig();
		given(blade).containsResourceFileWithContents("xml/rootElem.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1"), true))
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
		    .and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config);
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(rootElem( mergeElem("appns.bs.b1.SomeClass1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		String config = bundleConfig();
		given(blade).containsResourceFileWithContents("xml/rootElem.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1"), true))
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
			.and(blade).containsResourceFileWithContents("xml/rootElem.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1"), true))
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
			.and(blade).containsResourceFileWithContents("xml/rootElem.xml", rootElem( mergeElem("appns.bs.b1.SomeClass1"), true))
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
		given(aspect).containsResourceFileWithContents("rootElem.xml", rootElem(arbitraryElem()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(arbitraryElem());
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(referencingElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(referencedElem(id)));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(referencingElem(id))
			.and(response).containsText(referencedElem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByAClass() throws Exception {
		given(aspect).indexPageRequires("appns/Class")
			.and(aspect).classRequires("appns/Class", "appns/bs1/b1/gridname")
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(referencedElem("appns.bs1.b1.gridname")));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(referencedElem("appns.bs1.b1.gridname"));
	}
	
	@Test
	public void xmlInBladesetResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.gridname";
		given(aspect).containsResourceFileWithContents("aspect-config.xml", rootElem(referencingElem(id)))
			.and(bladeset).containsResourceFileWithContents("bladeset-config.xml", rootElem(referencedElem(id)))
			.and(bladeset).hasClass("appns/bs/Class")
			.and(aspect).indexPageRefersTo("appns.bs1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(referencingElem(id))
			.and(response).containsText(referencedElem(id));
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInWorkbench() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(workbench).containsResourceFileWithContents("workbench-config.xml", rootElem(referencingElem(id)))
			.and(blade).containsResourceFileWithContents("blade-config.xml", rootElem(referencedElem(id)));
		when(workbench).requestReceived("xml/bundle.xml", response);
		then(response).containsText(referencingElem(id))
			.and(response).containsText(referencedElem(id));
	}
	
	private String bundleConfig(){
		String content = "<?xml version=\"1.0\"?> "
		 + "<bundleConfig xmlns=\"http://schema.caplin.com/CaplinTrader/bundleConfig\">"
			+ "<resource rootElement=\"rootElem\""
			+ "	 templateElements=\"templateElem1, templateElem2, templateElem2\""
			+ "	 mergeElements=\"mergeElem, alternateMergeElem@custom-id\"/>"
		+ "</bundleConfig>";
		return content;
	}
	
	private String rootElem(String input){
		return rootElem(input, false);
	}
	
	private String rootElem(String input, boolean includePreamble ){
		String result = "";
		if(includePreamble){
			result = "<?xml version=\"1.0\" ?>";
		}
		result += "<rootElem xmlns=\"http://schema.acme.org/schema\">"
		+ input 
		+ "</rootElem>";
		return result;
	}
	
	private String arbitraryElem() {
		return "<a>content</a>";
	}
	
	private String referencingElem(String id) {
		return "<a gridname='" + id + "'>content</a>";
	}
	
	private String referencedElem(String id) {
		return "<b id='" + id + "'>othercontent</b>";
	}
	
	private String mergeElem(String id){
		String content = ""
			+ "<templateElem1>"
			+ "<mergeElem id=\"" + id + "\" className=\"OtherClass1\"></mergeElem>"
			+ "</templateElem1>";
		return content;
	}
	
	private String mergeElems1(){
		String content = ""
			+ "<templateElem1>"
			+ "<mergeElem id=\"appns.SomeClass2\" className=\"OtherClass2\"></mergeElem>"
			+ "</templateElem1>";
		return content;
	}
	
	private String mergeElems2(){
		String content = ""
			+ "<templateElem1>"
			+ "<mergeElem id=\"appns.SomeClass2\" className=\"OtherClass2\"></mergeElem>"
			+ "<mergeElem id=\"appns.SomeClass1\" className=\"OtherClass1\"></mergeElem>"
			+ "</templateElem1>";
		return content;
	}
	
	private String templateElems(){
		String content = ""
			+ "<templateElem1>"
			+ "<mergeElem id=\"appns.bs.b1.id1\" className=\"appns.Class1\"></mergeElem>"
			+ "</templateElem1>"
			+ "<templateElem2>"
			+ "<mergeElem id=\"appns.bs.b1.id2\" className=\"appns.Class2\"></mergeElem>"
			+ "</templateElem2>";
		return content;
	}
}
