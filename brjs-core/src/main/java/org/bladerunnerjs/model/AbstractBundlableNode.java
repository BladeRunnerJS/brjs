package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.appserver.util.ExceptionThrowingMissingTokenHandler;
import org.bladerunnerjs.appserver.util.MissingTokenHandler;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.AppRequestHandler;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {

	private final MemoizedValue<BundleSet> bundleSet;
	private RequirePlugin defaultRequirePlugin;

	// TODO: these messages need to be covered off in a spec test (a single test would be perfect)
	public class Messages {
		public static final String REQUEST_HANDLED_MSG = "Handling logical request '%s' for app '%s'.";
		public static final String CONTEXT_IDENTIFIED_MSG = "%s '%s' identified as context for request '%s'.";
		public static final String BUNDLER_IDENTIFIED_MSG = "Bundler '%s' identified as handler for request '%s'.";
	}

	public AbstractBundlableNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
		defaultRequirePlugin = root().plugins().requirePlugin("default");
		bundleSet = new MemoizedValue<>(this.getClass().getSimpleName()+" bundleSet", root(), root().dir(), app().dir());
	}

	@Override
	public List<LinkedAsset> seedAssets() {
		List<LinkedAsset> seedAssets = new ArrayList<>();
		seedAssets.addAll( assetDiscoveryResult().getRegisteredSeedAssets() );
		for (AssetContainer scopeAssetContainer : scopeAssetContainers()) {
			if (scopeAssetContainer instanceof Aspect || scopeAssetContainer instanceof Bladeset
						|| scopeAssetContainer instanceof Blade || scopeAssetContainer instanceof Workbench<?>) {
				Asset assetContainerRootAsset = scopeAssetContainer.asset(scopeAssetContainer.requirePrefix() + "@root");
				if (assetContainerRootAsset instanceof LinkedAsset) {
					seedAssets.add( (LinkedAsset) assetContainerRootAsset );
				}
			}
		}
		return seedAssets;
	}

	@Override
	public Asset getAsset(String requirePath) throws RequirePathException {
		Asset asset = null;
		RuntimeException noLinkedAssetException = null;
		RequirePlugin requirePlugin;
		String pluginName;
		String requirePathSuffix;

		if(requirePath.contains("!")) {
			pluginName = StringUtils.substringBefore(requirePath, "!");
			requirePathSuffix = StringUtils.substringAfter(requirePath, "!");
			requirePlugin = root().plugins().requirePlugin(pluginName);
		} else {
			requirePlugin = defaultRequirePlugin;
			pluginName = "default";
			requirePathSuffix = requirePath;
		}

		if (requirePlugin == null) {
			asset = defaultRequirePlugin.getAsset(this, requirePath);
			noLinkedAssetException = new RuntimeException("Unable to find a require plugin for the prefix '"+pluginName+"' and there is no asset registered for the require path '"+requirePath+"'.");
		}
		else {
			asset = requirePlugin.getAsset(this, requirePathSuffix);
			noLinkedAssetException = new RuntimeException("There is no asset registered for the require path '"+requirePathSuffix+"'.");
		}

		if (asset == null) {
			throw noLinkedAssetException;
		}

		return asset;
	}

	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return bundleSet.value(() -> {
			return BundleSetCreator.createBundleSet(this);
		});
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		try {
			return handleLogicalRequest(logicalRequestPath, getBundleSet(), contentAccessor, version);
		}
		catch (ModelOperationException e) {
			throw new ContentProcessingException(e);
		}
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestpath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		App app = bundlableNode.app();
		Logger logger = app.root().logger(AbstractBundlableNode.class);

		logger.debug(Messages.REQUEST_HANDLED_MSG, logicalRequestpath, app.getName());

		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		logger.debug(Messages.CONTEXT_IDENTIFIED_MSG, bundlableNode.getTypeName(), name, logicalRequestpath);

		ContentPlugin contentProvider = app.root().plugins().contentPluginForLogicalPath(logicalRequestpath);

		if(contentProvider == null) {
			throw new ResourceNotFoundException("No content provider could be found found the logical request path '" + logicalRequestpath + "'");
		}

		logger.debug(Messages.BUNDLER_IDENTIFIED_MSG, contentProvider.getPluginClass().getSimpleName(), logicalRequestpath);

		ResponseContent pluginResponseContent = contentProvider.handleRequest(logicalRequestpath, bundleSet, contentAccessor, version);
		try {
			MissingTokenHandler missingTokenHandler = (bundleSet.bundlableNode() instanceof TestPack) ? new ExceptionThrowingMissingTokenHandler() : null;
			return AppRequestHandler.getTokenFilteredResponseContent(app, app.appConf().getDefaultLocale(), version, pluginResponseContent, missingTokenHandler);
		} catch (ConfigException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Asset> assets(Asset asset, List<String> requirePaths) throws RequirePathException {
		List<Asset> assets = new ArrayList<Asset>();

		for(String requirePath : requirePaths) {
			String canonicalRequirePath = asset.assetContainer().canonicaliseRequirePath(asset, requirePath);
			assets.add(getAsset(canonicalRequirePath));
		}

		return assets;
	}

}
