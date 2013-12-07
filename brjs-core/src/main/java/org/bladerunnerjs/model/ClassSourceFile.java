package org.bladerunnerjs.model;

public class ClassSourceFile
{
	private final SourceModule sourceModule;
	
	public ClassSourceFile(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}
	
	public SourceModule getSourceModule() {
		return sourceModule;
	}
	
	public String getClassName() {
		return sourceModule.getRequirePath().replaceAll("/", ".");
	}
}
