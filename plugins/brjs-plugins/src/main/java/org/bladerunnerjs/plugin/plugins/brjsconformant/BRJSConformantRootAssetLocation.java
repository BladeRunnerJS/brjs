package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJSNodeHelper;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.TheAbstractAssetLocation;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;

public class BRJSConformantRootAssetLocation extends TheAbstractAssetLocation implements RootAssetLocation {
	public BRJSConformantRootAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation) {
		super(rootNode, assetContainer, dir, parentAssetLocation);
	}
	
	@Override
	public List<MemoizedFile> getCandidateFiles() {
		return rootNode.getMemoizedFile(dir()).files();
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		BRJSNodeHelper.populate(this, templateGroup, true);
	}
	
	@Override
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		// TODO: us throwing an exception here means that we have broken the interface-segregation principle -- fix this
		throw new RuntimeException("requirePrefix() only makes sense if the asset-container is of type JsLib");
	}
}
