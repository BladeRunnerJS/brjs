package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class JsStyleUtilityTest extends SpecTest {
	private Bladeset bladeset;
	private Blade blade;
	private Workbench bladeWorkbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			bladeset = brjs.app("app1").bladeset("bs");
			blade = bladeset.blade("b1");
			bladeWorkbench = blade.workbench();
	}
	
	@Test
	public void styleNameIsNodeJsByDefault() throws Exception {
		then(bladeset).jsStyleIs("node.js");
	}
	
	@Test
	public void styleNameCanBeChanged() throws Exception {
		given(bladeset).containsFileWithContents(".js-style", "namespaced-js");
		then(bladeset).jsStyleIs("namespaced-js");
	}
	
	@Test
	public void styleNamesAreTrimmed() throws Exception {
		given(bladeset).containsFileWithContents(".js-style", "namespaced-js\n");
		then(bladeset).jsStyleIs("namespaced-js");
	}
	
	@Test
	public void setStyleNamesAreAvailableInSubDirectories() throws Exception {
		given(bladeset).containsFileWithContents(".js-style", "namespaced-js");
		then(blade).jsStyleIs("namespaced-js")
			.and(bladeWorkbench).jsStyleIs("namespaced-js");
	}
	
	@Test
	public void styleNamesCanBeChangedAtMultipleLevels() throws Exception {
		given(blade).containsFileWithContents(".js-style", "namespaced-js")
			.and(bladeWorkbench).containsFileWithContents(".js-style", "node.js");
		then(bladeset).jsStyleIs("node.js")
			.and(blade).jsStyleIs("namespaced-js")
			.and(bladeWorkbench).jsStyleIs("node.js");
	}
}
