package org.bladerunnerjs.spec.plugin.minifier;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
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
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).classFileHasContent("mypkg.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).containsText(minifyWhitespaceContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureSimpleOption() throws Exception
	{
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).classFileHasContent("mypkg.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-simple/bundle.js", response);
		then(response).containsText(minifySimpleContent);
	}
	
	@Test
	public void closureMinifierRunsForRequestsWithClosureAdvancedOption() throws Exception
	{
		given(aspect).hasClass("mypkg.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).classFileHasContent("mypkg.Class1", unminifiedContent);
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-advanced/bundle.js", response);
		then(response).containsText(minifyAdvancedContent);
	}
	
	// TODO: these tests need rewriting as they hard to read and maintain
	@Ignore
	@Test
	public void closureMinifierHandlesRequestsWithMultipleFiles() throws Exception
	{
		given(blade).hasPackageStyle("src/mypkg/bs/b1", "caplin-js")
			.and(blade).hasClasses("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2")
			.and(aspect).indexPageRefersTo("mypkg.bs.b1.Class1")
			.and(blade).classRefersTo("mypkg.bs.b1.Class1", "mypkg.bs.b1.Class2");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).textEquals("window.mypkg={\"bs\":{\"b1\":{}}};mypkg.bs.b1.Class2=function(){};mypkg.bs.b1.Class1=function(){};br.extend(mypkg.bs.b1.Class1,mypkg.bs.b1.Class2);mypkg.bs.b1.Class2=require(\"mypkg/bs/b1/Class2\");mypkg.bs.b1.Class1=require(\"mypkg/bs/b1/Class1\");");
	}
	
	@Ignore
	@Test
	public void closureMinifierHandlesAMixOfSourceFileTypes() throws Exception
	{
		given(blade).hasPackageStyle("src/mypkg.cjs", "caplin-js")
			.and(blade).hasPackageStyle("mypkg.node", "node.js")
			.and(blade).hasClasses("mypkg.cjs.Class", "mypkg.node.Class")
			.and(aspect).indexPageRefersTo("mypkg.cjs.Class")
			.and(blade).classRefersTo("mypkg.cjs.Class",  "mypkg.node.Class");
		when(app).requestReceived("/default-aspect/js/prod/en_GB/closure-whitespace/bundle.js", response);
		then(response).textEquals("window.mypkg={\"cjs\":{}};mypkg.cjs.Class=function(){};br.extend(mypkg.cjs.Class,mypkg.node.Class);mypkg.cjs.Class=require(\"mypkg/cjs/Class\");mypkg.node.Class=function(){};");
	}
	
}
