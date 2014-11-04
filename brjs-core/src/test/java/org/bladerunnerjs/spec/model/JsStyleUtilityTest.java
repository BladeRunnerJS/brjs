package org.bladerunnerjs.spec.model;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class JsStyleUtilityTest extends SpecTest {
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench bladeWorkbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			bladeset = brjs.app("app1").bladeset("bs");
			blade = bladeset.blade("b1");
			bladeWorkbench = blade.workbench();
	}
	
	@Test
	public void styleNameIsCommonJsByDefault() throws Exception {
		then(bladeset).jsStyleIs("common-js");
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
			.and(bladeWorkbench).containsFileWithContents(".js-style", "common-js");
		then(bladeset).jsStyleIs("common-js")
			.and(blade).jsStyleIs("namespaced-js")
			.and(bladeWorkbench).jsStyleIs("common-js");
	}
	
	@Test
	public void styleCanBeSetAtRoot() throws Exception {
		given(brjs).containsFileWithContents(".js-style", "namespaced-js");
		then(brjs).jsStyleIs("namespaced-js");
	}
	
	@Test
	public void jsStyleUtilityDoesntRecurseOutsideOfBrjsRoot() throws Exception {
		given(brjs).hasNotYetBeenCreated();
		FileUtils.cleanDirectory(testSdkDirectory);
		File oldTestSdkDirectory = testSdkDirectory;
		testSdkDirectory = new File(oldTestSdkDirectory, "subdir");
		new File(testSdkDirectory, "sdk").mkdirs();
		
		given(brjs).hasBeenCreated()
			.and(oldTestSdkDirectory).containsFileWithContents(".js-style", "namespaced-js");
		then(brjs).jsStyleIs("common-js");
		
		FileUtils.deleteQuietly(oldTestSdkDirectory);
	}
	
}
