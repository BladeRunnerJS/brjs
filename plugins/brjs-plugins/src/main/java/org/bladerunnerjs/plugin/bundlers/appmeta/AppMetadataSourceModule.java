package org.bladerunnerjs.plugin.bundlers.appmeta;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.AppMetadataUtility;

import com.Ostermiller.util.ConcatReader;
import com.google.common.base.Joiner;

public class AppMetadataSourceModule  implements SourceModule  {
	public static final String APP_META_DATA = "appmeta!$data";

	private List<String> requirePaths = new ArrayList<>();
	private BundlableNode bundlableNode;

	public AppMetadataSourceModule(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		requirePaths.add(getPrimaryRequirePath());
	}

	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		// do nothing
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public String getPrimaryRequirePath() {
		return APP_META_DATA;
	}

	@Override
	public List<String> getRequirePaths() {
		return requirePaths ;
	}

	@Override
	public Reader getReader() throws IOException {
		try {
			App app = bundlableNode.app();
			String version = bundlableNode.root().getAppVersionGenerator().getVersion();
			
			return new ConcatReader(new Reader[] {
				new StringReader(String.format(CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_HEADER, getPrimaryRequirePath(), "[]")),
				new StringReader(
					"\tmodule.exports.APP_VERSION = '" + version + "';\n" +
					"\tmodule.exports.VERSIONED_BUNDLE_PATH = '" + AppMetadataUtility.getRelativeVersionedBundlePath(app, version, "") + "';\n" +
					"\tmodule.exports.LOCALE_COOKIE_NAME = '" + app.appConf().getLocaleCookieName() + "';\n" +
					"\tmodule.exports.APP_LOCALES = {'" + Joiner.on("':true, '").join(app.appConf().getLocales()) + "':true};\n" +
					"\treturn module.exports;\n"),
				new StringReader(CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_FOOTER)
			});
		}
		catch(ConfigException e) {
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

	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}

	@Override
	public boolean isGlobalisedModule() {
		return false;
	}

	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}
}
