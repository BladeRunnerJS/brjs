package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;


public class FullyQualifiedLinkedAssetFile implements LinkedAssetFile {
	private List<SourceLocation> sourceLocations;
	private final AssetFile assetFile;
	private boolean recalculateDependencies = true;
	private List<SourceFile> dependentSourceFiles;
	private List<AliasDefinition> aliases;
	private boolean containsClassReferences;
	
	public FullyQualifiedLinkedAssetFile(File file) {
		assetFile = new WatchingAssetFile(file);
		assetFile.addObserver(new Observer());
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
	public void onSourceLocationsUpdated(List<SourceLocation> sourceLocations) {
		recalculateDependencies = true;
		this.sourceLocations = sourceLocations;
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		if(recalculateDependencies) {
			recalculateDependencies();
		}
		
		return dependentSourceFiles;
	}

	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		if(recalculateDependencies) {
			recalculateDependencies();
		}
		
		return aliases;
	}
	
	@Override
	public boolean containsClassReferences() throws ModelOperationException {
		if(recalculateDependencies) {
			recalculateDependencies();
		}
		
		return containsClassReferences;
	}
	
	private void recalculateDependencies() throws ModelOperationException {
		dependentSourceFiles = new ArrayList<>();
		aliases = new ArrayList<>();
		containsClassReferences = false;
		Trie trie = createTrie();
		
		try {
			try(Reader reader = assetFile.getReader()) {
				for(Object match : trie.getMatches(reader)) {
					if(match instanceof SourceFile) {
						dependentSourceFiles.add((SourceFile) match);
					}
					else if(match instanceof ClassSourceFile) {
						containsClassReferences = true;
						dependentSourceFiles.add(((ClassSourceFile) match).getSourceFile());
					}
					else {
						aliases.add((AliasDefinition) match);
					}
				}
			}
		}
		catch(IOException e) {
			throw new ModelOperationException(e);
		}
		
		recalculateDependencies = false;
	}
	
	private Trie createTrie() throws ModelOperationException {
		Trie trie = new Trie();
		
		for(SourceLocation sourceLocation : sourceLocations) {
			for(SourceFile sourceFile : sourceLocation.sourceFiles()) {
				ClassSourceFile classSourceFile = new ClassSourceFile(sourceFile);
				
				trie.add(sourceFile.getRequirePath(), sourceFile);
				trie.add(classSourceFile.getClassName(), classSourceFile);
				
				for(AliasDefinition aliasDefinition : sourceFile.getAliases()) {
					trie.add(aliasDefinition.getName(), aliasDefinition);
				}
			}
		}
		
		return trie;
	}
	
	private class Observer implements AssetFileObserver {
		@Override
		public void onAssetFileModified() {
			recalculateDependencies = true;
		}
	}
}
