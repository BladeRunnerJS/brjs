package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.BundlerPlugin;
import org.bladerunnerjs.utility.LogicalRequestHandler;
import org.bladerunnerjs.utility.NameValidator;


public class App extends AbstractBRJSNode implements NamedNode
{
	public class Messages {
		public static final String APP_DEPLOYED_LOG_MSG = "App '%s' at '%s' sucesfully deployed";
		public static final String APP_DEPLOYMENT_FAILED_LOG_MSG = "App '%s' at '%s' could not be sucesfully deployed";
	}
	
	private final NodeMap<JsLib> nonBladeRunnerLibs = JsLib.createAppNonBladeRunnerLibNodeSet();
	private final NodeMap<Bladeset> bladesets = Bladeset.createNodeSet();
	private final NodeMap<Aspect> aspects = Aspect.createNodeSet();
	private final NodeMap<JsLib> jsLibs = JsLib.createAppNodeSet();
	private final LogicalRequestHandler requestHandler;
	
	private String name;
	private AppConf appConf;
	private final Logger logger;
	
	public App(RootNode rootNode, Node parent, File dir, String name)
	{
		this.name = name;
		init(rootNode, parent, dir);
		requestHandler = new LogicalRequestHandler(this);
		logger = rootNode.logger(LoggerType.CORE, Node.class);
	}
	
	public static NodeMap<App> createAppNodeSet()
	{
		return new NodeMap<>(App.class, "apps", null);
	}
	
	public static NodeMap<App> createSystemAppNodeSet()
	{
		return new NodeMap<>(App.class, "sdk/system-applications", null);
	}
	
	public List<AssetContainer> getAllAssetContainers() {
		List<AssetContainer> assetContainer = new ArrayList<>();
		
		for(AbstractAssetContainer aspect : aspects()) {
			assetContainer.add(aspect);
		}
		
		assetContainer.addAll(getNonAspectAssetContainers());
		
		return assetContainer;
	}
	
	public List<AssetContainer> getNonAspectAssetContainers() {
		List<AssetContainer> assetContainers = new ArrayList<>();
		
		for(Bladeset bladeset : bladesets()) {
			assetContainers.add(bladeset);
			
			for(Blade blade : bladeset.blades()) {
				assetContainers.add(blade);
			}
		}
		
		assetContainers.addAll( jsLibs() );
		
		return assetContainers;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		try {
			transformations.put("appns", appConf().getAppNamespace());
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
	
	public List<Bladeset> bladesets()
	{
		return children(bladesets);
	}
	
	public Bladeset bladeset(String bladesetName)
	{
		return child(bladesets, bladesetName);
	}

	public List<Aspect> aspects()
	{
		return children(aspects);
	}
	
	public Aspect aspect(String aspectName)
	{
		return child(aspects, aspectName);
	}
	
	public List<JsLib> jsLibs()
	{
		List<JsLib> appJsLibs = new ArrayList<JsLib>();
		appJsLibs.addAll( children(jsLibs) );
		appJsLibs.add( new JsLibAppWrapper(this, root().sdkLib()) );
		appJsLibs.addAll( nonBladeRunnerLibs() );
		return appJsLibs;
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
		
		return child(jsLibs, jsLibName);
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		super.populate();
		aspect("default").populate();
	};
	
	public void populate(String appNamespace) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, appNamespace);
		
		try {
			appConf().setAppNamespace(appNamespace);
			populate();
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
	
	public List<BundlerPlugin> bundlerPlugins(String mimeType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getNamespace() {
		try {
			return appConf().getAppNamespace();
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void handleLogicalRequest(BladerunnerUri requestUri, java.io.OutputStream os) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException {
		requestHandler.handle(requestUri, os);
	}

	public List<JsLib> nonBladeRunnerLibs()
	{
		Map<String, JsLib> libs = new HashMap<String,JsLib>();
		
		for (JsLib lib : root().sdkNonBladeRunnerLibs())
		{
			libs.put(lib.getName(), new JsLibAppWrapper(this, lib) );			
		}
		for (JsLib lib : children(nonBladeRunnerLibs))
		{
			libs.put(lib.getName(), lib );			
		}
		
		return new ArrayList<JsLib>( libs.values() );
	}
	
	public JsLib nonBladeRunnerLib(String libName)
	{
		JsLib appLib = child(nonBladeRunnerLibs, libName);
		JsLib sdkLib = root().sdkNonBladeRunnerLib(libName);
		
		if (!appLib.dirExists() && sdkLib.dirExists())
		{
			return new JsLibAppWrapper(this, sdkLib);
		}
		return appLib;
	}
	
}
