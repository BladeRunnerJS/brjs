package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;

public class FileSet<AF extends AssetFile> {
	private final Constructor<AF> classConstructor = null; // TODO: remove the = null bit once fully implemented
	private final SourceLocation sourceLocation;
	
	public FileSet(Class<AF> assetFileClass, SourceLocation sourceLocation, String[] includePaths, String[] excludePaths) {
		this.sourceLocation = sourceLocation;
		
//		try {
//			classConstructor = assetFileClass.getConstructor(String.class, File.class, Resources.class, SourceLocation.class);
//		}
//		catch (NoSuchMethodException | SecurityException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public List<AF> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addObserver(FileSetObserver fileSetObserver) {
		// TODO Auto-generated method stub
	}
	
	@SuppressWarnings("unused")
	private AF createSourceFile(String sourcePath) {
		try {
			File sourceFile = ((Node) sourceLocation).file(sourcePath);
			String requirePath = sourceLocation.getRequirePrefix() + sourcePath.replaceAll("\\.js$", "");
			Resources resources = sourceLocation.getResources(sourcePath);
			
			return classConstructor.newInstance(requirePath, sourceFile, resources, sourceLocation);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
