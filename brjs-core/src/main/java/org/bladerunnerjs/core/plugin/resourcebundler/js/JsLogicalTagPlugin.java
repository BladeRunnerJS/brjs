package org.bladerunnerjs.core.plugin.resourcebundler.js;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.resourcebundler.LogicalTagPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;


public class JsLogicalTagPlugin implements LogicalTagPlugin {
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getTagName() {
		return "js.bundle";
	}
	
	@Override
	public void writeDevTag(Map<String, String> tagAttributes, IndexPageFile indexPageFile, String locale, Writer writer) throws IOException {
		BundlableNode bundlableNode = indexPageFile.getBundlableNode();
		
		try {
			for (BundlerPlugin bundler : indexPageFile.getBundlerPlugins("text/javascript")) {
				writeRequests(writer, bundler.generateRequiredDevRequestPaths(bundlableNode.getBundleSet(), locale));
			}
		}
		catch(BundlerProcessingException | ModelOperationException e) {
			// TODO: we need a better standard exception type for tag replacement plugins
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void writeProdTag(Map<String, String> tagAttributes, IndexPageFile indexPageFile, String locale, Writer writer) throws IOException {
		BundlableNode bundlableNode = indexPageFile.getBundlableNode();
		
		try {
			for (BundlerPlugin bundler : indexPageFile.getBundlerPlugins("text/javascript")) {
				writeRequests(writer, bundler.generateRequiredProdRequestPaths(bundlableNode.getBundleSet(), locale));
			}
		}
		catch(BundlerProcessingException | ModelOperationException e) {
			// TODO: we need a better standard exception type for tag replacement plugins
			throw new RuntimeException(e);
		}
	}
	
	private void writeRequests(Writer writer, List<String> requests) throws IOException {
		for (String request : requests) {
			writer.write("<script type='text/javascript' src='" + request + "'></script>\n");
		}
	}
}