package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	public abstract List<LinkedAsset> getSeedFiles();
	
	@Override
	public List<LinkedAsset> seedFiles() {
		List<LinkedAsset> seedFiles = new ArrayList<>();
		
		seedFiles.addAll(getSeedFiles());
		seedFiles.addAll(assetLocation("resources").seedResources());
		
		return seedFiles;
	}
	
	@Override
	public AliasesFile aliasesFile() {
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml", this);
		}
		
		return aliasesFile;
	}
	
	@Override
	public SourceModule getSourceModule(String requirePath) throws RequirePathException {
		SourceModule sourceModule = null;
		
		for(AssetContainer assetContainer : getAssetContainers()) {
			SourceModule locationSourceModule = assetContainer.sourceModule(requirePath);
			
			if(locationSourceModule != null) {
				if(sourceModule == null) {
					sourceModule = locationSourceModule;
				}
				else {
					throw new AmbiguousRequirePathException("'" + sourceModule.getAssetPath() + "' and '" +
						locationSourceModule.getAssetPath() + "' source files both available via require path '" +
						sourceModule.getRequirePath() + "'.");
				}
			}
		}
		
		if(sourceModule == null) {
			throw new UnresolvableRequirePathException(requirePath);
		}
		
		return sourceModule;
	}
	
	@Override
	public AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException {
		return aliasesFile.getAlias(aliasName);
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return new BundleSetCreator(this).createBundleSet();
	}
	
	@Override
	public List<AliasDefinitionsFile> getAliasDefinitionFiles() {
		List<AliasDefinitionsFile> aliasDefinitionFiles = new ArrayList<>();
		
		for(AssetContainer assetContainer : getAssetContainers()) {
			for(AssetLocation assetLocation : assetContainer.assetLocations()) {
				aliasDefinitionFiles.add(assetLocation.aliasDefinitionsFile());
			}
		}
		
		return aliasDefinitionFiles;
	}
}
