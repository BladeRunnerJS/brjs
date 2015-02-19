package org.bladerunnerjs.plugin.brjsconformant;

import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BRJSNodeHelper;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.TheAbstractAssetLocation;
import org.bladerunnerjs.model.engine.RootNode;

public class BRJSConformantRootAssetLocation extends TheAbstractAssetLocation implements RootAssetLocation {
	public BRJSConformantRootAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation) {
		super(rootNode, assetContainer, dir, parentAssetLocation);
	}
	
	@Override
	public List<MemoizedFile> getCandidateFiles() {
		return Collections.emptyList();
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
