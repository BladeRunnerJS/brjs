package org.bladerunnerjs.model;

public class ClassSourceFile
{
	private final SourceModule sourceFile;
	
	public ClassSourceFile(SourceModule sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	public SourceModule getSourceFile() {
		return sourceFile;
	}
	
	public String getClassName() {
		return sourceFile.getRequirePath().replaceAll("/", ".");
	}
}
