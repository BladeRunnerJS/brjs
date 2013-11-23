package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.FullyQualifiedLinkedAssetFile;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.aliasing.AliasName;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class CaplinJsSourceFile implements SourceFile {
	private LinkedAssetFile assetFile;
	private AssetLocation assetLocation;
	private String requirePath;
	
	@Override
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File file)
	{
		this.assetLocation = assetLocation;
		this.requirePath = assetLocation.getAssetContainer().file("src").toURI().relativize(file.toURI()).getPath().replaceAll("\\.js$", "");
		assetFile = new FullyQualifiedLinkedAssetFile();
		assetFile.initializeUnderlyingObjects(assetLocation, file);
	}
	
	@Override
 	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		List<SourceFile> dependentSourceFiles = assetFile.getDependentSourceFiles();
		dependentSourceFiles.removeAll(getOrderDependentSourceFiles());
		dependentSourceFiles.remove(this);
		
		return dependentSourceFiles;
	}
	
	@Override
	public List<AliasName> getAliasNames() throws ModelOperationException {
		return assetFile.getAliasNames();
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return assetFile.getReader();
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException {
		// TODO: scan the source file for caplin.extend(), caplin.implement(), br.extend() & br.implement()
		return new ArrayList<>();
	}
	
	@Override
	public File getUnderlyingFile() {
		return assetFile.getUnderlyingFile();
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
	
	public String getClassName() {
		return getRequirePath().replaceAll("/", ".");
	}
}
