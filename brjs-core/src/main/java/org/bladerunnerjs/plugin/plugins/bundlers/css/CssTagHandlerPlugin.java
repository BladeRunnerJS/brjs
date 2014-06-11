package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;

public class CssTagHandlerPlugin extends AbstractTagHandlerPlugin {
	private CssContentPlugin cssContentPlugin;
	
	private static String COMMON_THEME_NAME = "common";
	private static String THEME_ATTRIBUTE = "theme";
	private static String ALT_THEME_ATTRIBUTE = "alternateTheme";
	public static String INVALID_THEME_EXCEPTION = String.format("The attribute '%s' should only contain a single theme and cannot contain spaces.", THEME_ATTRIBUTE);
	
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
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		writeTagContent(true, writer, bundleSet, getTheme(tagAttributes), getAlternateThemes(tagAttributes), locale, version);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, String version) throws IOException {
		writeTagContent(false, writer, bundleSet, getTheme(tagAttributes), getAlternateThemes(tagAttributes), locale, version);
	}
	
	private String getTheme(Map<String, String> tagAttributes) throws IOException {
		String themeName = tagAttributes.get(THEME_ATTRIBUTE);
		if (themeName != null && themeName.contains(",")) {
			throw new IOException( INVALID_THEME_EXCEPTION );
		}
		return themeName;
	}
	
	private List<String> getAlternateThemes(Map<String, String> tagAttributes) throws IOException {
		String alternateThemes = tagAttributes.get(ALT_THEME_ATTRIBUTE);
		if (alternateThemes == null) {
			return Arrays.asList();
		}
		return Arrays.asList( tagAttributes.get(ALT_THEME_ATTRIBUTE).split(",") );
	}
	
	
	private void writeTagContent(boolean isDev, Writer writer, BundleSet bundleSet, String theme, List<String> alternateThemes, String locale, String version) throws IOException {
		try {
			App app = bundleSet.getBundlableNode().app();
			List<String> contentPaths = (isDev) ? cssContentPlugin.getValidDevContentPaths(bundleSet, locale) : cssContentPlugin.getValidProdContentPaths(bundleSet, locale);
			
			if (theme == null && alternateThemes.size() == 0) {
				writeTagsForCommonTheme(isDev, app, writer, contentPaths, version);
			} else if (theme != null) {
				writeTagsForCommonTheme(isDev, app, writer, contentPaths, version);
				writeTagsForMainTheme(isDev, app, writer, contentPaths, theme, version);
			}
			
			for (String alternateTheme : alternateThemes) {
				writeTagsForAlternateTheme(isDev, app, writer, contentPaths, alternateTheme, version);
			}
		}
		catch(MalformedTokenException | ContentProcessingException | MalformedRequestException e) {
			throw new IOException(e);
		}
	}
	
	private void writeTagsForCommonTheme(boolean isDev, App app, Writer writer, List<String> contentPaths, String version) throws IOException, MalformedTokenException, MalformedRequestException {
		for(String contentPath : contentPaths) {
			if (getThemeFromContentPath(contentPath).equals(COMMON_THEME_NAME)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				writer.write( String.format("<link rel=\"stylesheet\" href=\"%s\"/>\n", requestPath) );
			}
		}
	}
	
	private void writeTagsForMainTheme(boolean isDev, App app, Writer writer, List<String> contentPaths, String themeName, String version) throws IOException, MalformedTokenException, MalformedRequestException {
		for(String contentPath : contentPaths) {
			if (getThemeFromContentPath(contentPath).equals(themeName)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				writer.write( String.format("<link rel=\"stylesheet\" title=\"%s\" href=\"%s\"/>\n", themeName, requestPath) );
			}
		}
	}
	
	private void writeTagsForAlternateTheme(boolean isDev, App app, Writer writer, List<String> contentPaths, String themeName, String version) throws IOException, MalformedTokenException, MalformedRequestException {
		for(String contentPath : contentPaths) {
			if (getThemeFromContentPath(contentPath).equals(themeName)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				writer.write( String.format("<link rel=\"alternate stylesheet\" title=\"%s\" href=\"%s\"/>\n", themeName, requestPath) );
			}
		}
	}
	
	
	private String getRequestPath(boolean isDev, App app, String contentPath, String version) throws MalformedTokenException {
		return (isDev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version); 
	}
	
	private String getThemeFromContentPath(String contentPath) throws MalformedRequestException {
		return cssContentPlugin.getContentPathParser().parse(contentPath).properties.get("theme"); 
	}
	
}
