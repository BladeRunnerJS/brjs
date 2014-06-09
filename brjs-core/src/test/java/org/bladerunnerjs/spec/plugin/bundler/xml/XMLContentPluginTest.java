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
		given(brjs).automaticallyFindsBundlers()
		.and(brjs).automaticallyFindsMinifiers()
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
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).containsResourceFileWithContents("gridDefinitions.xml", xml(getFullGridDefinition()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(xml(getFullGridDefinition()));
	}
	
	
	
	@Test
	public void aspectXmlFilesAreBundledFromNestedDirectory() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).containsResourceFileWithContents("xml/gridDefinitions.xml", xml(getFullGridDefinition()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(xml(getFullGridDefinition()));
	}
	

	@Test
	public void aspectXmlFilesBundlingFailsWithWrongNamespace() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("xml/gridDefinitions.xml", xml(getProviderMapping("xxxxx.Provider"), true))
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.Provider", "appns.bs.b1.*" );
	}
	
	@Test
	public void xmlFilesWithinTheAspectArenNotNamespaced() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).containsResourceFileWithContents("xml/gridDefinitions.xml", xml(getProviderMapping("xxxxx.Provider"), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void aspectXmMergesEmptyGridDefinitions() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions1.xml", xml("", true))
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions2.xml", xml("", true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml(""));
	}
	
	@Test
	public void aspectXmMergesDuplicateDataMappingElements() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions1.xml", xml( getProviderMapping("appns.DatProvider1"), true))
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions2.xml", xml( getProviderMapping("appns.DatProvider1"), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml( getProviderMapping("appns.DatProvider1")));
	}
	
	@Ignore //This test runs in eclipse but fails in gradle build
	@Test 
	//This tests that merge elements - dataProviderMapping are correctly merged within a template elements - dataMappings
	public void aspectXmlDoesNotMergeDifferentDataMappingElements() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions1.xml", xml( getProviderMapping("appns.DatProvider1"), true))
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions2.xml", xml( getDataProviderMapping2ForMerge(), true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(getDataProviderMapping1and2ForMerge());
	}
	
	@Test
	public void badlyFormedXMLFails() throws Exception {
		
		String badXml = "<xxx=\">";
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions3.xml", xml(badXml , true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(XMLStreamException2.class);
	}
