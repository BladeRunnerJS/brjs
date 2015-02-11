package org.bladerunnerjs.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.utility.ObserverList;

public final class AppSdkJsLib implements JsLib {
	private App app;
	private JsLib sdkJsLib;
	private MemoizedFile[] scopeFiles;

	public AppSdkJsLib(App app, SdkJsLib sdkJsLib) {
		this.app = app;
		this.sdkJsLib = sdkJsLib;
	}
	
	public JsLib getWrappedJsLib() {
		return sdkJsLib;
	}
	
	@Override
	public App app() {
		return app;
	}
	
	@Override
	public String getName() {
		return sdkJsLib.getName();
	}

	@Override
	public String getTypeName() {
		return sdkJsLib.getTypeName();
	}
	
	@Override
	public boolean isValidName() {
		return sdkJsLib.isValidName();
	}
	
	@Override
	public void assertValidName() throws InvalidNameException {
		sdkJsLib.assertValidName();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return sdkJsLib.isNamespaceEnforced();
	}
	
	@Override
	public BRJS root() {
		return sdkJsLib.root();
	}
	
	@Override
	public String requirePrefix() {
		return sdkJsLib.requirePrefix();
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		sdkJsLib.populate(templateGroup);
	}
	
	@Override
	public Set<LinkedAsset> linkedAssets() {
		return sdkJsLib.linkedAssets();
	}
	
	@Override
	public LinkedAsset linkedAsset(String requirePath) {
		return sdkJsLib.linkedAsset(requirePath);
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		return sdkJsLib.scopeAssetContainers();
	}
	
	@Override
	public String getTemplateName() {
		return sdkJsLib.getTemplateName();
	}
	
	@Override
	public Node parentNode() {
		return sdkJsLib.parentNode();
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		sdkJsLib.addTemplateTransformations(transformations);
	}
	
	@Override
	public AssetLocation assetLocation(String locationPath) {
		return sdkJsLib.assetLocation(locationPath);
	}
	
	@Override
	public MemoizedFile dir() {
		return sdkJsLib.dir();
	}
	
	@Override
	public MemoizedFile file(String filePath) {
		return sdkJsLib.file(filePath);
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new MemoizedFile[] {app().libsDir(), app().libsDir(), root().sdkJsLibsDir().dir(), root().file("js-patches")};
		}
		
		return scopeFiles;
	}
	
	@Override
	public void populate(String libNamespace, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		sdkJsLib.populate(libNamespace, templateGroup);
	}
	
	@Override
	public boolean dirExists() {
		return sdkJsLib.dirExists();
	}
	
	@Override
	public boolean exists()
	{
		return sdkJsLib.exists();
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return sdkJsLib.assetLocations();
	}
	
	@Override
	public RootAssetLocation rootAssetLocation() {
		return sdkJsLib.rootAssetLocation();
	}
	
	@Override
	public List<String> getAssetLocationPaths()
	{
		return sdkJsLib.getAssetLocationPaths();
	}
	
	@Override
	public boolean containsFile(String filePath) {
		return sdkJsLib.containsFile(filePath);
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException {
		sdkJsLib.create();
	}
	
	@Override
	public void ready() {
		sdkJsLib.ready();
	}
	
	@Override
	public void delete() throws ModelUpdateException {
		sdkJsLib.delete();
	}
	
	@Override
	public MemoizedFile storageDir(String pluginName) {
		return sdkJsLib.storageDir(pluginName);
	}
	
	@Override
	public MemoizedFile storageFile(String pluginName, String filePath) {
		return sdkJsLib.storageFile(pluginName, filePath);
	}
	
	@Override
	public NodeProperties nodeProperties(String pluginName) {
		return sdkJsLib.nodeProperties(pluginName);
	}
	
	@Override
	public void addObserver(EventObserver observer) {
		sdkJsLib.addObserver(observer);
	}
	
	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer) {
		sdkJsLib.addObserver(eventType, observer);
	}
	
	@Override
	public void notifyObservers(Event event, Node notifyForNode) {
		sdkJsLib.notifyObservers(event, notifyForNode);
	}
	
	@Override
	public ObserverList getObservers() {
		return sdkJsLib.getObservers();
	}
	
	@Override
	public void discoverAllChildren() {
		sdkJsLib.discoverAllChildren();
	}

	@Override
	public void runTests(TestType... testTypes)
	{
		sdkJsLib.runTests(testTypes);
	}

	@Override
	public List<TypedTestPack> testTypes()
	{
		return sdkJsLib.testTypes();
	}

	@Override
	public TypedTestPack testType(String type)
	{
		return sdkJsLib.testType(type);
	}
	
	@Override
	public void incrementFileVersion()
	{
		sdkJsLib.incrementFileVersion();
	}
	
	@Override
	public void incrementChildFileVersions()
	{
		sdkJsLib.incrementChildFileVersions();
	}
	
	@Override
	public AliasesFile aliasesFile() {
		return sdkJsLib.aliasesFile();
	}

	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		return sdkJsLib.getLinkedAsset(requirePath);
	}

	@Override
	public List<AssetLocation> seedAssetLocations() {
		return sdkJsLib.seedAssetLocations();
	}

	@Override
	public List<LinkedAsset> seedAssets() {
		return sdkJsLib.seedAssets();
	}

	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return sdkJsLib.getBundleSet();
	}

	@Override
	public AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException {
		return sdkJsLib.getAlias(aliasName);
	}

	@Override
	public List<AliasDefinitionsFile> aliasDefinitionFiles() {
		return sdkJsLib.aliasDefinitionFiles();
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		return sdkJsLib.handleLogicalRequest(logicalRequestPath, contentAccessor, version);
	}

	@Override
	public List<Asset> getLinkedAssets(AssetLocation assetLocation, List<String> requirePaths) throws RequirePathException {
		return sdkJsLib.getLinkedAssets(assetLocation, requirePaths);
	}
}
