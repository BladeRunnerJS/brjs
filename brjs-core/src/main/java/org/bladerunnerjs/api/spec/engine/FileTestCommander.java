package org.bladerunnerjs.api.spec.engine;

import java.io.File;

public class FileTestCommander {

	private FileTestBuilder fileTestBuilder;
	private CommanderChainer commanderChainer;
	
	public FileTestCommander(SpecTest specTest, File file) {
		fileTestBuilder = new FileTestBuilder(specTest, file);
		commanderChainer = new CommanderChainer(specTest);
	}
	
	public CommanderChainer containsFolder(String filePath) throws Exception {
		fileTestBuilder.containsFolder(filePath);
		return commanderChainer;
	}

	public CommanderChainer containsFile(String filePath) throws Exception {
		fileTestBuilder.containsFile(filePath);
		return commanderChainer;
	}

	public CommanderChainer containsFileWithContents(String filePath, String contents) throws Exception {
		fileTestBuilder.containsFileWithContents(filePath, contents);
		return commanderChainer;
	}

}