//	
	@Test
	public void axmlWithUnknownRootNodeFails() throws Exception {
		
		String badXml = "<wibble></wibble>";
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsResourceFileWithContents("xml/gridDefinitions3.xml", xml(badXml , true));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(exceptions).verifyException(ContentProcessingException.class, "wibble");
	}
	
	@Test
	public void bladeXmlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		String config = getSimpleConfig();
		given(blade).containsResourceFileWithContents("xml/gridDefinitions.xml", xml( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
		    .and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config);
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		String config = getSimpleConfig();
		given(blade).containsResourceFileWithContents("xml/gridDefinitions.xml", xml( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classExtends("appns.Class1", "appns.bs.b1.Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		String config = getSimpleConfig();
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(blade).containsResourceFileWithContents("xml/gridDefinitions.xml", xml( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		String config = getSimpleConfig();
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)	
			.and(blade).containsResourceFileWithContents("xml/gridDefinitions.xml", xml( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			
			.and(aspect).containsResourceFileWithContents("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsTextOnce(xml( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	

	@Test
	public void arbritaryXMLIsUnchangedWhenNoBundlerConfig() throws Exception {
		given(aspect).containsResourceFileWithContents("gridDefinitions.xml", xml(getArbitraryXml()));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(getArbitraryXml());
	}
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(aspect).containsResourceFileWithContents("application.xml", xml(getReferencingXML(id)))
			.and(blade).containsResourceFileWithContents("wibble.xml", xml(getReferencedXML(id)));
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(getReferencingXML(id))
			.and(response).containsText(getReferencedXML(id));
	}
	
	
	@Test
	public void xmlInBladesetResourceIsBundledWhenReferencedByXMLInAspect() throws Exception {
		String id = "appns.bs1.gridname";
		given(aspect).containsResourceFileWithContents("application.xml", xml(getReferencingXML(id)))
			.and(bladeset).containsResourceFileWithContents("wibble.xml", xml(getReferencedXML(id)))
			.and(bladeset).hasClass("appns/bs/Class")
			.and(aspect).indexPageRefersTo("appns.bs1.Class");
		when(aspect).requestReceived("xml/bundle.xml", response);
		then(response).containsText(getReferencingXML(id))
			.and(response).containsText(getReferencedXML(id));
	}
	
	
	@Test
	public void xmlInBladeResourceIsBundledWhenReferencedByXMLInWorkbench() throws Exception {
		String id = "appns.bs1.b1.gridname";
		given(workbench).containsResourceFileWithContents("application.xml", xml(getReferencingXML(id)))
			.and(blade).containsResourceFileWithContents("wibble.xml", xml(getReferencedXML(id)));
		when(workbench).requestReceived("xml/bundle.xml", response);
		then(response).containsText(getReferencingXML(id))
			.and(response).containsText(getReferencedXML(id));
	}
	
	private String getReferencingXML(String id) {
		String content = ""
				+ "<a gridname='" + id + "'>content</a>";
		return content;
	}
	
	private String getReferencedXML(String id) {
		String content = ""
				+ "<b id='" + id + "'>othercontent</b>";
		return content;
	}
	
	private String getArbitraryXml() {
		String content = ""
				+ "<a>content</a>";
		return content;
	}
	
	public String getEmptyConfig(){
		
		String content = "<?xml version=\"1.0\"?> "
		 + "<bundleConfig xmlns=\"http://schema.caplin.com/CaplinTrader/bundleConfig\">"
		 + "</bundleConfig>";
		return content;
	}

	
	public String getSimpleConfig(){
		
		String content = "<?xml version=\"1.0\"?> "
		 + "<bundleConfig xmlns=\"http://schema.caplin.com/CaplinTrader/bundleConfig\">"
			+ "<resource rootElement=\"gridDefinitions\""
			+ "	templateElements=\"dataProviderMappings, decoratorMappings, templates, grids\""
			+ "	mergeElements=\"dataProviderMapping, decoratorMapping, gridTemplate, folder@name, grid\"/>"
		+ "</bundleConfig>";
		return content;
	}
	
	private String xml(String input){
		return xml(input, false);
	}
	
	private String xml(String input, boolean includePreamble ){
		String result = "";
		if(includePreamble){
			result = "<?xml version=\"1.0\" ?>";
		}
		result += "<gridDefinitions xmlns=\"http://schema.caplin.com/CaplinTrader/gridDefinitions\">"
		+ input 
		+ "</gridDefinitions>";
		return result;
	}
	
	private String getProviderMapping(String id){
		
		String content = ""
			+ "<dataProviderMappings>"
			+ "<dataProviderMapping id=\"" + id + "\" className=\"ProviderClass1\"></dataProviderMapping>"
			+ "</dataProviderMappings>";
		return content;
	}
	
	
	private String getDataProviderMapping2ForMerge(){
		
		String content = ""
			+ "<dataProviderMappings>"
			+ "<dataProviderMapping id=\"appns.DatProvider2\" className=\"ProviderClass2\"></dataProviderMapping>"
			+ "</dataProviderMappings>";
		return content;
	}
	
	private String getDataProviderMapping1and2ForMerge(){
		
		String content = ""
			+ "<dataProviderMappings>"
			+ "<dataProviderMapping id=\"appns.DatProvider2\" className=\"ProviderClass2\"></dataProviderMapping>"
			+ "<dataProviderMapping id=\"appns.DatProvider1\" className=\"ProviderClass1\"></dataProviderMapping>"
			+ "</dataProviderMappings>";
		return content;
	}
	
	
	private String getFullGridDefinition(){
		
		String content = ""
			+ "<dataProviderMappings>"
			+ "<dataProviderMapping id=\"appns.example.grid.rttpContainerGridDataProvider\" className=\"caplin.grid.RttpContainerGridDataProvider\"></dataProviderMapping>"
			+ "</dataProviderMappings>"
			+ "<decoratorMappings>"
			+ "<decoratorMapping id=\"appns.example.grid.columnMenuDecorator\" className=\"caplin.grid.decorator.ColumnHeaderMenuDecorator\"></decoratorMapping>"
			+ "</decoratorMappings>"
			+ "<templates>"
			+ "<gridTemplate id=\"appns.example.grid.fxGrid\" displayedColumns=\"description\">"
			+ "<decorators>"
			+ "<appns.example.grid.columnMenuDecorator></appns.example.grid.columnMenuDecorator>"
			+ "</decorators>"
			+ "<columnDefinitions>"
			+ "<column id=\"description\" fields=\"InstrumentDescription\" displayName=\"@{griddefinitions.currency}\" primaryFieldType=\"text\"></column>"
			+ "</columnDefinitions>"
			+ "</gridTemplate>"
			+ "</templates>";
		return content;
	}
	
	
}
