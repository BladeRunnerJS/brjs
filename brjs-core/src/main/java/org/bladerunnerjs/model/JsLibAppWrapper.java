package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.ObserverList;

public final class JsLibAppWrapper implements JsLib {
	private App jsLibApp;
	private JsLib wrappedJsLib;
	
	public JsLibAppWrapper(App jsLibApp, JsLib jsLib) {
		this.jsLibApp = jsLibApp;
		this.wrappedJsLib = jsLib;
	}
	
	public JsLib getWrappedJsLib() {
		return wrappedJsLib;
	}
	
	@Override
	public App app() {
		return jsLibApp;
	}
	
	@Override
	public String getName() {
		return wrappedJsLib.getName();
	}
	
	@Override
	public boolean isValidName() {
		return wrappedJsLib.isValidName();
	}
	
	@Override
	public void assertValidName() throws InvalidNameException {
		wrappedJsLib.assertValidName();
	}
	
	@Override
	public String namespace() {
		return wrappedJsLib.namespace();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return wrappedJsLib.isNamespaceEnforced();
	}
	
	@Override
	public BRJS root() {
		return wrappedJsLib.root();
	}
	
	@Override
	public String requirePrefix() {
		return wrappedJsLib.requirePrefix();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		wrappedJsLib.populate();
	}
	
	@Override
	public Set<SourceModule> sourceModules() {
		return wrappedJsLib.sourceModules();
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) {
		return wrappedJsLib.sourceModule(requirePath);
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		return wrappedJsLib.scopeAssetContainers();
	}
	
	@Override
	public String getTemplateName() {
		return wrappedJsLib.getTemplateName();
	}
	
	@Override
	public JsLibConf libConf() throws ConfigException {
		return wrappedJsLib.libConf();
	}
	
	@Override
	public Node parentNode() {
		return wrappedJsLib.parentNode();
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		wrappedJsLib.addTemplateTransformations(transformations);
	}
	
	@Override
	public AssetLocation assetLocation(String locationPath) {
		return wrappedJsLib.assetLocation(locationPath);
	}
	
	@Override
	public File dir() {
		return wrappedJsLib.dir();
	}
	
	@Override
	public File file(String filePath) {
		return wrappedJsLib.file(filePath);
	}
	
	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException {
		wrappedJsLib.populate(libNamespace);
	}
	
	@Override
	public boolean dirExists() {
		return wrappedJsLib.dirExists();
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return wrappedJsLib.assetLocations();
	}
	
	@Override
	public boolean containsFile(String filePath) {
		return wrappedJsLib.containsFile(filePath);
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException {
		wrappedJsLib.create();
	}
	
	@Override
	public void ready() {
		wrappedJsLib.ready();
	}
	
	@Override
	public void delete() throws ModelUpdateException {
		wrappedJsLib.delete();
	}
	
	@Override
	public File storageDir(String pluginName) {
		return wrappedJsLib.storageDir(pluginName);
	}
	
	@Override
	public File storageFile(String pluginName, String filePath) {
		return wrappedJsLib.storageFile(pluginName, filePath);
	}
	
	@Override
	public NodeProperties nodeProperties(String pluginName) {
		return wrappedJsLib.nodeProperties(pluginName);
	}
	
	@Override
	public void addObserver(EventObserver observer) {
		wrappedJsLib.addObserver(observer);
	}
	
	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer) {
		wrappedJsLib.addObserver(eventType, observer);
	}
	
	@Override
	public void notifyObservers(Event event, Node notifyForNode) {
		wrappedJsLib.notifyObservers(event, notifyForNode);
	}
	
	@Override
	public ObserverList getObservers() {
		return wrappedJsLib.getObservers();
	}
	
	@Override
	public void discoverAllChildren() {
		wrappedJsLib.discoverAllChildren();
	}

	@Override
	public void runTests(TestType... testTypes)
	{
		wrappedJsLib.runTests(testTypes);
	}

	@Override
	public List<TypedTestPack> testTypes()
	{
		return wrappedJsLib.testTypes();
	}

	@Override
	public TypedTestPack testType(String type)
	{
		return wrappedJsLib.testType(type);
	}
}
