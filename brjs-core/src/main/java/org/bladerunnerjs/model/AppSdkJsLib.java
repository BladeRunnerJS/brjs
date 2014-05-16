package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.ObserverList;

public final class AppSdkJsLib implements JsLib {
	private App app;
	private JsLib sdkJsLib;
	private File[] scopeFiles;
	
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
	public void populate() throws InvalidNameException, ModelUpdateException {
		sdkJsLib.populate();
	}
	
	@Override
	public Set<SourceModule> sourceModules() {
		return sdkJsLib.sourceModules();
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) {
		return sdkJsLib.sourceModule(requirePath);
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
	public File dir() {
		return sdkJsLib.dir();
	}
	
	@Override
	public File file(String filePath) {
		return sdkJsLib.file(filePath);
	}
	
	@Override
	public File[] scopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {app().libsDir(), app().thirdpartyLibsDir(), root().sdkLibsDir().dir(), root().file("js-patches"), root().conf().file("bladerunner.conf")};
		}
		
		return scopeFiles;
	}
	
	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException {
		sdkJsLib.populate(libNamespace);
	}
	
	@Override
	public boolean dirExists() {
		return sdkJsLib.dirExists();
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
	public File storageDir(String pluginName) {
		return sdkJsLib.storageDir(pluginName);
	}
	
	@Override
	public File storageFile(String pluginName, String filePath) {
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
}
