package com.caplin.cutlass.command.test.testrunner;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestAssetLocation;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.utility.ObserverList;

public class JsTestDriverBundlableNode implements BundlableNode {
	private final BundlableNode bundlableNode;
	
	public JsTestDriverBundlableNode(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
	}
	
	public App app() {
		return bundlableNode.app();
	}
	
	@Override
	public String requirePrefix() {
		return bundlableNode.requirePrefix();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		bundlableNode.populate();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return bundlableNode.isNamespaceEnforced();
	}
	
	@Override
	public Set<LinkedAsset> linkedAssets() {
		return bundlableNode.linkedAssets();
	}

	@Override
	public String getTemplateName() {
		return bundlableNode.getTemplateName();
	}

	@Override
	public LinkedAsset linkedAsset(String requirePath) {
		return bundlableNode.linkedAsset(requirePath);
	}

	@Override
	public Node parentNode() {
		return bundlableNode.parentNode();
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		bundlableNode.addTemplateTransformations(transformations);
	}

	@Override
	public File dir() {
		return bundlableNode.dir();
	}

	@Override
	public File file(String filePath) {
		return bundlableNode.file(filePath);
	}

	@Override
	public AssetLocation assetLocation(String locationPath) {
		return bundlableNode.assetLocation(locationPath);
	}

	@Override
	public File[] memoizedScopeFiles() {
		return bundlableNode.memoizedScopeFiles();
	}

	@Override
	public List<AssetLocation> assetLocations() {
		return bundlableNode.assetLocations();
	}

	@Override
	public boolean dirExists() {
		return bundlableNode.dirExists();
	}
	
	@Override
	public boolean exists()
	{
		return bundlableNode.exists();
	}
	
	@Override
	public boolean containsFile(String filePath) {
		return bundlableNode.containsFile(filePath);
	}

	@Override
	public RootAssetLocation rootAssetLocation() {
		return bundlableNode.rootAssetLocation();
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException {
		bundlableNode.create();
	}
	
	public List<String> getAssetLocationPaths() {
		return bundlableNode.getAssetLocationPaths();
	}

	@Override
	public List<AssetContainer> scopeAssetContainers() {
		return bundlableNode.scopeAssetContainers();
	}

	@Override
	public void ready() {
		bundlableNode.ready();
	}

	@Override
	public void delete() throws ModelUpdateException {
		bundlableNode.delete();
	}

	@Override
	public File storageDir(String pluginName) {
		return bundlableNode.storageDir(pluginName);
	}

	@Override
	public File storageFile(String pluginName, String filePath) {
		return bundlableNode.storageFile(pluginName, filePath);
	}

	@Override
	public NodeProperties nodeProperties(String pluginName) {
		return bundlableNode.nodeProperties(pluginName);
	}

	@Override
	public void addObserver(EventObserver observer) {
		bundlableNode.addObserver(observer);
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer) {
		bundlableNode.addObserver(eventType, observer);
	}

	@Override
	public AliasesFile aliasesFile() {
		return bundlableNode.aliasesFile();
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode) {
		bundlableNode.notifyObservers(event, notifyForNode);
	}

	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		LinkedAsset linkedAsset = bundlableNode.getLinkedAsset(requirePath);
		
		if((linkedAsset instanceof SourceModule) && (linkedAsset.assetLocation() instanceof TestAssetLocation)) {
			linkedAsset = new JsTestDriverEmptyTestSourceModule((SourceModule) linkedAsset);
		}
		
		return linkedAsset;
	}
	
	@Override
	public ObserverList getObservers() {
		return bundlableNode.getObservers();
	}

	@Override
	public void discoverAllChildren() {
		bundlableNode.discoverAllChildren();
	}

	@Override
	public List<AssetLocation> seedAssetLocations() {
		return bundlableNode.seedAssetLocations();
	}

	@Override
	public List<LinkedAsset> seedAssets() {
		return bundlableNode.seedAssets();
	}

	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return bundlableNode.getBundleSet();
	}

	@Override
	public AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException {
		return bundlableNode.getAlias(aliasName);
	}

	@Override
	public List<AliasDefinitionsFile> aliasDefinitionFiles() {
		return bundlableNode.aliasDefinitionFiles();
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		return bundlableNode.handleLogicalRequest(logicalRequestPath, contentAccessor, version);
	}

	@Override
	public List<Asset> getLinkedAssets(AssetLocation assetLocation, List<String> requirePaths) throws RequirePathException {
		return bundlableNode.getLinkedAssets(assetLocation, requirePaths);
	}
	
	@Override
	public BRJS root() {
		return bundlableNode.root();
	}
}
