package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.netbeans.spi.project.support.ant.PathMatcher;

import com.google.common.base.Joiner;

public class StandardFileSet<AF extends AssetFile> implements FileSet<AF> {
	private List<FileSetObserver> observers = new ArrayList<>();
	private SourceLocation sourceLocation;
	private AssetFileFactory<AF> fileSetFactory;
	private PathMatcher pathMatcher;
	
	public static String[] paths(String... paths) {
		return paths;
	}
	
	public StandardFileSet(SourceLocation sourceLocation, String[] includePaths, String[] excludePaths, AssetFileFactory<AF> fileSetFactory) {
		this.sourceLocation = sourceLocation;
		this.fileSetFactory = fileSetFactory;
		
		String includePathsStr = (includePaths == null) ? null : Joiner.on(", ").join(includePaths);
		String excludePathsStr = (excludePaths == null) ? null : Joiner.on(", ").join(excludePaths);
		pathMatcher = new PathMatcher(includePathsStr, excludePathsStr, sourceLocation.dir());
	}
	
	public List<AF> getFiles() {
		List<AF> files = new ArrayList<>();
		File baseDir = sourceLocation.dir();
		
		for(File file : FileUtils.listFiles(baseDir, null, true)) {
			String relativePath = baseDir.toURI().relativize(file.toURI()).getPath();
			
			if(pathMatcher.matches(relativePath, true)) {
				files.add(fileSetFactory.createFile(sourceLocation, file.getPath()));
			}
		}
		
		return files;
	}
	
	public void addObserver(FileSetObserver fileSetObserver) {
		observers.add(fileSetObserver);
	}
}
