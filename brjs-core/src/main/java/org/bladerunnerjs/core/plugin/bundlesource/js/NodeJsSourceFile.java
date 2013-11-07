package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AliasDefinition;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class NodeJsSourceFile implements SourceFile {
	private final File assetFile;
	@SuppressWarnings("unused") private List<SourceLocation> sourceLocations;
	private List<String> dependentRequirePaths;
	private List<AliasDefinition> aliases;
	private BundlableNode bundlableNode; // TODO: where do I get this from?
	@SuppressWarnings("unused") private FileModifiedChecker fileModifiedChecker;
	
	public NodeJsSourceFile(SourceLocation sourceLocation, File file) {
		assetFile = file;
		fileModifiedChecker = new FileModifiedChecker(assetFile);
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		List<SourceFile> dependentSourceFiles = new ArrayList<>();
		
		try {
			for(String dependentRequirePath : dependentRequirePaths) {
				dependentSourceFiles.add(bundlableNode.getSourceFile(dependentRequirePath));
			}
		}
		catch (AmbiguousRequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return dependentSourceFiles;
	}
	
	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		return aliases;
	}
	
	@Override
	public Reader getReader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getRequirePath() {
		return assetFile.getPath().replaceAll("\\.js^", "");
	}
	
	@Override
	public Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException {
		return new ArrayList<>();
	}
}
