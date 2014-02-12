package org.bladerunnerjs.model;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.utility.LogicalRequestHandler;
import org.bladerunnerjs.utility.filemodification.NodeFileModifiedChecker;

import com.google.common.base.Joiner;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	private Map<String, AssetContainer> assetContainers = new HashMap<>();
	private BundleSet bundleSet;
	private NodeFileModifiedChecker bundleSetFileModifiedChecker = new NodeFileModifiedChecker(this);
	private final LogicalRequestHandler requestHandler;
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		requestHandler = new LogicalRequestHandler(this);
	}
	
	public abstract List<LinkedAsset> getSeedFiles();
	
	@Override
	public List<LinkedAsset> seedFiles() {
		List<LinkedAsset> seedFiles = new ArrayList<>();
		
		seedFiles.addAll(getSeedFiles());
		
		AssetLocation resourcesAssetLocation = assetLocation("resources");
		if (resourcesAssetLocation != null)
		{
			seedFiles.addAll(assetLocation("resources").seedResources());			
		}
		
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
		
		for(AssetContainer assetContainer : getPotentialAssetContainers(requirePath)) {
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
	public AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, ContentFileProcessingException {
		return aliasesFile().getAlias(aliasName);
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		if(bundleSetFileModifiedChecker.hasChangedSinceLastCheck() || (bundleSet == null)) {
			bundleSet = BundleSetCreator.createBundleSet(this);
		}
		
		return bundleSet;
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
	
	@Override
	public void handleLogicalRequest(BladerunnerUri  requestUri, OutputStream os) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		requestHandler.handle( requestUri.logicalPath, os);
	}
	
	
	private List<AssetContainer> getPotentialAssetContainers(String requirePath) {
		List<AssetContainer> potentialAssetContainers = new ArrayList<>();
		int requirePrefixSize = 0;
		AssetContainer prevAssetContainer, nextAssetContainer = null;
		boolean assetContainersMayStillExist = true;
		
		do {
			String requirePrefix = getRequirePrefix(requirePath, ++requirePrefixSize);
			
			if(requirePrefix == null) {
				assetContainersMayStillExist = false;
			}
			else {
				prevAssetContainer = nextAssetContainer;
				nextAssetContainer = assetContainers .get(requirePrefix);
				
				if(nextAssetContainer != null) {
					potentialAssetContainers.add(nextAssetContainer);
				}
				else if(moreAssetContainersMayExistOnDisk(requirePrefixSize, prevAssetContainer)) {
					addMissingAssetContainers(requirePath, potentialAssetContainers);
				}
				else {
					assetContainersMayStillExist = false;
				}
			}
		} while(assetContainersMayStillExist);
		
		return potentialAssetContainers;
	}
	
	private String getRequirePrefix(String requirePath, int i) {
		String[] pathSegments = requirePath.split("/", 1);
		
		return (pathSegments.length != i) ? null : Joiner.on("/").join(pathSegments);
	}
	
	private boolean moreAssetContainersMayExistOnDisk(int requirePrefixSize, AssetContainer prevAssetContainer) {
		boolean moreAssetContainersMayExistOnDisk = true;
		
		if(requirePrefixSize == 3) {
			moreAssetContainersMayExistOnDisk = false;
		}
		else if((requirePrefixSize == 2) && !(prevAssetContainer instanceof Aspect)) {
			moreAssetContainersMayExistOnDisk = false;
		}
		
		return moreAssetContainersMayExistOnDisk;
	}
	
	private void addMissingAssetContainers(String requirePath, List<AssetContainer> potentialAssetContainers) {
		for(AssetContainer assetContainer : getAssetContainers()) {
			assetContainers.put(assetContainer.requirePrefix(), assetContainer);
			
			if(requirePath.startsWith(assetContainer.requirePrefix()) && !potentialAssetContainers.contains(assetContainer)) {
				potentialAssetContainers.add(assetContainer);
			}
		}
	}
}
