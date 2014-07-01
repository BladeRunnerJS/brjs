package org.bladerunnerjs.plugin;

import java.io.Reader;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginUtility;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class InputSource {
	
//	private static final Pattern SOURCE_MAPPING_URL_PATTERN = Pattern.compile("\n//# sourceMappingURL=(.*)$");
	
	private ParsedContentPath parsedContentPath;
	private final ContentPlugin contentPlugin;
	private BundleSet bundleSet;
	private ContentPluginUtility contentPluginUtility;
	private String version;
	
	public InputSource(ParsedContentPath parsedContentPath, ContentPlugin contentPlugin, BundleSet bundleSet, ContentPluginUtility contentPluginUtility, String version) {
		this.parsedContentPath = parsedContentPath;
		this.contentPlugin = contentPlugin;
		this.bundleSet = bundleSet;
		this.contentPluginUtility = contentPluginUtility;
		this.version = version;
	}
	
	public Reader getContentPluginReader() throws ContentProcessingException {
		return contentPlugin.writeContent(parsedContentPath, bundleSet, contentPluginUtility, version);
	}
	
	
//TODO: re-enable source map support
//	public String getSourceMap(String version) throws MalformedRequestException, ContentProcessingException {
//		String sourceMap = null;
//		
//		try {
//			if(source != filteredSource) {
//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				ParsedContentPath contentPath = contentPlugin.getContentPathParser().parse(sourceMappingUrl);
//				ContentPluginUtility contentOutputStream = new StaticContentPluginUtility(bundleSet.getBundlableNode().app(), bos);
//				
//				contentPlugin.writeContent(contentPath, bundleSet, contentOutputStream, version);
//				
//				sourceMap = bos.toString(bundleSet.getBundlableNode().root().bladerunnerConf().getBrowserCharacterEncoding());
//			}
//		}
//		catch(ConfigException | IOException e) {
//			throw new ContentProcessingException(e);
//		}
//		
//		return sourceMap;
//	}
//	
//	private String getSourceMappingUrl(String source) {
//		Matcher matcher = SOURCE_MAPPING_URL_PATTERN.matcher(source);
//		String sourceMappingUrl = null;
//		
//		if(matcher.find()) {
//			sourceMappingUrl = matcher.group(1);
//		}
//		
//		return sourceMappingUrl;
//	}
	
}
