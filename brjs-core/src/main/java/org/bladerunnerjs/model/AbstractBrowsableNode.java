package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.utility.TagPluginUtility;
import org.dom4j.DocumentException;

public abstract class AbstractBrowsableNode extends AbstractBundlableNode implements BrowsableNode {
	public AbstractBrowsableNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public void filterIndexPage(String indexPage, String locale, Writer writer, RequestMode requestMode) throws ModelOperationException {
		try {
			TagPluginUtility.filterContent(indexPage, getBundleSet(), writer, requestMode, locale);
		}
		catch (IOException | NoTagHandlerFoundException | DocumentException e) {
			throw new ModelOperationException(e);
		}
	}
}
