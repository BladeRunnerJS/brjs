package org.bladerunnerjs.plugin;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class InputSource {
	private final String requestName;
	private final String source;
	private String sourceMappingUrl;
	private final String filteredSource;
	private final ContentPlugin contentPlugin;
	private BundleSet bundleSet;
	private Pattern sourceMappingUrlPattern = Pattern.compile("\n//# sourceMappingURL=(.*)$");
	
	public InputSource(String requestName, String source, ContentPlugin contentPlugin, BundleSet bundleSet) {
		this.requestName = requestName;
		this.source = source;
		this.sourceMappingUrl = getSourceMappingUrl(source);
		this.filteredSource = source.replaceFirst(sourceMappingUrlPattern.pattern(), "");
		this.contentPlugin = contentPlugin;
		this.bundleSet = bundleSet;
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public String getSource() {
		return filteredSource;
	}
	
	public String getSourceMap() throws MalformedRequestException, ContentProcessingException {
		String sourceMap = null;
		
		try {
			if(source != filteredSource) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ParsedContentPath contentPath = contentPlugin.getContentPathParser().parse(sourceMappingUrl);
				
				contentPlugin.writeContent(contentPath, bundleSet, bos);
				
				sourceMap = bos.toString(bundleSet.getBundlableNode().root().bladerunnerConf().getBrowserCharacterEncoding());
			}
		}
		catch(ConfigException | UnsupportedEncodingException e) {
			throw new ContentProcessingException(e);
		}
		
		return sourceMap;
	}
	
	private String getSourceMappingUrl(String source) {
		Matcher matcher = sourceMappingUrlPattern.matcher(source);
		String sourceMappingUrl = null;
		
		if(matcher.find()) {
			sourceMappingUrl = matcher.group(1);
		}
		
		return sourceMappingUrl;
	}
}
