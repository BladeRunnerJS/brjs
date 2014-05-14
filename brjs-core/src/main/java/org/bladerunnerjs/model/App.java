package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.plugins.commands.standard.InvalidBundlableNodeException;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.PageAccessor;


public class App extends AbstractBRJSNode implements NamedNode
{
	public class Messages {
		public static final String APP_DEPLOYED_LOG_MSG = "App '%s' at '%s' sucesfully deployed";
		public static final String APP_DEPLOYMENT_FAILED_LOG_MSG = "App '%s' at '%s' could not be sucesfully deployed";
	}
	
	private final NodeList<AppJsLib> nonBladeRunnerLibs = new NodeList<>(this, AppJsLib.class, "thirdparty-libraries", null);
	private final NodeList<Bladeset> bladesets = new NodeList<>(this, Bladeset.class, null, "-bladeset$");
	private final NodeList<Aspect> aspects = new NodeList<>(this, Aspect.class, null, "-aspect$");
	private final NodeList<AppJsLib> jsLibs = new NodeList<>(this, AppJsLib.class, "libs", null);
	
	private final MemoizedValue<List<AssetContainer>> assetContainers = new MemoizedValue<>("BRJS.assetContainers", root(), dir(), root().libsDir());
	private final MemoizedValue<List<AssetContainer>> nonAspectAssetContainers = new MemoizedValue<>("BRJS.nonAspectAssetContainers", root(), dir(), root().libsDir());
	private final MemoizedValue<List<JsLib>> nonBladeRunnerLibsList = new MemoizedValue<>("BRJS.nonBladeRunnerLibs", root(), dir(), root().libsDir());
	
	private String name;
	private AppConf appConf;
	private final Logger logger;
	private File[] scopeFiles;
	private final AppRequestHandler appRequestHandler;
	
	public App(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		logger = rootNode.logger(LoggerType.CORE, Node.class);
		appRequestHandler = new AppRequestHandler(this);
		
		registerInitializedNode();
	}
	
