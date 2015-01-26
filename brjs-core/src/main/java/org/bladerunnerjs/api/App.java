package org.bladerunnerjs.api;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.events.AppDeployedEvent;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.spec.model.app.building.StaticAppBuilder;
import org.bladerunnerjs.api.spec.model.app.building.WarAppBuilder;
import org.bladerunnerjs.model.AbstractBRJSNode;
import org.bladerunnerjs.model.AppJsLib;
import org.bladerunnerjs.model.AppSdkJsLib;
import org.bladerunnerjs.model.AppUtility;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DefaultAspect;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TemplateUtility;


public class App extends AbstractBRJSNode implements NamedNode
{
	public class Messages {
		public static final String APP_DEPLOYED_LOG_MSG = "App '%s' at '%s' sucesfully deployed";
		public static final String APP_DEPLOYMENT_FAILED_LOG_MSG = "App '%s' at '%s' could not be sucesfully deployed";
	}
	
	public final static String DEFAULT_CONTAINER_NAME = "default";
	
	private final NodeList<Bladeset> bladesets = new NodeList<>(this, Bladeset.class, null, "-bladeset$", "^default");
	private final NodeItem<DefaultBladeset> implicitDefaultBladeset = new NodeItem<>(this, DefaultBladeset.class, ".");
	private final NodeItem<Bladeset> explicitDefaultBladeset = new NodeItem<>(this, Bladeset.class, "default-bladeset");
		// default blade represents 'blades' dir since otherwise 2 nodes are registered for the same path
	private final NodeList<Aspect> aspects = new NodeList<>(this, Aspect.class, null, "-aspect$", "^default");
	private final NodeItem<DefaultAspect> implicitDefaultAspect = new NodeItem<>(this, DefaultAspect.class, ".");
	private final NodeItem<Aspect> explicitDefaultAspect = new NodeItem<>(this, Aspect.class, "default-aspect");
	private final NodeList<AppJsLib> bladeRunnerLibs = new NodeList<>(this, AppJsLib.class, "libs", null);
	
	private String name;
	private AppConf appConf;
	private final Logger logger;
	private MemoizedFile[] scopeFiles;
	private final AppRequestHandler appRequestHandler;
	
	public App(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		logger = rootNode.logger(Node.class);
		appRequestHandler = new AppRequestHandler(this);
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new MemoizedFile[] {dir(), root().sdkJsLibsDir().dir()};
		}
		
