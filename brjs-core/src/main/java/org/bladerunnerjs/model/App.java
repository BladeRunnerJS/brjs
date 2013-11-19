package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
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
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.LogicalRequestHandler;
import org.bladerunnerjs.model.utility.NameValidator;


public class App extends AbstractBRJSNode implements NamedNode
{
	public class Messages {
		public static final String APP_DEPLOYED_LOG_MSG = "App '%s' at '%s' sucesfully deployed";
		public static final String APP_DEPLOYMENT_FAILED_LOG_MSG = "App '%s' at '%s' could not be sucesfully deployed";
	}
	
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
		
		for(JsLib jsLib : jsLibs()) {
			assetContainers.add(jsLib);
		}
		
		for(Bladeset bladeset : bladesets()) {
			assetContainers.add(bladeset);
			
			for(Blade blade : bladeset.blades()) {
				assetContainers.add(blade);
			}
		}
		
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
		return (BRJS) parent;
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
		return children(jsLibs);
	}
	
	public JsLib jsLib(String jsLibName)
	{
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
			FileUtility.copyDirectoryContents(root().appJars().dir(), file("WEB-INF/lib"));
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
}
