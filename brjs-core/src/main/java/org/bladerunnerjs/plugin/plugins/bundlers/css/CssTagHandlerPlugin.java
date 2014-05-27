package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;

public class CssTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private CssContentPlugin cssContentPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		VirtualProxyContentPlugin virtualProxyCssContentPlugin = (VirtualProxyContentPlugin) brjs.plugins().contentProvider("css");
		cssContentPlugin = (CssContentPlugin) virtualProxyCssContentPlugin.getUnderlyingPlugin();
	}
	
	@Override
	public String getTagName() {
		return "css.bundle";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(true, writer, bundleSet, tagAttributes.get("theme"), locale, "dev");
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		writeTagContent(false, writer, bundleSet, tagAttributes.get("theme"), locale, version);
	}
	
	private void writeTagContent(boolean isDev, Writer writer, BundleSet bundleSet, String theme, String locale, String version) throws IOException {
		try {
			App app = bundleSet.getBundlableNode().app();
			
			for(String nextTheme : BRJSConformantAssetLocationPlugin.getBundlableNodeThemes(bundleSet.getBundlableNode())) {
				for(String contentPath : cssContentPlugin.getThemeStyleSheetContentPaths(nextTheme, locale)) {
					String requestPath = (isDev) ? app.createDevBundleRequest(contentPath) : app.createProdBundleRequest(contentPath, version);
					
					if(nextTheme.equals("common")) {
						writer.write("<link rel='stylesheet' href='" + requestPath + "'/>\n");
					}
					else if(nextTheme.equals(theme)) {
						writer.write("<link rel='stylesheet' title='" + theme + "' href='" + requestPath + "'/>\n");
					}
					else {
						writer.write("<link rel='alternate stylesheet' title='" + nextTheme + "' href='" + requestPath + "'/>\n");
					}
				}
			}
		}
		catch(MalformedTokenException e) {
			throw new IOException(e);
		}
	}
}
