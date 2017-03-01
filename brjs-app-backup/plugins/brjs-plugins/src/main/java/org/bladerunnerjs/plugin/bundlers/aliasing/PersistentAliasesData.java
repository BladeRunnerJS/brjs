package org.bladerunnerjs.plugin.bundlers.aliasing;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.engine.RootNode;

public class PersistentAliasesData {
	private final BRJS brjs;
	private final MemoizedFile aliasesFile;
	private final MemoizedValue<AliasesData> aliasesData;
	
	public PersistentAliasesData(RootNode rootNode, MemoizedFile aliasesFile) {
		this.brjs = (BRJS) rootNode;
		this.aliasesFile = aliasesFile;
		aliasesData = new MemoizedValue<>("PersistentAliasesData.aliasesData", rootNode, aliasesFile);
	}
	
	public AliasesData getData() throws ContentFileProcessingException {
		return aliasesData.value(() -> {
			return AliasesReader.read(brjs, aliasesFile, getCharacterEncoding());
		});
	}
	
	public void writeData() throws ContentFileProcessingException {
		try {
			AliasesWriter.write(brjs, getData(), aliasesFile, getCharacterEncoding());
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
