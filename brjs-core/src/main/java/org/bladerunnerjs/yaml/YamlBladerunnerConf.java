package org.bladerunnerjs.yaml;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.*;

import org.apache.bval.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.utility.ConfigValidationChecker;


public class YamlBladerunnerConf extends AbstractYamlConfFile {
	
	@Min(value=1)
	@Max(value=65535)
	public int jettyPort;
	
	@NotNull
	@NotEmpty
	public String defaultFileCharacterEncoding;

	@NotNull
	@NotEmpty
	public String loginRealm;

	@NotNull
	@NotEmpty
	public final String LOGIN_MODULE_NAME = "BladeRunnerLoginModule";
	
	@NotNull
	public String ignoredPaths;
	
	@NotNull
	public boolean useNodeCommands;
	
	// this can be null - our asking mechanism relies on it
	public Boolean allowAnonymousStats;
	
	@NotNull
	public String fileObserver;
	
	@NotNull // LinkedHashMap so the ordering is preserved and our tests can assert on the contents of written conf files reliably
	public LinkedHashMap<String,List<String>> orderedPlugins;
	
	@Override
	public void initialize(BRJSNode node) {
		jettyPort = getDefault(jettyPort, 7070);
		defaultFileCharacterEncoding = getDefault(defaultFileCharacterEncoding, "UTF-8");
		loginRealm = getDefault(loginRealm, "BladeRunnerLoginRealm");
		ignoredPaths = getDefault(ignoredPaths, ".svn, .git");
		useNodeCommands = getDefault(useNodeCommands, false);
		fileObserver = getDefault(fileObserver, "watching");
		orderedPlugins = getDefault(orderedPlugins, getDefaultOrderedPlugins());
	}
	
	@Override
	public void verify() throws ConfigException {
		ConfigValidationChecker.validate(this);
		verifyCharacterEncodings();
	}
	
	private void verifyCharacterEncodings() throws ConfigException {
		verifyCharacterEncoding("defaultFileCharacterEncoding", defaultFileCharacterEncoding);
	}
	
	private void verifyCharacterEncoding(String propertyName, String characterEncoding) throws ConfigException {
		if(!Charset.isSupported(characterEncoding)) {
			throw new ConfigException("the '" + propertyName + "' in '" + getUnderlyingFile().getPath() + "' is specified as '" +
				characterEncoding + "' which is not a valid character encoding");
		}
	}
	
	private LinkedHashMap<String,List<String>> getDefaultOrderedPlugins() {
		LinkedHashMap<String,List<String>> activePlugins = new LinkedHashMap<>();
		
		// we need to use strings for classes here because they are plugins and aren't on the classpath
		// use the full classname and perform a substring inside 'addDefaultActivePlugin' so refactoring *should* work on these strings
		addDefaultActivePlugin(activePlugins, AssetPlugin.class, "org.bladerunnerjs.plugin.bundlers.thirdparty.ThirdpartyAssetPlugin");		
		addDefaultActivePlugin(activePlugins, AssetPlugin.class, "org.bladerunnerjs.plugin.seedlocator.BrowsableNodeSeedLocator");		
		addDefaultActivePlugin(activePlugins, AssetPlugin.class, "org.bladerunnerjs.plugin.brjsconformant.BRJSConformantAssetPlugin");		
		addDefaultActivePlugin(activePlugins, AssetPlugin.class, "*");		
		
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.i18n.I18nContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.thirdparty.ThirdpartyContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.appmeta.AppMetadataContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.servicepopulator.ServicePopulatorContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "org.bladerunnerjs.plugin.bundlers.namespacedjs.NamespacedJsContentPlugin");
		addDefaultActivePlugin(activePlugins, ContentPlugin.class, "*");
		
		return activePlugins;
	}
	
	private void addDefaultActivePlugin(Map<String,List<String>> activePlugins, Class<? extends Plugin> pluginClass, String value)
	{
		String pluginKey = pluginClass.getSimpleName();
		List<String> pluginsForClass = activePlugins.get(pluginKey);
		if (pluginsForClass == null) {
			pluginsForClass = new ArrayList<>();
			activePlugins.put(pluginKey, pluginsForClass);
		}
		if (value.contains(".")) {
			value = StringUtils.substringAfterLast(value, ".");
		}
		pluginsForClass.add(value);
	}
}
