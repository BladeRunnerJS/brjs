package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.yaml.YamlTestRunnerConf;

public class TestRunnerConf extends ConfFile<YamlTestRunnerConf> {
	public TestRunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlTestRunnerConf.class, brjs.file("conf/test-runner.conf"));
	}
	
	public String getDefaultBrowser() throws ConfigException {
		return getConf().defaultBrowser;
	}
	
	public void setDefaultBrowser(String defaultBrowser) throws ConfigException {
		getConf().defaultBrowser = defaultBrowser;
		getConf().verify();
	}
}
