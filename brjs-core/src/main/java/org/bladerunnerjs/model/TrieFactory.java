package org.bladerunnerjs.model;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.EmptyTrieKeyException;
import org.bladerunnerjs.utility.Trie;
import org.bladerunnerjs.utility.TrieKeyAlreadyExistsException;

public class TrieFactory {
	private final MemoizedValue<Trie<Object>> trie;
	private final AssetContainer assetContainer;
	
	public static TrieFactory getFactoryForAssetContainer(AssetContainer assetContainer) {
		NodeProperties nodeProperties = assetContainer.nodeProperties("TrieFactory");
		
		if(nodeProperties.getTransientProperty("trieFactoryInstance") == null) {
			nodeProperties.setTransientProperty("trieFactoryInstance", new TrieFactory(assetContainer));
		}
		
		return (TrieFactory) nodeProperties.getTransientProperty("trieFactoryInstance");
	}
	
	private TrieFactory(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		trie = new MemoizedValue<>("TrieFactory.trie", assetContainer);
	}
	
	public Trie<Object> createTrie() throws ModelOperationException {
		return trie.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws RuntimeException, ModelOperationException {
				Trie<Object> trie = new Trie<Object>();
				
				for (AssetContainer assetContainer : assetContainer.scopeAssetContainers()) {
					try {
						if(assetContainer instanceof BundlableNode) {
							BundlableNode bundlableNode = (BundlableNode) assetContainer;
							
							for(AliasOverride aliasOverride : bundlableNode.aliasesFile().aliasOverrides()) {
								if(!trie.containsKey(aliasOverride.getName())) {
									addQuotedKeyToTrie(trie, aliasOverride.getName(), new AliasReference(aliasOverride.getName()));
								}
							}
						}
						
						for(SourceModule sourceModule : assetContainer.sourceModules()) {
							addQuotedKeyToTrie(trie, sourceModule.getRequirePath(), new SourceModuleReference(sourceModule));
							
							if (sourceModule.getClassname() != null) {
								addToTrie(trie, sourceModule.getClassname(), new SourceModuleReference(sourceModule));
								addQuotedKeyToTrie(trie, sourceModule.getClassname(), new SourceModuleReference(sourceModule));
							}
						}
						
						for(AssetLocation assetLocation : assetContainer.assetLocations()) {
							for(String aliasName : assetLocation.aliasDefinitionsFile().aliasNames()) {
								if(!trie.containsKey("'" + aliasName + "'")) {
									addQuotedKeyToTrie(trie, aliasName, new AliasReference(aliasName));
								}
							}
						}
					}
					catch (EmptyTrieKeyException | ContentFileProcessingException ex) {
						throw new ModelOperationException(ex);
					}
				}
				
				return trie;
			}
		});
	}
	
	private void addToTrie(Trie<Object> trie, String key, Object value) throws EmptyTrieKeyException
	{
		try
		{
			trie.add(key, value);
		}
		catch (TrieKeyAlreadyExistsException ex)
		{
		}
	}
	
	private void addQuotedKeyToTrie(Trie<Object> trie, String key, Object value) throws EmptyTrieKeyException {
		addToTrie(trie, "'" + key + "'", value);
		addToTrie(trie, "\\'" + key + "\\'", value);
		addToTrie(trie, "\"" + key + "\"", value);
		addToTrie(trie, "\\\"" + key + "\\\"", value);
		addToTrie(trie, "<" + key + ">", value);
		addToTrie(trie, "<" + key + "/", value);
		addToTrie(trie, "<" + key + " ", value);
		addToTrie(trie, "<" + key + "\t", value);
		addToTrie(trie, "<" + key + "\r", value);
		addToTrie(trie, "<" + key + "\n", value);
	}
}
