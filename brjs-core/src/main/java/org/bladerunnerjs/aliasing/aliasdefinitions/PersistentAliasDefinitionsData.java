package org.bladerunnerjs.aliasing.aliasdefinitions;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;

public class PersistentAliasDefinitionsData {
	private final BRJS brjs;
	private final AssetLocation assetLocation;
	private final MemoizedFile aliasesFile;
	private final MemoizedValue<AliasDefinitionsData> aliasDefinitionsData;
	
	public PersistentAliasDefinitionsData(AssetLocation assetLocation, MemoizedFile aliasesFile) {
		this.brjs = assetLocation.root();
		this.assetLocation = assetLocation;
		this.aliasesFile = aliasesFile;
		aliasDefinitionsData = new MemoizedValue<>("PersistentAliasDefinitionsData.aliasDefinitionsData", brjs, aliasesFile, assetLocation.assetContainer().dir(),
			assetLocation.root().file("conf/brjs.conf"), assetLocation.assetContainer().app().file("app.conf"));
	}
	
	public AliasDefinitionsData getData() throws ContentFileProcessingException {
		return aliasDefinitionsData.value(() -> {
			return AliasDefinitionsReader.read(aliasesFile, assetLocation, getCharacterEncoding());
		});
	}
	
	public void writeData() throws ContentFileProcessingException {
		try {
			AliasDefinitionsWriter.write(brjs, getData(), aliasesFile, getCharacterEncoding());
		}
		catch(Exception e) {
			throw new ContentFileProcessingException(e, aliasesFile);
		}
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
