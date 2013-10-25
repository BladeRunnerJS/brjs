package com.caplin.cutlass.command.analyse;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.command.analyse.DependencyAnalyserCommand;
import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class AnalyseCommandTest 
{
	BRJS brjs;
	DependencyAnalyserCommand analyseCommand;
	
	private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	
	@Before
	public void setUp()
	{
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/AnalyseApplicationCommandTest/structure-tests/"), new PrintStream(byteStream));
		BRJSAccessor.initialize(brjs);
		analyseCommand = new DependencyAnalyserCommand(brjs);
	}
	
	@Test
	public void testAppDependencyEmitString() throws Exception
	{
		analyseCommand.doCommand(new String[] {"app1"});
		
		String expected = "m2.html\n"
				 + ".section.htmlDepend\n"
				 + "b1.xml\n"
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
				 + "m1.html\n"
				 +  "m2.xml\n"
				 + "b1.html\n"
				 + "bs1.html\n"
				 + "b2.html\n"
				 + ".section.a.blade1.htmlDepend\n"
				 +  "bs2.xml\n"
				 +  ".section.a.xmlDepend\n"
				 + "bs2.html\n"
				 +  "m1.xml\n"
				 +  ".section.xmlDepend\n"
				 +  "b2.xml\n"
				 +  "bs1.xml\n";	
		
		assertTrue(byteStream.toString().contains(expected));
	}
	
	@Test
	public void testAppDependencyEmitJson() throws Exception
	{
		analyseCommand.doCommand(new String[] {"app1", "default", "json" });
		
		String expected = "{ name : 'm2.html', children : [{ name : 'section.htmlDepend'} ]}, "
				+ "{ name : 'b1.xml', children : [{ name : 'section.a.blade1.xmlDepend'} ]}, "
				+ "{ name : 'index.html', children : [{ name : 'section.app.main2', children : [{ name : 'lib2'}, { name : 'jquery'}, { name : 'knockout'}, { name : 'lib1'}, { name : 'section.app.main1'}, { name : 'section.a.app.bladeset1', children : [{ name : 'section.a.app.bladeset2', children : [{ name : 'section.a.blade1.app.blade1', children : [{ name : 'section.a.blade1.app.blade2'} ]} ]} ]} ]} ]}, "
				+ "{ name : 'm1.html'}, "
				+ "{ name : 'm2.xml'}, "
				+ "{ name : 'b1.html'}, { name : 'bs1.html'}, { name : 'b2.html', children : [{ name : 'section.a.blade1.htmlDepend'} ]}, "
				+ "{ name : 'bs2.xml', children : [{ name : 'section.a.xmlDepend'} ]}, "
				+ "{ name : 'bs2.html'}, "
				+ "{ name : 'm1.xml', children : [{ name : 'section.xmlDepend'} ]}, "
				+ "{ name : 'b2.xml'}, "
				+ "{ name : 'bs1.xml'}, "; 
		
		assertTrue(byteStream.toString().contains(expected));
	}

}
