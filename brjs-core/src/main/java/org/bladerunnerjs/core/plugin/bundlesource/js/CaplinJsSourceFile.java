package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.AliasDefinition;
import org.bladerunnerjs.model.AssetFileObserver;
import org.bladerunnerjs.model.FullyQualifiedLinkedAssetFile;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class CaplinJsSourceFile implements SourceFile {
	private LinkedAssetFile assetFile;
	private SourceLocation sourceLocation;
	private String filePath;
	
	public CaplinJsSourceFile(SourceLocation sourceLocation, String filePath) {
		this.sourceLocation = sourceLocation;
		this.filePath = filePath;
		assetFile = new FullyQualifiedLinkedAssetFile(sourceLocation, filePath);
	}
	
	@Override
	public void onSourceLocationsUpdated(List<SourceLocation> sourceLocations) {
		assetFile.onSourceLocationsUpdated(sourceLocations);
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		// TODO: remove order dependent source files from the list we return
		return assetFile.getDependentSourceFiles();
	}
	
	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		return assetFile.getAliases();
	}
	
	@Override
	public Reader getReader() {
		return assetFile.getReader();
	}
	
	@Override
	public void addObserver(AssetFileObserver observer) {
		assetFile.addObserver(observer);
	}
	
	@Override
	public String getRequirePath() {
		return getClassName().replaceAll("\\.", "/");
	}
	
	@Override
	public Resources getResources() {
		return sourceLocation.getResources(filePath);
	}
	
	@Override
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException {
		// TODO: scan the source file for caplin.extend(), caplin.implement(), br.extend() & br.implement()
		return null;
	}
	
	public String getClassName() {
		// TODO: implement this once we receive a SourceLocation in the constructor, instead of a file
		return null;
	}
}
