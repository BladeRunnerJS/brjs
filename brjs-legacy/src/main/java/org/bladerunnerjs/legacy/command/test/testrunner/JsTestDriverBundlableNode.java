package org.bladerunnerjs.legacy.command.test.testrunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.TestAssetLocation;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
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
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		bundlableNode.populate(templateGroup);
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
	public MemoizedFile dir() {
		return bundlableNode.dir();
	}

	@Override
	public MemoizedFile file(String filePath) {
		return bundlableNode.file(filePath);
	}

	@Override
	public AssetLocation assetLocation(String locationPath) {
		return bundlableNode.assetLocation(locationPath);
	}

	@Override
	public MemoizedFile[] memoizedScopeFiles() {
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
	public MemoizedFile storageDir(String pluginName) {
		return bundlableNode.storageDir(pluginName);
	}

	@Override
	public MemoizedFile storageFile(String pluginName, String filePath) {
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
	
	@Override
	public String getTypeName()
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public void incrementFileVersion()
	{
		bundlableNode.incrementFileVersion();	
	}
	
	@Override
	public void incrementChildFileVersions()
	{
		bundlableNode.incrementChildFileVersions();	
	}
}
