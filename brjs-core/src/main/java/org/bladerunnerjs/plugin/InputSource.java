package org.bladerunnerjs.plugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.StaticContentPluginOutput;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class InputSource {
	
	private static final Pattern SOURCE_MAPPING_URL_PATTERN = Pattern.compile("\n//# sourceMappingURL=(.*)$");
	
	private final String requestName;
	private final String source;
	private String sourceMappingUrl;
	private final String filteredSource;
	private final ContentPlugin contentPlugin;
	private BundleSet bundleSet;

	private Reader reader;
	
	public InputSource(String requestName, String source, ContentPlugin contentPlugin, BundleSet bundleSet) {
		this.requestName = requestName;
		this.source = source;
		this.sourceMappingUrl = getSourceMappingUrl(source);
		this.filteredSource = source.replaceFirst(SOURCE_MAPPING_URL_PATTERN.pattern(), "");
		this.contentPlugin = contentPlugin;
		this.bundleSet = bundleSet;
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public String getSource() {
		return filteredSource;
	}
	
	public String getSourceMap(String version) throws MalformedRequestException, ContentProcessingException {
		String sourceMap = null;
		
		try {
			if(source != filteredSource) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ParsedContentPath contentPath = contentPlugin.getContentPathParser().parse(sourceMappingUrl);
				ContentPluginOutput contentOutputStream = new StaticContentPluginOutput(bundleSet.getBundlableNode().app(), bos);
				
				contentPlugin.writeContent(contentPath, bundleSet, contentOutputStream, version);
				
				sourceMap = bos.toString(bundleSet.getBundlableNode().root().bladerunnerConf().getBrowserCharacterEncoding());
			}
		}
		catch(ConfigException | IOException e) {
			throw new ContentProcessingException(e);
		}
		
		return sourceMap;
	}
	
	private String getSourceMappingUrl(String source) {
		Matcher matcher = SOURCE_MAPPING_URL_PATTERN.matcher(source);
		String sourceMappingUrl = null;
		
		if(matcher.find()) {
			sourceMappingUrl = matcher.group(1);
		}
		
		return sourceMappingUrl;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}
	
	public Reader getReader(){
		return reader;
	}
}
