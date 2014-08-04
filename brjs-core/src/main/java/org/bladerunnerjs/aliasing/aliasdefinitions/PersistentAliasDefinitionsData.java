package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.File;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class PersistentAliasDefinitionsData {
	private final BRJS brjs;
	private final AssetLocation assetLocation;
	private final File aliasesFile;
	private final MemoizedValue<AliasDefinitionsData> aliasDefinitionsData;
	private AliasDefinitionsData prevFileAliasDefinitionsData;
	private AliasDefinitionsData liveAliasDefinitionsData;
	
	public PersistentAliasDefinitionsData(AssetLocation assetLocation, File aliasesFile) {
		this.brjs = assetLocation.root();
		this.assetLocation = assetLocation;
		this.aliasesFile = aliasesFile;
		aliasDefinitionsData = new MemoizedValue<>("PersistentAliasDefinitionsData.aliasDefinitionsData", brjs, aliasesFile, assetLocation.assetContainer().dir(),
			assetLocation.root().file("conf/brjs.conf"), assetLocation.assetContainer().app().file("app.conf"));
	}
	
	public AliasDefinitionsData getData() throws ContentFileProcessingException {
		return aliasDefinitionsData.value(() -> {
			AliasDefinitionsData fileAliasDefinitionsData = AliasDefinitionsReader.read(aliasesFile, assetLocation, getCharacterEncoding());
			
			if((prevFileAliasDefinitionsData == null) || !prevFileAliasDefinitionsData.equals(fileAliasDefinitionsData)) {
				prevFileAliasDefinitionsData = fileAliasDefinitionsData;
				liveAliasDefinitionsData = new AliasDefinitionsData(fileAliasDefinitionsData);
			}
			
			return liveAliasDefinitionsData;
		});
	}
	
	public void writeData() throws ContentFileProcessingException {
		try {
			AliasDefinitionsWriter.write(getData(), aliasesFile, getCharacterEncoding());
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
