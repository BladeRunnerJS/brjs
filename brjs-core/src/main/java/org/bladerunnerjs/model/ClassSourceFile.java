package org.bladerunnerjs.model;

public class ClassSourceFile
{
	private final SourceFile sourceFile;
	
	public ClassSourceFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public SourceFile getSourceFile() {
		return sourceFile;
	}
	
	public String getClassName() {
		return sourceFile.getRequirePath().replaceAll("/", ".");
	}
}
