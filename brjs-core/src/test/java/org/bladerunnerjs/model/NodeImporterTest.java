package org.bladerunnerjs.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class NodeImporterTest {
	
	@Test
	public void packageIsReplacedCorrectly() {
		assertEquals("x.y.c", NodeImporter.findAndReplaceInText("a.b.c", "a/b", "x/y"));
	}
	
	@Test
	public void requirePathIsReplacedCorrectly() {
		assertEquals("'x/y/c'", NodeImporter.findAndReplaceInText("'a/b/c'", "a/b", "x/y"));
	}

	@Test
	public void packageWithoutSuffixIsReplacedCorrectly() {
		assertEquals("x.y", NodeImporter.findAndReplaceInText("a.b", "a/b", "x/y"));
	}
	
	@Test
	public void requirePathWithoutSuffixIsReplacedCorrectly() {
		assertEquals("'x/y'", NodeImporter.findAndReplaceInText("'a/b'", "a/b", "x/y"));
	}

	@Test
	public void packageInQuotesIsReplacedCorrectly() {
		assertEquals("'x.y.c'", NodeImporter.findAndReplaceInText("'a.b.c'", "a/b", "x/y"));
	}
	
	@Test
	public void requirePathOneWordOnlyIsReplacedCorrectly() {
		assertEquals("'app4/forbob1/bob/BobViewModel'", NodeImporter.findAndReplaceInText("'app1/bob/BobViewModel'", "app1", "app4/forbob1"));
	}
	
	@Test
	public void packageOneWordOnlyIsReplacedCorrectly() {
		assertEquals("'app4.forbob1.bob.BobViewModel'", NodeImporter.findAndReplaceInText("'app1.bob.BobViewModel'", "app1", "app4/forbob1"));
	}
	
	@Test
	public void ifMatcherMatchesPartialWordNoReplacementIsMade() {
		assertEquals("'xmlutility.bob.BobViewModel'", NodeImporter.findAndReplaceInText("'xmlutility.bob.BobViewModel'", "utility", "x/y"));
	}
}
