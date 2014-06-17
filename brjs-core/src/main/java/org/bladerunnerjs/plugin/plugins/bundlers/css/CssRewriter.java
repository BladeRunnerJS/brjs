package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class CssRewriter {
	
	private static final String VALID_URI_CHARS = "[a-zA-Z0-9\\-\\._~:/\\?#\\[\\]@!$&\\(\\)\\*\\+,;=]+"; // all valid chars that can be in a URL
	private static final String URL_PATTERN_START = "(\\([\\s]*['|\"]?)"; // start of the pattern - matches a ( followed by an optional ' or "
	private static final String URL_PATTERN_END = "(['|\"]?[\\s]*\\))"; // end ot the pattern - matches an optional ' or " followed by a )
	private static final String NEGATIVE_LOOKAHEADS = "(?![a-zA-Z]+://?"+VALID_URI_CHARS+")" +	// negative lookahead that prevents matching URLs with protocols
														"(?!/.*)" +	// negative lookahead that prevents urls starting with a /
														"(?!(data):[a-zA-Z]+/[a-zA-Z]+;)"; 	// negative lookahead that prevents matching URLs in the format of a data URI
	
	private static final Pattern URL_PATTERN = Pattern.compile(URL_PATTERN_START+NEGATIVE_LOOKAHEADS+"("+VALID_URI_CHARS+")"+URL_PATTERN_END);
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
			String relativePath = urlMatcher.group(3);
			String urlSuffix = urlMatcher.group(4);
		
			
			String parsedUrl = parseUrl(cssBasePath, relativePath);
			String replacement = urlPrefix + parsedUrl + urlSuffix;
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
