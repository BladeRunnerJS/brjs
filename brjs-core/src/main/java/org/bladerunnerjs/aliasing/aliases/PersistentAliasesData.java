package org.bladerunnerjs.aliasing.aliases;

import java.io.File;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class PersistentAliasesData {
	private final BRJS brjs;
	private final File aliasesFile;
	private final MemoizedValue<AliasesData> aliasesData;
	
	public PersistentAliasesData(BRJS brjs, File aliasesFile) {
		this.brjs = brjs;
		this.aliasesFile = aliasesFile;
		aliasesData = new MemoizedValue<>("PersistentAliasesData.aliasesData", brjs, aliasesFile, brjs.file("conf/brjs.conf"));
	}
	
	public AliasesData getData() throws ContentFileProcessingException {
		return aliasesData.value(() -> {
			return AliasesReader.read(aliasesFile, getCharacterEncoding());
		});
	}
	
	public void writeData() throws ContentFileProcessingException {
		try {
			AliasesWriter.write(getData(), aliasesFile, getCharacterEncoding());
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
