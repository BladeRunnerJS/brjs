package org.bladerunnerjs.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedFileAccessor;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.InvalidBundlableNodeException;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.memoization.FileModificationRegistryRootFileFilter;
import org.bladerunnerjs.model.AbstractBRJSRootNode;
import org.bladerunnerjs.model.AppVersionGenerator;
import org.bladerunnerjs.model.BRJSGlobalFilesIOFileFilter;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.engine.ValidAppDirFileFilter;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.plugin.utility.CommandList;
import org.bladerunnerjs.plugin.utility.PluginAccessor;
import org.bladerunnerjs.utility.CommandRunner;
import org.bladerunnerjs.utility.JsStyleAccessor;
import org.bladerunnerjs.utility.FileObserverFactory;
import org.bladerunnerjs.utility.LoggerFactory;
import org.bladerunnerjs.utility.PluginLocatorLogger;
import org.bladerunnerjs.utility.UserCommandRunner;
import org.bladerunnerjs.utility.VersionInfo;

public class BRJS extends AbstractBRJSRootNode
{
	public static final String PRODUCT_NAME = "BladeRunnerJS";
	
	public class Messages {
		public static final String PERFORMING_NODE_DISCOVERY_LOG_MSG = "Performing node discovery.";
		public static final String CREATING_PLUGINS_LOG_MSG = "Creating plugins.";
		public static final String MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG = "Making plugins available via model.";
		public static final String PLUGIN_FOUND_MSG = "Found plugin '%s'.";
		public static final String CLOSE_METHOD_NOT_INVOKED = "The BRJS.close() method was not manually invoked, which causes resource leaks that can lead to failure.";
		public static final String BOTH_APPS_AND_BRJS_APPS_EXIST = "BRJS now uses a folder named '%s' for the location of your apps but the directory '%s' contains both '%s' and '%s' folders."+
		" '%s' will be used for the location of apps but this legacy behaviour may be removed so you should move all existing apps into the '%s' directory.";
		public static final String FILE_WATCHER_MESSAGE = "Using '%s' as the BRJS file observer";
	}
	
	private NodeList<App> userApps;
	private final NodeItem<DirNode> sdkRoot = new NodeItem<>(this, DirNode.class, "sdk");
	private final NodeList<App> systemApps = new NodeList<>(this, App.class, "sdk/system-applications", null);
	private final NodeItem<DirNode> sdkLibsDir = new NodeItem<>(this, DirNode.class, "sdk/libs/javascript");
	private final NodeList<SdkJsLib> sdkLibs = new NodeList<>(this, SdkJsLib.class, "sdk/libs/javascript", null);
	private final NodeItem<DirNode> jsPatches = new NodeItem<>(this, DirNode.class, "js-patches");
	private final NodeList<TemplateGroup> confTemplateGroups = new NodeList<>(this, TemplateGroup.class, "conf/templates", null);
	private final NodeList<TemplateGroup> sdkTemplateGroups = new NodeList<>(this, TemplateGroup.class, "sdk/templates", null);
	private final NodeItem<DirNode> appJars = new NodeItem<>(this, DirNode.class, "sdk/libs/java/application");
	private final NodeItem<DirNode> configuration = new NodeItem<>(this, DirNode.class, "conf");
	private final NodeItem<DirNode> systemJars = new NodeItem<>(this, DirNode.class, "sdk/libs/java/system");
	private final NodeItem<DirNode> testJars = new NodeItem<>(this, DirNode.class, "sdk/libs/java/testRunner");
	private final NodeItem<DirNode> userJars = new NodeItem<>(this, DirNode.class, "conf/java");
	private final NodeItem<DirNode> testResults = new NodeItem<>(this, DirNode.class, "sdk/test-results");
	
	private final MemoizedFileAccessor memoizedFileAccessor;
	private final Map<Integer, ApplicationServer> appServers = new LinkedHashMap<Integer, ApplicationServer>();
	private PluginAccessor pluginAccessor;
	private final IOFileFilter globalFilesFilter = new BRJSGlobalFilesIOFileFilter(this);
	private final IO io = new IO( globalFilesFilter );
	private final Logger logger = loggerFactory.getLogger(BRJS.class);
	
	private CommandList commandList;
	private final AppVersionGenerator appVersionGenerator;
	private final FileModificationRegistry fileModificationRegistry;
	private FileObserver fileObserver;
	private final JsStyleAccessor jsStyleAccessor = new JsStyleAccessor(this);

	private BladerunnerConf bladerunnerConf;
	private TestRunnerConf testRunnerConf;
	private boolean closed = false;
	
	private MemoizedFile appsFolder;
	private MemoizedFile sdkFolder;
	private PluginLocator pluginLocator;

