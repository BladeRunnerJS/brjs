package org.bladerunnerjs.aliasing.aliases;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasOverride;

public class AliasesData {
	public List<AliasOverride> aliasOverrides = new ArrayList<>();
	public List<String> groupNames = new ArrayList<>();
	public String scenario;
	
	public AliasesData() {
		// do nothing
	}
	
	public AliasesData(AliasesData aliasesData) {
		scenario = aliasesData.scenario;
		groupNames.addAll(aliasesData.groupNames);
		aliasOverrides.addAll(aliasesData.aliasOverrides);
	}
	
	public boolean equals(AliasesData aliasesData) {
		return ((scenario == null) ? aliasesData.scenario == null : scenario.equals(aliasesData.scenario)) && groupNames.equals(aliasesData.groupNames) && aliasOverrides.equals(aliasesData.aliasOverrides);
	}
}
