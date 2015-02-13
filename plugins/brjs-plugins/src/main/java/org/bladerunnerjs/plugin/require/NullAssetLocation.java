package org.bladerunnerjs.plugin.require;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.NamespaceException;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.utility.ObserverList;

public class NullAssetLocation implements AssetLocation {
	private final BRJS brjs;

	public NullAssetLocation(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public BRJS root() {
		return brjs;
	}

	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException {
		// do nothing
	}

	@Override
	public String getTemplateName() {
		return brjs.getTemplateName();
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}

	@Override
	public Node parentNode() {
		return null;
	}

	@Override
	public MemoizedFile dir() {
		return brjs.dir();
	}

	@Override
	public MemoizedFile file(String filePath) {
		return brjs.file(filePath);
	}

	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		return new MemoizedFile[]{};
	}

	@Override
	public String getTypeName() {
		return brjs.getTypeName();
	}

	@Override
	public boolean dirExists() {
		return brjs.dirExists();
	}

	@Override
	public boolean exists() {
		return brjs.exists();
	}

	@Override
	public boolean containsFile(String filePath) {
		return brjs.containsFile(filePath);
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException {
		// do nothing
	}

	@Override
	public void ready() {
		// do nothing
	}

	@Override
	public void delete() throws ModelUpdateException {
		// do nothing
	}

	@Override
	public MemoizedFile storageDir(String pluginName) {
		return brjs.storageDir(pluginName);
	}

	@Override
	public MemoizedFile storageFile(String pluginName, String filePath) {
		return brjs.storageFile(pluginName, filePath);
	}

	@Override
	public NodeProperties nodeProperties(String pluginName) {
		return brjs.nodeProperties(pluginName);
	}

	@Override
	public void addObserver(EventObserver observer) {
		brjs.addObserver(observer);
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer) {
		brjs.addObserver(eventType, observer);
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode) {
		brjs.notifyObservers(event, notifyForNode);
	}

	@Override
	public ObserverList getObservers() {
		return brjs.getObservers();
	}

	@Override
	public void discoverAllChildren() {
		// do nothing
	}

	@Override
	public void incrementFileVersion() {
		// do nothing
	}

	@Override
	public void incrementChildFileVersions() {
		// do nothing
	}

	@Override
	public String requirePrefix() {
		return "";
	}

	@Override
	public AssetLocation parentAssetLocation() {
		return null;
	}

	@Override
	public AssetContainer assetContainer() {
		return null;
	}

	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return Collections.emptyList();
	}

	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {
		return null;
	}

	@Override
	public List<AliasDefinitionsFile> aliasDefinitionsFiles() {
		return Collections.emptyList();
	}

	@Override
	public List<LinkedAsset> linkedAssets() {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> bundlableAssets() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Asset> bundlableAssets(AssetPlugin assetProducer) {
		return Collections.emptyList();
	}

	@Override
	public List<SourceModule> sourceModules() {
		return Collections.emptyList();
	}

	@Override
	public String canonicaliseRequirePath(String requirePath) throws RequirePathException {
		return requirePath;
	}

	@Override
	public String jsStyle() {
		return null;
	}

	@Override
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		// do nothing
	}

	@Override
	public List<MemoizedFile> getCandidateFiles() {
		return Collections.emptyList();
	}
}
