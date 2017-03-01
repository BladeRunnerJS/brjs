package org.bladerunnerjs.plugin.plugins.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.AppMetadataUtility;

import com.google.common.base.Joiner;

public class AppMetaDataSourceModule implements CommonJsSourceModule
{

	public static final String PRIMARY_REQUIRE_PATH = "app-meta!$app-metadata";
	private BRJS brjs;
	private BundlableNode bundlableNode;

	public AppMetaDataSourceModule(BRJS brjs, BundlableNode bundlableNode) {
		this.brjs = brjs;
		this.bundlableNode = bundlableNode;
	}

	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}

	@Override
	public boolean isGlobalisedModule() {
		return false;
	}

	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode)
			throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode)
			throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
	}

	@Override
	public Reader getReader() throws IOException {
		try	{
			App app = bundlableNode.app();
			String appLocales = Joiner.on("':true, '").join(app.appConf().getLocales());
			String localCookieName = app.appConf().getLocaleCookieName();
			String version = brjs.root().getAppVersionGenerator().getVersion();
			String bundlePath = AppMetadataUtility.getRelativeVersionedBundlePath(app, version, "");

			return new StringReader(
				String.format(COMMONJS_DEFINE_BLOCK_HEADER, getPrimaryRequirePath()) + "\n" +
				"// these variables should not be used directly but accessed via the 'br.app-meta-service' instead\n" +
				"module.exports.APP_VERSION = '" + version + "';\n" +
				"module.exports.VERSIONED_BUNDLE_PATH = '" + bundlePath + "';\n" +
				"module.exports.LOCALE_COOKIE_NAME = '" + localCookieName + "';\n" +
				"module.exports.APP_LOCALES = {'" + appLocales + "':true};" +
				COMMONJS_DEFINE_BLOCK_FOOTER
			);
		} catch (ConfigException e)	{
			throw new IOException(e);
		}
	}

	@Override
	public MemoizedFile file() {
		return bundlableNode.dir();
	}

	@Override
	public String getAssetName() {
		return getPrimaryRequirePath();
	}

	@Override
	public String getAssetPath() {
		return getPrimaryRequirePath();
	}

	@Override
	public List<String> getRequirePaths() {
		return Arrays.asList(getPrimaryRequirePath());
	}

	@Override
	public String getPrimaryRequirePath() {
		return PRIMARY_REQUIRE_PATH;
	}

	@Override
	public AssetContainer assetContainer() {
		return bundlableNode;
	}

	@Override
	public boolean isScopeEnforced() {
		return false;
	}

	@Override
	public boolean isRequirable() {
		return true;
	}

}