		return scopeFiles;
	}
	
	/**
	 * Returns *all* of the asset containers in the model. 
	 * This is different to BundleableNode.getAssetContainers which returns only the valid AssetContainers for a given BundleableNode.
	 */
	public List<AssetContainer> getAllAssetContainers() {
		List<AssetContainer> assetContainersList = new ArrayList<>();
		
		for(Aspect aspect : aspects()) {
			assetContainersList.add(aspect);
			addAllTestPacks(assetContainersList, aspect.testTypes());
		}
		
		assetContainersList.addAll(getNonAspectAssetContainers());
		
		return assetContainersList;
	}
	
	public List<AssetContainer> getNonAspectAssetContainers() {
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
		}
		
		return appConf ;
	}
	
	/**
	 * Get the list of bladesets for the App
	 * @return list of bladesets
	 */
	public List<Bladeset> bladesets()
	{
		List<Bladeset> childBladesets = new ArrayList<>( bladesets.list() );
		Bladeset defaultBladeset = defaultBladeset();
		if (defaultBladeset.exists()) {
			childBladesets.add(0, defaultBladeset);
		}
		return childBladesets;
	}
	
	/**
	 * Get a named bladeset. 
	 * bladeset("default") returns a bladeset with the name 'default' rather than the bladeset that represents the optional bladeset. 
	 * @return the named bladeset
	 */
	public Bladeset bladeset(String bladesetName)
	{
		if (bladesetName.equals(DEFAULT_CONTAINER_NAME)) {
			return defaultBladeset(true);
		}
		return bladesets.item(bladesetName);
	}
	
	/**
	 * Get the default bladeset for the app. This is different from using bladeset("default").
	 * @return the default bladeset
	 * @see #bladeset
	 */
	public Bladeset defaultBladeset()
	{
		return defaultBladeset(false);
	}

	/**
	 * Get the list of aspects for the App
	 * @return list of aspects
	 */
	public List<Aspect> aspects()
	{
		List<Aspect> childAspects = new ArrayList<>( aspects.list() );
		Aspect defaultAspect = defaultAspect();
		if (defaultAspect.exists()) {
			childAspects.add(0, defaultAspect);
		}
		return childAspects;
	}
	
	/**
	 * Get a named aspects. 
	 * aspect("default") returns an aspect with the name 'default' rather than the aspect that represents the optional aspect. 
	 * @return the named bladeset
	 */
	public Aspect aspect(String aspectName)
	{
		if (aspectName.equals(DEFAULT_CONTAINER_NAME)) {
			return defaultAspect(true);
		}
		return aspects.item(aspectName);
	}
	
	/**
	 * Get the default aspect for the app. This is different from using aspect("default").
	 * @return the default aspect
	 * @see #defaultAspect
	 */
	public Aspect defaultAspect()
	{
		return defaultAspect(false);
	}
	
	public List<JsLib> jsLibs()
	{
		Map<String, JsLib> libs = new LinkedHashMap<String,JsLib>();
		
		for (SdkJsLib lib : root().sdkLibs())
		{
			libs.put(lib.getName(), new AppSdkJsLib(this, lib) );			
		}
		for (JsLib lib : bladeRunnerLibs.list())
		{
			libs.put(lib.getName(), lib );			
		}
		
		return new ArrayList<JsLib>( libs.values() );
	}
	
	public JsLib jsLib(String jsLibName)
	{
		JsLib appLib = appJsLib(jsLibName);
		SdkJsLib sdkLib = root().sdkLib(jsLibName);
		
		if (!appLib.dirExists())
		{
			if (sdkLib.dirExists()) {
				return new AppSdkJsLib(this, sdkLib);
			}
		}
		return appLib;
	}
	
	public List<AppJsLib> appJsLibs()
	{
		return bladeRunnerLibs.list();
	}
	
	public AppJsLib appJsLib(String jsLibName)
	{
		return bladeRunnerLibs.item(jsLibName);
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		super.populate(templateGroup);
		
		TemplateUtility.populateOrCreate(defaultAspect(), templateGroup);
		TemplateUtility.populateOrCreate(defaultBladeset(), templateGroup);
	}
	
	public void populate(String requirePrefix, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		NameValidator.assertValidRootPackageName(this, requirePrefix);
		
		try {
			AppConf appConf = appConf();
			appConf.setAutoWrite(false);
			appConf.setRequirePrefix(requirePrefix);
			populate(templateGroup);
			appConf.setAutoWrite(true);
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
			notifyObservers(new AppDeployedEvent(), this);
			incrementFileVersion();
			logger.info(Messages.APP_DEPLOYED_LOG_MSG, getName(), dir().getPath());
		}
		catch (IllegalStateException e) {
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
	
	public MemoizedFile libsDir() {
		return file("libs");
	}
	
	public AppRequestHandler requestHandler() {
		return appRequestHandler;
	}
	
	public void build(MemoizedFile targetDir) throws ModelOperationException {
		new StaticAppBuilder().build(this, targetDir);
	}
	
	public void buildWar(MemoizedFile targetFile) throws ModelOperationException {
		new WarAppBuilder().build(this, targetFile);
	}
	
	
	private Bladeset defaultBladeset(boolean preferExplicitDefault)
	{
		return AppUtility.getImplicitOrExplicitAssetContainer(root(), Bladeset.class, implicitDefaultBladeset.item(false), explicitDefaultBladeset.item(false), preferExplicitDefault); 
	}
	
	private Aspect defaultAspect(boolean preferExplicitDefault)
	{
		return AppUtility.getImplicitOrExplicitAssetContainer(root(), Aspect.class, implicitDefaultAspect.item(false), explicitDefaultAspect.item(false), preferExplicitDefault); 
	}
	
}
