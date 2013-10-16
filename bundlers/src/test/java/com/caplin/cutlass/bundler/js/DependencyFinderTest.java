package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.js.Match;

public class DependencyFinderTest
{

	List<String> classes = Arrays.asList("a.test.class", "a.test.class2", "a.test.class.again", "another.class");
	private DependencyFinder finder;
	private ClassesTrie trie;

	@Before
	public void setup()
	{
		trie = new ClassesTrie();
		for (String classname : classes)
		{
			trie.addClass(classname);
		}
		finder = new DependencyFinder(trie, "");
	}

	@Test
	public void testFindingDependencyInString() throws Exception
	{
		String input = "there is a class (a.test.class) somewhere in here.";
		String classname = getClassnameFromInput(input);
		assertEquals("a.test.class", classname);
	}

	@Test
	public void testFindingDependencyInMultilineString() throws Exception
	{
		String input = "there is a \n" + "class (a.test.class) somewhere \n" + "in here.";
		String classname = getClassnameFromInput(input);
		assertEquals("a.test.class", classname);
	}

	@Test
	public void testDependencyStringsCannotSpreadMultipeLiness() throws Exception
	{
		String input = "there is NOT a \n" + "class (a.te\n" + "st.class) somewhere in here.";
		String classname = getClassnameFromInput(input);
		assertEquals("", classname);
	}

	@Test
	public void testClassnameMustMatchExactly() throws Exception
	{
		String input = "class a-test_class) isnt really a class";
		String classname = getClassnameFromInput(input);
		assertEquals("", classname);
	}
	
	@Test
	public void weCanFindClassesThatStartWithTheNameOfAnotherClass() throws Exception
	{
		String input = "there is a class (a.test.class2) somewhere in here.";
		String classname = getClassnameFromInput(input);
		assertEquals("a.test.class2", classname);
	}
	
	@Test
	public void weCanFindClassesWhosePackageNameLooksLikeAClass() throws Exception
	{
		String input = "there is a class (a.test.class.again) somewhere in here.";
		String classname = getClassnameFromInput(input);
		assertEquals("a.test.class.again", classname);
	}
	
	@Test(expected=IOException.class)
	public void exceptionIsThrownForMacLineEndings() throws Exception
	{
		String input = "there is a bad line\rending in here";
		getClassnameFromInput(input);
	}
	
	@Test
	public void testClassLookupIsGreedy() throws Exception
	{
		classes = Arrays.asList("match.the.longer.class", "match.the.longer.classsssss");
		trie = new ClassesTrie();
		for (String classname : classes)
		{
			trie.addClass(classname);
		}
		finder = new DependencyFinder(trie, "");
		
		String input = "this should match.the.longer.classsssss";
		String classname = getClassnameFromInput(input);
		assertEquals("match.the.longer.classsssss", classname);
	}
	
	@Test
	public void testClassLookupBundlesClassesThatAreReferencedWithAVariableDefinitionAtTheEnd() throws Exception
	{
		classes = Arrays.asList("match.the.longer.class", "match.the.longer.class.xyz");
		trie = new ClassesTrie();
		for (String classname : classes)
		{
			trie.addClass(classname);
		}
		finder = new DependencyFinder(trie, "");
		
		//x is a variable on "match.the.longer.class" but it can also part match the longer class above.
		String input = "this should match.the.longer.class.x;";
		String classname = getClassnameFromInput(input);
		assertEquals("match.the.longer.class", classname);
	}
	
	private String getClassnameFromInput(String input) throws IOException
	{
		input += '\n';
		String classname = "";
		for (char c : input.toCharArray())
		{
			Match matched = finder.next(c);
			if (matched != null)
			{
				classname = matched.getDependencyName();
			}
		}
		return classname;
	}
}
