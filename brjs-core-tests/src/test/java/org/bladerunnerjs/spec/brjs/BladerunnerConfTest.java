package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladerunnerConfTest extends SpecTest {
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
	}
	
	@Test
	public void bladerunnerConfWillHaveSensibleDefaultsIfItDoesntAlreadyExist() throws Exception {
		when(brjs).bladerunnerConf().write();
		then(brjs).fileHasContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\nignoredPaths: .svn, .git\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void readingBladerunnerConfWithMissingLoginReamlUsesTheDefault() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: 7070");
		then(brjs.bladerunnerConf().getLoginRealm()).textEquals("BladeRunnerLoginRealm");
	}
	
	@Test
	public void bladerunnerConfThatAlreadyExistsCanBeReadAndModified() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf().setJettyPort(8888).setDefaultFileCharacterEncoding("ISO-8859-1").write();
		then(brjs).fileHasContents("conf/brjs.conf", "defaultFileCharacterEncoding: ISO-8859-1\nignoredPaths: .svn, .git\njettyPort: 8888\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void readingAnEmptyBladerunnerConfWillUseTheDefaultValues() throws Exception {
		given(brjs).containsEmptyFile("conf/brjs.conf");
		then(brjs.bladerunnerConf().getLoginRealm()).textEquals("BladeRunnerLoginRealm")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void malformedBladerunnerConfCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "blah");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("Line 0, column 4"));
	}
	
	@Test
	public void jettyPortValuesLessThanOneCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: -1\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("jettyPort' must be greater than or equal to 1"));
	}
	
	@Test
	public void jettyPortValuesGreaterThan65KCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: 65536\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("jettyPort' must be less than or equal to 65535"));
	}
	
	@Test
	public void jettyPortValuesOfTheWrongTypeCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: abcd\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("Unable to convert value to required type \"int\""));
	}
	
	@Test
	public void invalidEncodingValuesWillCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: ZZZ-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), "defaultFileCharacterEncoding", "ZZZ-8",
			unquoted("not a valid character encoding"));
	}
	
	@Test
	public void readingAnBladerunnerConfFileWithEmptyValuesWillCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: \njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("'defaultFileCharacterEncoding' may not be empty"));
	}
	
	@Test
	public void invalidPropertyInBladerunnerConfCausesAnExcpetion() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "sillyProperty: UTF-8\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/brjs.conf").getPath(), unquoted("Unable to find property 'sillyProperty'"));
	}
	
	@Test
	public void theModelUpdatesWhenTheUnderlyingFileIsChanged() throws Exception {
		given(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm")
			.and(brjs.bladerunnerConf()).defaultFileCharacterEncodingIs("UTF-8");
		when(brjs).containsFileWithContents("conf/brjs.conf", "defaultFileCharacterEncoding: ISO-8859-1\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		then(brjs.bladerunnerConf().getDefaultFileCharacterEncoding().toString()).textEquals("ISO-8859-1");
	}
	
}
