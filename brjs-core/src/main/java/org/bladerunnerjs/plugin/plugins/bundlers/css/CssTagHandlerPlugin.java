package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;

public class CssTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private CssContentPlugin cssContentPlugin;
	
	@Override
	public void setBRJS(BRJS brjs) {
		cssContentPlugin = (CssContentPlugin) brjs.plugins().contentProvider("css");
	}
	
	@Override
	public String getTagName() {
		return "css.bundle";
	}
	
	@Override
	public String getGroupName() {
		return "text/css";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(writer, bundleSet, tagAttributes.get("theme"), locale);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(writer, bundleSet, tagAttributes.get("theme"), locale);
	}
	
	private void writeTagContent(Writer writer, BundleSet bundleSet, String theme, String locale) throws IOException {
		try {
			List<String> locales = new ArrayList<>();
			locales.add(locale);
			
			for(String nextTheme : BRJSConformantAssetLocationPlugin.getBundlableNodeThemes(bundleSet.getBundlableNode())) {
				for(String contentPath : cssContentPlugin.getThemeStyleSheetContentPaths(nextTheme, locales)) {
					if(nextTheme.equals("common")) {
						writer.write("<link rel='stylesheet' href='" + contentPath + "'/>");
					}
					else if(nextTheme.equals(theme)) {
						writer.write("<link rel='stylesheet' title='" + theme + "' href='" + contentPath + "'/>");
					}
					else {
						writer.write("<link rel='alternate stylesheet' title='" + nextTheme + "' href='" + contentPath + "'/>");
					}
				}
			}
		}
		catch(MalformedTokenException e) {
			throw new IOException(e);
		}
	}
}
