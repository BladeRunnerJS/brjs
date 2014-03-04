package org.bladerunnerjs.testing.specutility;

public class SourceModuleDescriptor {
	public String requirePath;
	public String[] filePaths;
	
	public SourceModuleDescriptor(String requirePath, String[] filePaths) {
		this.requirePath = requirePath;
		this.filePaths = filePaths;
	}
	
	public static SourceModuleDescriptor sourceModule(String requirePath, String... filePaths) {
		return new SourceModuleDescriptor(requirePath, filePaths);
	}
}
