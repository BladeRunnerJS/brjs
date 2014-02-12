package org.bladerunnerjs.spec.plugin.bundler.xml;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.xml.stream.XMLStreamException2;

public class XMLContentPluginTest extends SpecTest{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade = null;
	
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
	public void aspectXmlFilesAreBundled() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("gridDefinitions.xml", wrap(getFullGridDefinition()));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsText(wrap(getFullGridDefinition()));
	}
	
	
	
	@Test
	public void aspectXmlFilesAreBundledFromNestedDirectory() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions.xml", wrap(getFullGridDefinition()));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsText(wrap(getFullGridDefinition()));
	}
	

	@Test
	public void aspectXmlFilesBundlingFailsWithWrongNamespace() throws Exception {
		
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions.xml", wrap(getProviderMapping("xxxxx.Provider"), true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(exceptions).verifyException(NamespaceException.class, "xxxxx.Provider", "appns.*" );
	}
	
	@Test
	public void aspectXmMergesEmptyGridDefinitions() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions1.xml", wrap("", true)).
		and(aspect).resourceFileContains("xml/gridDefinitions2.xml", wrap("", true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap(""));
	}
	
	@Test
	public void aspectXmMergesDuplicateDataMappingElements() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions1.xml", wrap( getProviderMapping("appns.DatProvider1"), true)).
		and(aspect).resourceFileContains("xml/gridDefinitions2.xml", wrap( getProviderMapping("appns.DatProvider1"), true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap( getProviderMapping("appns.DatProvider1")));
	}
	
	@Ignore //This test runs in eclipse but fails in gradle build
	@Test 
	//This tests that merge elements - dataProviderMapping are correctly merged within a template elements - dataMappings
	public void aspectXmlDoesNotMergeDifferentDataMappingElements() throws Exception {
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions1.xml", wrap( getProviderMapping("appns.DatProvider1"), true)).
		and(aspect).resourceFileContains("xml/gridDefinitions2.xml", wrap( getDataProviderMapping2ForMerge(), true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(getDataProviderMapping1and2ForMerge());
	}
	
	@Test
	public void badlyFormedXMLFails() throws Exception {
		
		String badXml = "<xxx=\">";
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions3.xml", wrap(badXml , true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(exceptions).verifyException(XMLStreamException2.class);
	}
//	
	@Test
	public void axmlWithUnknownRootNodeFails() throws Exception {
		
		String badXml = "<wibble></wibble>";
		String config = getSimpleConfig();
		given(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config).
		and(aspect).resourceFileContains("xml/gridDefinitions3.xml", wrap(badXml , true));
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(exceptions).verifyException(BundlerProcessingException.class, "wibble");
	}
	
	@Test
	public void bladeXmlFilesAreBundledIfTheirClassIsReferencedInsideIndexPage() throws Exception {
		String config = getSimpleConfig();
		given(blade).resourceFileContains("xml/gridDefinitions.xml", wrap( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
		    .and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config);
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfAspectSrcRefersToBlade() throws Exception {
		String config = getSimpleConfig();
		given(blade).resourceFileContains("xml/gridDefinitions.xml", wrap( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).classDependsOn("appns.Class1", "appns.bs.b1.Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAspectIndexPage() throws Exception {
		String config = getSimpleConfig();
		given(blade).hasClass("appns.bs.b1.Class1")
			.and(blade).resourceFileContains("xml/gridDefinitions.xml", wrap( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)
			.and(aspect).containsFileWithContents("index.html", "appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap( getProviderMapping("appns.bs.b1.DatProvider1")));
	}
	
	@Test
	public void bladeXMLFilesAreBundledIfTheBladeIsReferredToByAnAspectHTMLResourceFile() throws Exception {
		String config = getSimpleConfig();
		given(blade).hasClass("appns.bs.b1.Class1")
			.and(brjs).hasConfigurationFileWithContent("bundleConfig.xml", config)	
			.and(blade).resourceFileContains("xml/gridDefinitions.xml", wrap( getProviderMapping("appns.bs.b1.DatProvider1"), true))
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClass("appns.bs.b1.Class1")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.AppClass")
			
			.and(aspect).resourceFileContains("html/aspect-view.html", "<div id='appns.stuff'>appns.bs.b1.Class1</div>")
			.and(aspect).containsFileWithContents("index.html", "appns.AppClass");
		when(app).requestReceived("/default-aspect/bundle.xml", response);
		then(response).containsTextOnce(wrap( getProviderMapping("appns.bs.b1.DatProvider1")));
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
	
	private String wrap(String input){
		return wrap(input, false);
	}
	
	private String wrap(String input, boolean includePreamble ){
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