	@Override
	public File[] scopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {dir(), root().libsDir(), root().conf().file("bladerunner.conf")};
		}
		
		return scopeFiles;
	}
	
	/**
	 * Returns *all* of the asset containers in the model. 
	 * This is different to BundleableNode.getAssetContainers which returns only the valid AssetContainers for a given BundleableNode.
	 */
	public List<AssetContainer> getAllAssetContainers() {
		return assetContainers.value(() -> {
			List<AssetContainer> assetContainersList = new ArrayList<>();
			
			for(Aspect aspect : aspects()) {
				assetContainersList.add(aspect);
				addAllTestPacks(assetContainersList, aspect.testTypes());
			}
			
			assetContainersList.addAll(getNonAspectAssetContainers());
			
			return assetContainersList;
		});
	}
	
	public List<AssetContainer> getNonAspectAssetContainers() {
		return nonAspectAssetContainers.value(() -> {
			List<AssetContainer> assetContainers = new ArrayList<>();
			
			for(Bladeset bladeset : bladesets()) {
				assetContainers.add(bladeset);
				addAllTestPacks(assetContainers, bladeset.testTypes());
				
				for(Blade blade : bladeset.blades()) {
					assetContainers.add(blade);
					addAllTestPacks(assetContainers, blade.testTypes());
					assetContainers.add(blade.workbench());
					addAllTestPacks(assetContainers, blade.workbench().testTypes());				
				}
			}
			
			for (JsLib jsLib : jsLibs())
			{
				assetContainers.add( jsLib );
				addAllTestPacks(assetContainers, jsLib.testTypes());			
			}
			
			return assetContainers;
		});
	}
	
	private void addAllTestPacks(List<AssetContainer> assetContainers, List<TypedTestPack> typedTestPacks)
	{
		for (TypedTestPack typedTestPack : typedTestPacks)
		{
			for (TestPack testPack : typedTestPack.testTechs())
			{
				assetContainers.add(testPack);
			}
		}
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		try {
			transformations.put("appns", appConf().getRequirePrefix());
			transformations.put("appname", name);
		}
		catch(ConfigException e) {
			throw new ModelUpdateException(e);
		}
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	public BRJS parent()
	{
		return (BRJS) parentNode();
	}
	
	public AppConf appConf() throws ConfigException {
		if(appConf == null) {
			appConf = new AppConf(this);
			appConf.autoWriteOnSet(false);
		}
		
		return appConf ;
	}
	
	public List<Bladeset> bladesets()
	{
		return bladesets.list();
	}
	
	public Bladeset bladeset(String bladesetName)
	{
		return bladesets.item(bladesetName);
	}

	public List<Aspect> aspects()
	{
		return aspects.list();
	}
	
	public Aspect aspect(String aspectName)
	{
		return aspects.item(aspectName);
	}
	
	public List<JsLib> jsLibs()
	{
		Map<String,JsLib> appJsLibs = new LinkedHashMap<>();
		
		for (SdkJsLib lib : root().sdkLibs()) {
			appJsLibs.put(lib.getName(), new AppSdkJsLib(this, lib));
		}
		for (JsLib lib : nonBladeRunnerLibs()) {
			appJsLibs.put(lib.getName(), lib);
		}
		for (JsLib lib : jsLibs.list()) {
			appJsLibs.put(lib.getName(), lib);
		}
		
		return new ArrayList<>( appJsLibs.values() );
	}
	
	public JsLib jsLib(String jsLibName)
	{
		for (JsLib lib : jsLibs()) 
		{
			if (lib.getName().equals(jsLibName))
			{
				return lib;
			}
		}
		
		return jsLibs.item(jsLibName);
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		super.populate();
		aspect("default").populate();
	};
	
	public void populate(String requirePrefix) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, requirePrefix);
		
		try {
			appConf().setRequirePrefix(requirePrefix);
			populate();
			appConf.autoWriteOnSet(true);
			appConf().write();
		}
		catch (ConfigException e) {
			if(e.getCause() instanceof InvalidNameException) {
				throw (InvalidNameException) e.getCause();
			}
			else {
				throw new ModelUpdateException(e);
			}
		}
	}
	
	public void deploy() throws TemplateInstallationException
	{
		try {
			if(!root().appJars().dirExists()) throw new IllegalStateException(
				"The directory containing the app jars, located at '" + root().appJars().dir().getPath() + "', is not present");
			FileUtils.copyDirectory(root().appJars().dir(), file("WEB-INF/lib"));
			notifyObservers(new AppDeployedEvent(), this);
			logger.info(Messages.APP_DEPLOYED_LOG_MSG, getName(), dir().getPath());
		}
		catch (IOException | IllegalStateException e) {
			logger.error(Messages.APP_DEPLOYMENT_FAILED_LOG_MSG, getName(), dir().getPath());
			throw new TemplateInstallationException(e);
		}
	}
	
	public String getRequirePrefix() {
		try {
			return appConf().getRequirePrefix();
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public BundlableNode getBundlableNode(BladerunnerUri bladerunnerUri) throws InvalidBundlableNodeException
	{
		File baseDir = new File(dir(), bladerunnerUri.scopePath);
		BundlableNode bundlableNode = root().locateFirstBundlableAncestorNode(baseDir);
		
		return bundlableNode;
	}
	
	public List<JsLib> nonBladeRunnerLibs()
	{
		return nonBladeRunnerLibsList.value(() -> {
			Map<String, JsLib> libs = new LinkedHashMap<String,JsLib>();
			
			for (SdkJsLib lib : root().sdkNonBladeRunnerLibs())
			{
				libs.put(lib.getName(), new AppSdkJsLib(this, lib) );			
			}
			for (JsLib lib : nonBladeRunnerLibs.list())
			{
				libs.put(lib.getName(), lib );			
			}
			
			return new ArrayList<JsLib>( libs.values() );
		});
	}
	
	public JsLib nonBladeRunnerLib(String libName)
	{
		JsLib appLib = nonBladeRunnerLibs.item(libName);
		SdkJsLib sdkLib = root().sdkNonBladeRunnerLib(libName);
		
		if (!appLib.dirExists() && sdkLib.dirExists())
		{
			return new AppSdkJsLib(this, sdkLib);
		}
		return appLib;
	}
	
	public File libsDir() {
		return file("libs");
	}
	
	public File thirdpartyLibsDir() {
		return file("thirdparty-libraries");
	}
	
	public boolean canHandleLogicalRequest(String requestPath) {
		return appRequestHandler.canHandleLogicalRequest(requestPath);
	}
	
	public void handleLogicalRequest(String requestPath, OutputStream os, PageAccessor pageAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		appRequestHandler.handleLogicalRequest(requestPath, os, pageAccessor);
	}
	
	public String createDevBundleRequest(String contentPath) throws MalformedTokenException {
		return appRequestHandler.createRequest("bundle-request", "", "dev", contentPath);
	}
	
	public String createProdBundleRequest(String contentPath) throws MalformedTokenException {
		return appRequestHandler.createRequest("bundle-request", "", String.valueOf(new Date().getTime()), contentPath);
	}
}
