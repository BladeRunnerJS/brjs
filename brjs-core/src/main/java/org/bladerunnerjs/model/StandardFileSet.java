package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.netbeans.spi.project.support.ant.PathMatcher;

import com.google.common.base.Joiner;

public class StandardFileSet<AF extends AssetFile> {
	private AssetContainer assetContainer;
	private AssetFileFactory<AF> fileSetFactory;
	private PathMatcher pathMatcher;
	
	public static String[] paths(String... paths) {
		return paths;
	}
	
	public StandardFileSet(AssetContainer assetContainer, String[] includePaths, String[] excludePaths, AssetFileFactory<AF> fileSetFactory) {
		this.assetContainer = assetContainer;
		this.fileSetFactory = fileSetFactory;
		
		String includePathsStr = (includePaths == null) ? null : Joiner.on(", ").join(includePaths);
		String excludePathsStr = (excludePaths == null) ? null : Joiner.on(", ").join(excludePaths);
		pathMatcher = new PathMatcher(includePathsStr, excludePathsStr, assetContainer.dir());
	}
	
	@SuppressWarnings("unchecked")
	public List<AF> getFiles() {
		List<AF> files = new ArrayList<>();
		File baseDir = assetContainer.dir();
		
		for(File file : FileUtils.listFiles(baseDir, null, true)) {
			String relativePath = baseDir.toURI().relativize(file.toURI()).getPath();
			
			if(pathMatcher.matches(relativePath, true)) {
				files.add((AF) assetContainer.root().getAssetFile(fileSetFactory, assetContainer, file));
			}
		}
		
		return files;
	}
	
}
