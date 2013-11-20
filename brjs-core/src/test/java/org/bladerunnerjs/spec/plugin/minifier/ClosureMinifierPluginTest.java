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
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/js.bundle", response);
		then(response).containsText(minifyWhitespaceContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureSimpleOption() throws Exception
	{
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).classFileHasContent("novox.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-simple/js.bundle", response);
		then(response).containsText(minifySimpleContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureAdvancedOption() throws Exception
	{
		given(aspect).hasClass("novox.Class1")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(aspect).classFileHasContent("novox.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-advanced/js.bundle", response);
		then(response).containsText(minifyAdvancedContent);
	}
	
	
	@Test
	public void closureMinifierHandlesRequestsWithMultipleFiles() throws Exception
	{
		given(blade).packageOfStyle("novox", "caplin-js")
			.and(blade).hasClasses("novox.Class1", "novox.Class2")
			.and(aspect).indexPageRefersTo("novox.Class1")
			.and(blade).classRefersTo("novox.Class1", "novox.Class2");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/js.bundle", response);
		then(response).textEquals("novox.Class2=function(){};novox.Class1=function(){};br.extend(novox.Class1,novox.Class2);");
	}
	
	@Test
	public void closureMinifierHandlesAMixOfSourceFileTypes() throws Exception
	{
		given(blade).packageOfStyle("novox.cjs", "caplin-js")
			.and(blade).packageOfStyle("novox.node", "node.js")
			.and(blade).hasClasses("novox.cjs.Class", "novox.node.Class")
			.and(aspect).indexPageRefersTo("novox.cjs.Class")
			.and(blade).classDependsOn("novox.cjs.Class",  "novox.node.Class");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/js.bundle", response);
		then(response).textEquals("novox.node.Class=function(){};var Class=require(\"novox/node/Class\");novox.cjs.Class=function(){};");
	}
	
}
