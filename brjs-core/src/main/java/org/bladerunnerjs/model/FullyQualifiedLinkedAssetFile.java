package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.Trie;


public class FullyQualifiedLinkedAssetFile implements LinkedAssetFile {
	private final SourceLocation sourceLocation;
	private List<SourceLocation> sourceLocations;
	private final File assetFile;
	private List<SourceFile> dependentSourceFiles;
	private List<AliasDefinition> aliases;
	private FileModifiedChecker fileModifiedChecker;
	
	public FullyQualifiedLinkedAssetFile(SourceLocation sourceLocation, File file) {
		this.sourceLocation = sourceLocation;	
		assetFile = file;
		fileModifiedChecker = new FileModifiedChecker(assetFile);
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException {
		return new BufferedReader( new FileReader(assetFile) );
	}
	
	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			recalculateDependencies();
		}
		
		return dependentSourceFiles;
	}

	@Override
	public List<AliasDefinition> getAliases() throws ModelOperationException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			recalculateDependencies();
		}
		
		return aliases;
	}
	
	private void recalculateDependencies() throws ModelOperationException {
		dependentSourceFiles = new ArrayList<>();
		aliases = new ArrayList<>();
		Trie<Object> trie = createTrie();
		
		try {
			try(Reader reader = getReader()) {
				for(Object match : trie.getMatches(reader)) {
					if(match instanceof SourceFile) {
						dependentSourceFiles.add((SourceFile) match);
					}
					else if(match instanceof ClassSourceFile) {
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
	}
	
	private Trie<Object> createTrie() throws ModelOperationException {
		Trie<Object> trie = new Trie<Object>();
		
//		for(SourceLocation sourceLocation : sourceLocations) {
			for(SourceFile sourceFile : sourceLocation.sourceFiles()) {
				ClassSourceFile classSourceFile = new ClassSourceFile(sourceFile);
				
				trie.add(sourceFile.getRequirePath(), sourceFile);
				trie.add(classSourceFile.getClassName(), classSourceFile);
				
				for(AliasDefinition aliasDefinition : sourceFile.getAliases()) {
					trie.add(aliasDefinition.getName(), aliasDefinition);
				}
			}
//		}
		
		return trie;
	}
}
