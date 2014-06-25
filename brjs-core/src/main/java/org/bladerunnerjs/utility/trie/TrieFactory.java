package org.bladerunnerjs.utility.trie;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.trie.exception.EmptyTrieKeyException;
import org.bladerunnerjs.utility.trie.exception.TrieKeyAlreadyExistsException;

public class TrieFactory {
	private final MemoizedValue<Trie<AssetReference>> trie;
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
	
	public Trie<AssetReference> createTrie() throws ModelOperationException {
		return trie.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws RuntimeException, ModelOperationException {
				Trie<AssetReference> trie = new Trie<AssetReference>();
				
				for (AssetContainer assetContainer : assetContainer.scopeAssetContainers()) {
					try {
						if(assetContainer instanceof BundlableNode) {
							BundlableNode bundlableNode = (BundlableNode) assetContainer;
							
							for(AliasOverride aliasOverride : bundlableNode.aliasesFile().aliasOverrides()) {
								addToTrie(trie, aliasOverride.getName(), new AliasOverrideReference(aliasOverride));
							}
						}
						
						for(LinkedAsset asset : assetContainer.linkedAssets()) {
							List<String> requirePaths = asset.getRequirePaths();
							
							for(String requirePath : requirePaths) {
								addToTrie(trie, requirePath, new LinkedAssetReference(asset));
							}
						}
						
						for(AssetLocation assetLocation : assetContainer.assetLocations()) {
							for(AliasDefinition aliasDefintion : assetLocation.aliasDefinitionsFile().aliases()) {
								String aliasName = aliasDefintion.getName();
								addToTrie(trie, aliasName, new AliasDefinitionReference(aliasDefintion));
							}
						}
					}
					catch (EmptyTrieKeyException | ContentFileProcessingException ex) {
						throw new ModelOperationException(ex);
					}
				}
				
				if (trie.needsOptimizing()) {
					trie.optimize();
				}
				return trie;
			}
		});
	}
	
	private void addToTrie(Trie<AssetReference> trie, String key, AssetReference value) throws EmptyTrieKeyException {
		if (!trie.containsKey(key)) {
			try
			{
				trie.add(key, value);
			}
			catch (TrieKeyAlreadyExistsException | TrieLockedException e)
			{
				// wrap this in a RuntimeException since its unexpected, let the other exceptions bubble up
				throw new RuntimeException(e);
			}
		}
	}
}
