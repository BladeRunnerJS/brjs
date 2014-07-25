package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.BRJSNodeHelper;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.TheAbstractAssetLocation;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public class BRJSConformantRootAssetLocation extends TheAbstractAssetLocation implements RootAssetLocation {
	public BRJSConformantRootAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	protected List<File> getCandidateFiles() {
		return Collections.emptyList();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this, true);
	}
	
	@Override
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		// TODO: us throwing an exception here means that we have broken the interface-segregation principle -- fix this
		throw new RuntimeException("requirePrefix() only makes sense if the asset-container is of type JsLib");
	}
}
