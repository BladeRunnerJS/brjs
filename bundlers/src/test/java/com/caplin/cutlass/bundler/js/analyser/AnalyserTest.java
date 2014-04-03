package com.caplin.cutlass.bundler.js.analyser;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.bundler.js.analyser.HTMLCodeUnitVisitor;
import com.caplin.cutlass.bundler.js.analyser.JsonCodeUnitVisitor;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

public class AnalyserTest
{
	private String testStructures = "src/test/resources/generic-bundler/bundler-structure-tests";

	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testStructures)));
	}
	
	@Ignore
	@Test
	public void testAnalyserOnApp1WithTextOutput() throws Exception
	{
		String seedDirName = testStructures + "/" + APPLICATIONS_DIR + "/test-app1/main-aspect";
		File seedDir = new File(seedDirName );
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getCodeAnalyser(seedDir);
		String result = codeAnalyser.emitString();
		
		String expected = "m1.html\n"
		 +  "m2.xml\n"
		 +  "b1.html\n"
		 +  "bs1.html\n"
		 +  "bs2.xml\n"
		 +  ".section.a.xmlDepend\n"
		 +  "m1.xml\n"
		 +  ".section.xmlDepend\n"
		 +  "bs2.html\n"
		 +  "m2.html\n"
		 +  ".section.htmlDepend\n"
		 +  "b1.xml\n"
		 +  ".section.a.blade1.xmlDepend\n"
		 +  "index.html\n"
		 +  ".section.app.main2\n"
		 +  "..lib2 - LIB\n"
		 +  "..jquery - LIB\n"
		 +  "..knockout - LIB\n"
		 +  "..lib1 - LIB\n"
		 +  "..section.app.main1 - STATIC\n"
		 +  "..section.a.app.bladeset1\n"
		 +  "...section.a.app.bladeset2\n"
		 +  "....section.a.blade1.app.blade1\n"
		 +  ".....section.a.blade1.app.blade2\n"
		 +  "c1.htm\n"
		 +  "b2.html\n"
		 +  ".section.a.blade1.htmlDepend\n"
		 +  "b2.xml\n"
		 +  "bs1.xml\n";
		
		assertEquals(expected, result);
	}
	
	@Ignore
	@Test
	public void testAnalyserOnApp1WithJSONOutput() throws Exception
	{
		String seedDirName = testStructures + "/" + APPLICATIONS_DIR + "/test-app1/main-aspect";
		File seedDir = new File(seedDirName );
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getCodeAnalyser(seedDir);
		JsonCodeUnitVisitor visitor = new JsonCodeUnitVisitor();
		codeAnalyser.emit(visitor);
		String result = visitor.getResult();
		
		String expected = "{ name : 'm1.html'}, "
		+ "{ name : 'm2.xml'}, "
		+ "{ name : 'b1.html'}, "
		+ "{ name : 'bs1.html'}, "
		+ "{ name : 'bs2.xml', children : [{ name : 'section.a.xmlDepend'} ]}, " 
		+ "{ name : 'm1.xml', children : [{ name : 'section.xmlDepend'} ]}, "
		+ "{ name : 'bs2.html'}, "
		+ "{ name : 'm2.html', children : [{ name : 'section.htmlDepend'} ]}, "
		+ "{ name : 'b1.xml', children : [{ name : 'section.a.blade1.xmlDepend'} ]}, "
		+ "{ name : 'index.html', children : [{ name : 'section.app.main2', children : [{ name : 'lib2'}, { name : 'jquery'}, { name : 'knockout'}, { name : 'lib1'}, { name : 'section.app.main1'}, { name : 'section.a.app.bladeset1', children : [{ name : 'section.a.app.bladeset2', children : [{ name : 'section.a.blade1.app.blade1', children : [{ name : 'section.a.blade1.app.blade2'} ]} ]} ]} ]} ]}, "
		+ "{ name : 'c1.htm'}, "
		+ "{ name : 'b2.html', children : [{ name : 'section.a.blade1.htmlDepend'} ]}, "
		+ "{ name : 'b2.xml'}, "
		+ "{ name : 'bs1.xml'}, "; 
		//TODO remove trailing comma
		assertEquals(expected, result);
	}
	
	@Ignore
	@Test
	public void testAnalyserOnApp1WithHTMLOutput() throws Exception
	{
		String seedDirName = testStructures + "/" + APPLICATIONS_DIR + "/test-app1/main-aspect";
		File seedDir = new File(seedDirName );
		CodeAnalyser codeAnalyser = CodeAnalyserFactory.getCodeAnalyser(seedDir);
		HTMLCodeUnitVisitor visitor = new HTMLCodeUnitVisitor(true);
		codeAnalyser.emit(visitor);
		
		String result = visitor.getResult();
		
		String expected = "<ul>\n"
		 + " <li>m1.html</li>\n"
		 + "</ul><ul>\n"
		 + " <li>m2.xml</li>\n"
		 + "</ul><ul>\n"
		 + " <li>b1.html</li>\n"
		 + "</ul><ul>\n"
		 + " <li>bs1.html</li>\n"
		 + "</ul><ul>\n"
		 + " <li>bs2.xml<ul>\n"
		 + "  <li>section.a.xmlDepend</li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>m1.xml<ul>\n"
		 + "  <li>section.xmlDepend</li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>bs2.html</li>\n"
		 + "</ul><ul>\n"
		 + " <li>m2.html<ul>\n"
		 + "  <li>section.htmlDepend</li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>b1.xml<ul>\n"
		 + "  <li>section.a.blade1.xmlDepend</li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>index.html<ul>\n"
		 + "  <li>section.app.main2<ul>\n"
		 + "   <li>lib2</li>\n"
		 + "   <li>jquery</li>\n"
		 + "   <li>knockout</li>\n"
		 + "   <li>lib1</li>\n"
		 + "   <li>section.app.main1</li>\n"
		 + "   <li>section.a.app.bladeset1<ul>\n"
		 + "    <li>section.a.app.bladeset2<ul>\n"
		 + "     <li>section.a.blade1.app.blade1<ul>\n"
		 + "      <li>section.a.blade1.app.blade2</li>\n"
		 + "     </ul></li>\n"
		 + "    </ul></li>\n"
		 + "   </ul></li>\n"
		 + "  </ul></li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>c1.htm</li>\n"
		 + "</ul><ul>\n"
		 + " <li>b2.html<ul>\n"
		 + "  <li>section.a.blade1.htmlDepend</li>\n"
		 + " </ul></li>\n"
		 + "</ul><ul>\n"
		 + " <li>b2.xml</li>\n"
		 + "</ul><ul>\n"
		 + " <li>bs1.xml</li>\n"
		 + "</ul>";
			
		assertEquals(expected, result);
	}

}
