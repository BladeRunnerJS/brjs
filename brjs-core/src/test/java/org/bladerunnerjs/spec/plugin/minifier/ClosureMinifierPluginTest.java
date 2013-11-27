package org.bladerunnerjs.spec.plugin.minifier;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class ClosureMinifierPluginTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private Blade blade;
	
	private StringBuffer response = new StringBuffer();
	private String unminifiedContent;
	private String minifyWhitespaceContent;
	private String minifySimpleContent;
	private String minifyAdvancedContent;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
    		.and(brjs).automaticallyFindsMinifiers()
    		.and(brjs).hasBeenCreated();
    		app = brjs.app("app1");
    		aspect = app.aspect("default");
    		blade = app.bladeset("bs").blade("b1");
			
		/* only closure compiler service used to calculate responses - http://closure-compiler.appspot.com/home */
		unminifiedContent = "function hello(name) {\n"+
				"  alert('Hello, ' + name);\n"+
				"}\n"+
				"hello('New user');\n"+
				"\n";
		minifyWhitespaceContent = "function hello(name){alert(\"Hello, \"+name)}hello(\"New user\");";
		minifySimpleContent		= "function hello(a){alert(\"Hello, \"+a)}hello(\"New user\");";
		minifyAdvancedContent	= "alert(\"Hello, New user\");";
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureWhitespaceOption() throws Exception
	{
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).classFileHasContent("novox.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).containsText(minifyWhitespaceContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureSimpleOption() throws Exception
	{
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).classFileHasContent("novox.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-simple/bundle.js", response);
		then(response).containsText(minifySimpleContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureAdvancedOption() throws Exception
	{
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).classFileHasContent("novox.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-advanced/bundle.js", response);
		then(response).containsText(minifyAdvancedContent);
	}
	
	
	@Test
	public void closureMinifierHandlesRequestsWithMultipleFiles() throws Exception
	{
		given(blade).hasPackageStyle("src/novox/bs/b1", "caplin-js")
			.and(blade).hasClasses("novox.bs.b1.Class1", "novox.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("novox.bs.b1.Class1")
			.and(blade).classRefersTo("novox.bs.b1.Class1", "novox.bs.b1.Class2");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).textEquals("window.novox={\"bs\":{\"b1\":{\"Class2\":{},\"Class1\":{}}}};novox.bs.b1.Class2=function(){};novox.bs.b1.Class1=function(){};br.extend(novox.bs.b1.Class1,novox.bs.b1.Class2);novox.bs.b1.Class2=require(\"novox/bs/b1/Class2\");novox.bs.b1.Class1=require(\"novox/bs/b1/Class1\");");
	}
	
	@Test
	public void closureMinifierHandlesAMixOfSourceFileTypes() throws Exception
	{
		given(blade).hasPackageStyle("novox.cjs", "caplin-js")
			.and(blade).hasPackageStyle("novox.node", "node.js")
			.and(blade).hasClasses("novox.cjs.Class", "novox.node.Class")
			.and(aspect).indexPageRefersTo("novox.cjs.Class")
			.and(blade).classRequires("novox.cjs.Class",  "novox.node.Class");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).textEquals("novox.node.Class=function(){};var Class=require(\"novox/node/Class\");novox.cjs.Class=function(){};");
	}
	
}
