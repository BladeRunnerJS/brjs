package org.bladerunnerjs.legacy.command.test.testrunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;
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
	public Set<Asset> assets() {
		return bundlableNode.assets();
	}

	@Override
	public String getTemplateName() {
		return bundlableNode.getTemplateName();
	}

	@Override
	public Asset asset(String requirePath) {
		return bundlableNode.asset(requirePath);
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
	public MemoizedFile[] memoizedScopeFiles() {
		return bundlableNode.memoizedScopeFiles();
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
	public void create() throws InvalidNameException, ModelUpdateException {
		bundlableNode.create();
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
	public void notifyObservers(Event event, Node notifyForNode) {
		bundlableNode.notifyObservers(event, notifyForNode);
	}

	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		LinkedAsset linkedAsset = bundlableNode.getLinkedAsset(requirePath);
		
		if (linkedAsset instanceof TestAsset) {
			return new JsTestDriverEmptyTestSourceModule((SourceModule) linkedAsset);
		} else {	
			return linkedAsset;
		}
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
	public List<LinkedAsset> seedAssets() {
		return bundlableNode.seedAssets();
	}

	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return new JsTestDriverBundleSet(this, bundlableNode.getBundleSet());
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		return bundlableNode.handleLogicalRequest(logicalRequestPath, contentAccessor, version);
	}
	
	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		return bundlableNode.handleLogicalRequest(logicalRequestPath, contentAccessor, version);
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

	@Override
	public String canonicaliseRequirePath(Asset asset, String requirePath) throws RequirePathException
	{
		return bundlableNode.canonicaliseRequirePath(asset, requirePath);
	}

	@Override
	public List<Asset> assets(Asset asset, List<String> requirePaths) throws RequirePathException
	{
		return bundlableNode.assets(asset, requirePaths);
	}
}
