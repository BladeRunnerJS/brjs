package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.plugin.utility.IndexPageSeedLocator;
import org.bladerunnerjs.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.utility.TagPluginUtility;

public abstract class AbstractBrowsableNode extends AbstractBundlableNode implements BrowsableNode {
	
	private IndexPageSeedLocator indexPageSeedLocator;
	
	public AbstractBrowsableNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
		indexPageSeedLocator = new IndexPageSeedLocator(root());
	}
	
	@Override
	public void filterIndexPage(String indexPage, Locale locale, String version, Writer writer, RequestMode requestMode) throws ModelOperationException {
		try {
			TagPluginUtility.filterContent(indexPage, getBundleSet(), writer, requestMode, locale, version);
		}
		catch (IOException | NoTagHandlerFoundException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<LinkedAsset> seedAssets() {
		Set<LinkedAsset> seedAssets = new LinkedHashSet<>();
		seedAssets.addAll( indexPageSeedLocator.seedAssets(this) );
		seedAssets.addAll( super.seedAssets() );
		return new ArrayList<>( seedAssets );
	}
}
