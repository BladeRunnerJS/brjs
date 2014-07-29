package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class AliasData {
	private final BRJS brjs;
	private final File aliasesFile;
	private final MemoizedValue<AliasesData> aliasesData;
	
	public AliasData(BRJS brjs, File aliasesFile) {
		this.brjs = brjs;
		this.aliasesFile = aliasesFile;
		aliasesData = new MemoizedValue<>("AliasData.aliasesData", brjs, aliasesFile, brjs.file("conf/brjs.conf"));
	}
	
	public List<AliasOverride> getAliasOverrides() throws ContentFileProcessingException {
		return getAliasesData().aliasOverrides;
	}

	public void setAliasOverrides(List<AliasOverride> aliasOverrides) throws ContentFileProcessingException {
		getAliasesData().aliasOverrides = aliasOverrides;
	}
	
	public List<String> getGroupNames() throws ContentFileProcessingException {
		return getAliasesData().groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) throws ContentFileProcessingException {
		getAliasesData().groupNames = groupNames;
	}
	
	public String getScenario() throws ContentFileProcessingException {
		return getAliasesData().scenario;
	}
	
	public void setScenario(String scenario) throws ContentFileProcessingException {
		getAliasesData().scenario = scenario;
	}
	
	public void write() throws IOException {
		try {
			AliasesWriter.write(getAliasesData(), aliasesFile, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private AliasesData getAliasesData() throws ContentFileProcessingException {
		return aliasesData.value(() -> {
			return AliasesReader.read(aliasesFile, getCharacterEncoding());
		});
	}
	
	private String getCharacterEncoding() {
		try {
			return brjs.bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
}