	public BRJS(File brjsDir, File workingDir, PluginLocator pluginLocator, LoggerFactory loggerFactory, AppVersionGenerator appVersionGenerator) throws InvalidSdkDirectoryException
	{
		super(brjsDir, loggerFactory);
		
		this.appVersionGenerator = appVersionGenerator;
		memoizedFileAccessor  = new MemoizedFileAccessor(this);
		
		File appsFolderPath = findAppsFolder(brjsDir, workingDir);
		
		FileModificationRegistryRootFileFilter fileModificationRegistryRootFileFilter = new FileModificationRegistryRootFileFilter(this, brjsDir, appsFolderPath);
		fileModificationRegistry = new FileModificationRegistry(fileModificationRegistryRootFileFilter, globalFilesFilter);
		
		appsFolder = getMemoizedFile(appsFolderPath);
		
		userApps = new NodeList<>(this, App.class, null, null, null, new ValidAppDirFileFilter(this), appsFolder);
		this.pluginLocator = pluginLocator;
		sdkFolder = dir().file("sdk");
		
		try
		{
			logger.info(Messages.PERFORMING_NODE_DISCOVERY_LOG_MSG);
			registerNode(this);
			fileModificationRegistryRootFileFilter.addRootFile(rootStorageDir());
		}
		catch (NodeAlreadyRegisteredException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public MemoizedFile appsFolder() {
		return appsFolder;
	}
	
	public MemoizedFile sdkFolder() {
		return sdkFolder;
	}
	
	private File findAppsFolder(File brjsDir, File workingDir) {
		File currentFolder = workingDir;
		while(currentFolder != null) {
			if (new File(currentFolder, AppConf.FILE_NAME).exists()) {
				return currentFolder.getParentFile();
			} else if (new File(currentFolder, "apps").exists() && new File(currentFolder, "sdk").exists()) {
				if (new File(currentFolder, "brjs-apps").exists()) {
					logger.warn(Messages.BOTH_APPS_AND_BRJS_APPS_EXIST, "brjs-apps", brjsDir.getAbsolutePath(), "brjs-apps", "apps", brjsDir.getAbsolutePath()+"/apps", brjsDir.getAbsolutePath()+"/brjs-apps"); 
				}
				return new File (currentFolder, "apps");
			} else if (new File(currentFolder, "brjs-apps").exists()) {
				return new File (currentFolder, "brjs-apps");
			}
			
			currentFolder = currentFolder.getParentFile();
		}
		if (brjsDir == workingDir) {
			return new File (brjsDir, "brjs-apps");
		}
		return workingDir;		
	}
	
	@Override
	public boolean isRootDir(File dir)
	{
		File sdkDir = new File(dir, "sdk");
		
		return (sdkDir.exists() && sdkDir.isDirectory());
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException {
		// do nothing
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		try {
			super.populate(templateGroup);
			if (!bladerunnerConf().fileExists()) {
				bladerunnerConf().write();
			}
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
	
	@Override
	public void finalize() throws IOException, InterruptedException {
		if(!closed) {
			logger.error(Messages.CLOSE_METHOD_NOT_INVOKED);
			close();
		}
	}
	
	public void close() throws IOException, InterruptedException {
		if (fileObserver != null) {
			fileObserver.stop();
		}
		closed  = true;
	}
	
	public BundlableNode locateFirstBundlableAncestorNode(File file) throws InvalidBundlableNodeException
	{
		Node node = locateFirstAncestorNode( getMemoizedFile(file), BundlableNode.class);
		BundlableNode bundlableNode = null;
		
		while((node != null) && (bundlableNode == null))
		{
			if(node instanceof BundlableNode)
			{
				bundlableNode = (BundlableNode) node;
			}
			
			node = node.parentNode();
		}
		
		if (bundlableNode == null) throw new InvalidBundlableNodeException( dir().getRelativePath( getMemoizedFile(file) ) );
		
		return bundlableNode;
	}
	
	@Override
	public IO io() {
		return io;
	}
	
	public JsStyleAccessor jsStyleAccessor() {
		return jsStyleAccessor;
	}
	
	public List<App> apps()
	{
		Map<String,App> apps = new LinkedHashMap<>();
		
		for (App app : systemApps()) {
			apps.put(app.getName(), app);
		}
		for (App app : userApps()) {
			if (!apps.containsKey(app.getName())) {
				apps.put(app.getName(), app);				
			}
		}		
		
		return new ArrayList<>( apps.values() );
	}
	
	public App app(String appName)
	{
		App userApp = userApps.item(appName);
		App systemApp = systemApps.item(appName);
		
		return(systemApp.dirExists()) ? systemApp : userApp;
	}
	
	public List<App> userApps()
	{
		return userApps.list();
	}
	
	public App userApp(String appName)
	{
		return userApps.item(appName);
	}
	
	public List<App> systemApps()
	{
		return systemApps.list();
	}
	
	public App systemApp(String appName)
	{
		return systemApps.item(appName);
	}
	
	public DirNode sdkJsLibsDir()
	{
		return sdkLibsDir.item();
	}
	
	public List<SdkJsLib> sdkLibs()
	{
		return new ArrayList<SdkJsLib>( sdkLibs.list() );
	}
	
	public SdkJsLib sdkLib(String libName)
	{
		return sdkLibs.item(libName);
	}
	
	public DirNode jsPatches()
	{
		return jsPatches.item();
	}
	
	public List<TemplateGroup> confTemplateGroups()
	{
		List<TemplateGroup> templates = new ArrayList<>(confTemplateGroups.list());
		return templates;
	}
	
	public TemplateGroup confTemplateGroup(String templateGroupName)
	{
		return confTemplateGroups.item(templateGroupName);
	}
	
	public List<TemplateGroup> sdkTemplateGroups()
	{
		List<TemplateGroup> templates = new ArrayList<>(sdkTemplateGroups.list());
		return templates;
	}
	
	public TemplateGroup sdkTemplateGroup(String templateGroupName)
	{
		return sdkTemplateGroups.item(templateGroupName);
	}
	
	// TODO: delete this method -- the test results should live within a generated directory
	public DirNode testResults()
	{
		return testResults.item();
	}
	
	public DirNode appJars()
	{
		return appJars.item();
	}
	
	public DirNode conf()
	{
		return configuration.item();
	}
	
	public DirNode systemJars()
	{
		return systemJars.item();
	}
	
	public DirNode testJars()
	{
		return testJars.item();
	}
	
	public DirNode userJars()
	{
		return userJars.item();
	}
	
	public VersionInfo versionInfo()
	{
		return new VersionInfo(this);
	}
	
	public MemoizedFile loginRealmConf()
	{
		return dir().file("sdk/loginRealm.conf");
	}
	
	public MemoizedFile usersPropertiesConf()
	{
		return dir().file("conf/users.properties");
	}
	
	public BladerunnerConf bladerunnerConf() throws ConfigException {
		if(bladerunnerConf == null) {
			bladerunnerConf = new BladerunnerConf(this);
		}
		
		return bladerunnerConf;
	}
	
	public TestRunnerConf testRunnerConf() throws ConfigException {
		if(testRunnerConf == null) {
			testRunnerConf = new TestRunnerConf(this);
		}
		
		return testRunnerConf;
	}
	
	public DirNode sdkRoot() {
		return sdkRoot.item();
	}
	
	public PluginAccessor plugins() {
		if (pluginAccessor == null) {
			logger.info(Messages.CREATING_PLUGINS_LOG_MSG);
			pluginLocator.createPlugins(this);
			PluginLocatorLogger.logPlugins(logger, pluginLocator);
			
			logger.info(Messages.MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
			try
			{
				pluginAccessor = new PluginAccessor(this, pluginLocator);
			}
			catch (ConfigException e)
			{
				throw new RuntimeException(e);
			}
		}
		return pluginAccessor;
	}
	
	public int runCommand(String... args) throws NoSuchCommandException, CommandArgumentsException, CommandOperationException
	{
		return CommandRunner.run(this, getCommandList(), args);
	}
	
	public int runUserCommand(LogLevelAccessor logLevelAccessor, String... args) throws CommandOperationException
	{
		return UserCommandRunner.run(this, getCommandList(), logLevelAccessor, args);
	}
	
	public ApplicationServer applicationServer() throws ConfigException
	{
		return applicationServer( bladerunnerConf().getJettyPort() );
	}
	
	public ApplicationServer applicationServer(int port)
	{
		ApplicationServer appServer = appServers.get(port);
		if (appServer == null)
		{
			appServer = new BRJSApplicationServer(this, port);
			appServers.put(port, appServer);
		}
		return appServer;
	}
	
	public LoggerFactory getLoggerFactory() {
		return loggerFactory;
	}
	
	public AppVersionGenerator getAppVersionGenerator()
	{
		return appVersionGenerator;
	}
	
	@Override
	public String toString()
	{
		return getTypeName()+", dir: " + dir().getPath();
	}

	@Override
	public FileModificationRegistry getFileModificationRegistry()
	{
		return fileModificationRegistry;
	}
	
	@Override
	public MemoizedFile getMemoizedFile(File file)
	{
		return memoizedFileAccessor.getMemoizedFile(file);
	}
	
	@Override
	public MemoizedFile getMemoizedFile(File dir, String name)
	{
		return getMemoizedFile( new File(dir, name) );
	}
	
	public FileObserver fileObserver() throws ConfigException, IOException {
		if (fileObserver == null) {
			try
			{
				fileObserver = FileObserverFactory.getObserver(this);
				logger.debug(Messages.FILE_WATCHER_MESSAGE, fileObserver.getClass().getSimpleName());
			}
			catch (ConfigException ex) {
				throw ex;
			}
		}
		return fileObserver;
	}

	private CommandList getCommandList() {
		if (commandList == null) {
			commandList = new CommandList(this, plugins().commandPlugins());
		}
		return commandList;
	}
	
}
