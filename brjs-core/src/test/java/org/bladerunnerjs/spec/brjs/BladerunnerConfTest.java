package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladerunnerConfTest extends SpecTest {
	// TODO: add a test that shows the object updates if the conf file is modified
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
	}
	
	@Test
	public void bladerunnerConfWillHaveSensibleDefaultsIfItDoesntAlreadyExist() throws Exception {
		when(brjs).bladerunnerConf().write();
		then(brjs).fileHasContents("conf/bladerunner.conf", "browserCharacterEncoding: UTF-8\ndefaultFileCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void readingBladerunnerConfWithMissingLoginReamlThrowsException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 7070");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("'loginRealm' may not be null"));
	}
	
	@Test
	public void bladerunnerConfThatAlreadyExistsCanBeReadAndModified() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf().setJettyPort(8888).setDefaultFileCharacterEncoding("ISO-8859-1").write();
		then(brjs).fileHasContents("conf/bladerunner.conf", "browserCharacterEncoding: UTF-8\ndefaultFileCharacterEncoding: ISO-8859-1\njettyPort: 8888\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void readingAnEmptyBladerunnerConfFileWillCauseAnException() throws Exception {
		given(brjs).containsEmptyFile("conf/bladerunner.conf");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("is empty"));
	}
	
	@Test
	public void readingAnBladerunnerConfFileWithMissingValuesWillCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("'browserCharacterEncoding' may not be null"));
	}
	
	@Test
	public void malformedBladerunnerConfCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "blah");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("Line 0, column 4"));
	}
	
	@Test
	public void jettyPortValuesLessThanOneCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 0\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("jettyPort' must be greater than or equal to 1"));
	}
	
	@Test
	public void jettyPortValuesGreaterThan65KCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 65536\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("jettyPort' must be less than or equal to 65535"));
	}
	
	@Test
	public void jettyPortValuesOfTheWrongTypeCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: abcd\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("Unable to convert value to required type \"int\""));
	}
	
	@Test
	public void invalidEncodingValuesWillCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: ZZZ-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), "defaultFileCharacterEncoding", "ZZZ-8",
			unquoted("not a valid character encoding"));
	}
	
	@Test
	public void readingAnBladerunnerConfFileWithEmptyValuesWillCauseAnException() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding:\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("'browserCharacterEncoding' may not be empty"));
	}
	
	@Test
	public void invalidPropertyInBladerunnerConfCausesAnExcpetion() throws Exception {
		given(brjs).containsFileWithContents("conf/bladerunner.conf", "sillyProperty: UTF-8\nloginRealm: BladeRunnerLoginRealm");
		when(brjs).bladerunnerConf();
		then(exceptions).verifyException(ConfigException.class, brjs.file("conf/bladerunner.conf").getPath(), unquoted("Unable to find property 'sillyProperty'"));
	}
}
