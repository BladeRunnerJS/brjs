package org.bladerunnerjs.core.plugin.minifier;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class InputSource {
	private final String requestName;
	private final String source;
	private String sourceMappingUrl;
	private final String filteredSource;
	private final BundlerPlugin bundlerPlugin;
	private BundleSet bundleSet;
	private Pattern sourceMappingUrlPattern = Pattern.compile("\n//# sourceMappingURL=(.*)$");
	
	public InputSource(String requestName, String source, BundlerPlugin bundlerPlugin, BundleSet bundleSet) {
		this.requestName = requestName;
		this.source = source;
		this.sourceMappingUrl = getSourceMappingUrl(source);
		this.filteredSource = source.replaceFirst(sourceMappingUrlPattern.pattern(), "");
		this.bundlerPlugin = bundlerPlugin;
		this.bundleSet = bundleSet;
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public String getSource() {
		return filteredSource;
	}
	
	public String getSourceMap() throws MalformedRequestException, BundlerProcessingException {
		String sourceMap = null;
		
		try {
			if(source != filteredSource) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ParsedRequest parsedRequest = bundlerPlugin.getRequestParser().parse(sourceMappingUrl);
				
				bundlerPlugin.handleRequest(parsedRequest, bundleSet, bos);
				
				sourceMap = bos.toString(bundleSet.getBundlableNode().root().bladerunnerConf().getDefaultOutputEncoding());
			}
		}
		catch(ConfigException | UnsupportedEncodingException e) {
			throw new BundlerProcessingException(e);
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
