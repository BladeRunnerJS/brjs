package org.bladerunnerjs.plugin.bundlers.aliasing;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.AssetContainer;

public class PersistentAliasDefinitionsData {
	private final BRJS brjs;
	private final AssetContainer assetContainer;
	private final MemoizedFile aliasesFile;
	private final MemoizedValue<AliasDefinitionsData> aliasDefinitionsData;
	
	public PersistentAliasDefinitionsData(AssetContainer assetContainer, MemoizedFile aliasesFile) {
		this.brjs = assetContainer.root();
		this.assetContainer = assetContainer;
		this.aliasesFile = aliasesFile;
		aliasDefinitionsData = new MemoizedValue<>("PersistentAliasDefinitionsData.aliasDefinitionsData", brjs, aliasesFile, assetContainer.dir(),
				brjs.file("conf/brjs.conf"), assetContainer.app().file("app.conf"));
	}
	
	public AliasDefinitionsData getData() throws ContentFileProcessingException {
		return aliasDefinitionsData.value(() -> {
			return AliasDefinitionsReader.read(aliasesFile, assetContainer, getCharacterEncoding());
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
