package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.BRJS;

public class AliasData {
	private MemoizedValue<AliasesData> aliasesData;
	
	public AliasData(BRJS brjs, File aliasesFile) {
		aliasesData = new MemoizedValue<>("AliasData.aliasesData", brjs, aliasesFile);
	}
	
	public List<AliasOverride> getAliasOverrides() {
		return getAliasesData().aliasOverrides;
	}

	public void setAliasOverrides(List<AliasOverride> aliasOverrides) {
		getAliasesData().aliasOverrides = aliasOverrides;
	}
	
	public List<String> getGroupNames() {
		return getAliasesData().groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		getAliasesData().groupNames = groupNames;
	}
	
	public String getScenario() {
		return getAliasesData().scenario;
	}
	
	public void setScenario(String scenario) {
		getAliasesData().scenario = scenario;
	}
	
	public void write() {
		// TODO: AliasesWriter.write()
	}
	
	private AliasesData getAliasesData() {
		return aliasesData.value(() -> {
			// TODO: return AliasesReader.read();
			return null;
		});
	}
}
