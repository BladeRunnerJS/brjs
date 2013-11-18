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
import org.bladerunnerjs.model.utility.EmptyTrieKeyException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.Trie;
import org.bladerunnerjs.model.utility.TrieKeyAlreadyExistsException;

/**
 * A linked asset file that refers to another AssetFile using a fully qualified name such as 'my.package.myClass'
 *
 */
public class FullyQualifiedLinkedAssetFile implements LinkedAssetFile {
	private App app;
	private File assetFile;
	private List<SourceFile> dependentSourceFiles;
	private List<AliasDefinition> aliases;
	private FileModifiedChecker fileModifiedChecker;
	private AssetLocation assetLocation;
	
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File file)
	{
		this.assetLocation = assetLocation;
		app = assetLocation.getAssetContainer().getApp();
		this.assetFile = file;
		fileModifiedChecker = new FileModifiedChecker(file);
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
	
	@Override
	public File getUnderlyingFile() {
		return assetFile;
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
		
		for(AssetContainer assetContainer : app.getAllAssetContainers()) {
			for(SourceFile sourceFile : assetContainer.sourceFiles()) {
				ClassSourceFile classSourceFile = new ClassSourceFile(sourceFile);
				
				try
				{
					if (!sourceFile.getUnderlyingFile().equals(assetFile))
					{
        				trie.add(sourceFile.getRequirePath(), sourceFile);
        				trie.add(classSourceFile.getClassName(), classSourceFile);
        				
        				for(AliasDefinition aliasDefinition : sourceFile.getAliases()) {
        					trie.add(aliasDefinition.getName(), aliasDefinition);
        				}
					}
				}
				catch (TrieKeyAlreadyExistsException ex)
				{
					throw new ModelOperationException(ex);
				}
				catch (EmptyTrieKeyException ex)
				{
					throw new ModelOperationException(ex);					
				}
			}
		}
		
		return trie;
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
}
