package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.eclipse.jetty.util.URIUtil;

public class CssRewriter {
	

	private static final String URL_PATTERN_START = "(url[\\s]*\\([\\s]*['\"]?)"; // start of the pattern - matches a ( followed by an optional ' or "
	private static final String URL_PATTERN_END = "(['\"]?[\\s]*\\))"; // end of the pattern - matches an optional ' or " followed by a )
	private static final String PRE_PATTERN_NEGATIVE_LOOKAHEADS = 
			"(?!['\"])" + // prevent matching a ' or " at the start of the URL (needed because the " and ' in the above regex are optional
			"(?![a-zA-Z]+://)" +	// prevent matching URLs with protocols		
			"(?!/)" +	// prevent matching urls starting with a /
			"(?!data:[a-zA-Z]+/[a-zA-Z]+;)"; 	// prevent matching URLs in the format of a data URI
	private static final Pattern URL_PATTERN = Pattern.compile(URL_PATTERN_START+PRE_PATTERN_NEGATIVE_LOOKAHEADS+"(.*?)"+URL_PATTERN_END, Pattern.CASE_INSENSITIVE);
	private static final char[] postPathSymbols = new char[] { '?', '#' };
	
	private final Asset cssAsset;
	private final TargetPathCreator targetPathCreator;
	
	public CssRewriter(Asset cssAsset) {
		this.cssAsset = cssAsset;
		targetPathCreator = new TargetPathCreator(cssAsset.assetLocation().root());
	}
	
	public String getFileContents() throws IOException, ContentProcessingException {
		try {
			String unprocessedCss = "";
			
			try (Reader fileReader = cssAsset.getReader()) {
				unprocessedCss = IOUtils.toString(fileReader);
			}
			
			return rewriteCss(cssAsset.dir(), unprocessedCss);
		}
		catch (CssImageReferenceException cssImageReferenceException) {
			cssImageReferenceException.setCssFileContainingImageReference(cssAsset.getAssetPath());
			throw cssImageReferenceException;
		}
		catch (Exception e) {
			throw new ContentProcessingException(e, "Error while bundling asset '" + cssAsset.getAssetPath() + "'.");
		}
	}
	
	public String rewriteCss(File cssBasePath, final CharSequence input) throws ContentProcessingException {
		Matcher urlMatcher = URL_PATTERN.matcher(input);
		StringBuffer css = new StringBuffer();
		
		while (urlMatcher.find()) {
			String urlPrefix = urlMatcher.group(1);
			String relativePath = urlMatcher.group(2);
			String urlSuffix = urlMatcher.group(3);
		
			
			String parsedUrl = parseUrl(cssBasePath, relativePath);
			String replacement = urlPrefix + parsedUrl + urlSuffix;
			
			replacement = replacement.replaceAll("\\$","\\\\\\$");
			urlMatcher.appendReplacement(css, replacement);
		}
		urlMatcher.appendTail(css);
		
		return css.toString();
	}
	
	private String parseUrl(File cssBasePath, String relativePath) throws ContentProcessingException {
		String ending = "";
		
		for (char postPathSymbol : postPathSymbols) {
			if (relativePath.contains(Character.toString(postPathSymbol))) {
				int index = relativePath.indexOf(postPathSymbol);
				ending = relativePath.substring(index);
				relativePath = relativePath.substring(0, index);
				break;
			}
		}
		
		File imageFile = new File(getCanonicalPath(cssBasePath.getPath() + "/" + relativePath));
		String targetPath = targetPathCreator.getRelativeBundleRequestForImage(imageFile);
		targetPath = URIUtil.encodePath(targetPath);
		return targetPath + ending;
	}
	
	private String getCanonicalPath(String imagePath) throws ContentProcessingException {
		try {
			return new File(imagePath).getCanonicalPath();
		}
		catch (IOException e) {
			throw new ContentProcessingException("referenced image ('" + imagePath + "') does not exist.");
		}
	}
}
