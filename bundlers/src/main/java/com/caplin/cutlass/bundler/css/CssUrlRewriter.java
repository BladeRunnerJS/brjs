package com.caplin.cutlass.bundler.css;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class CssUrlRewriter
{
	private static final Pattern URL_PATTERN = Pattern.compile("url\\(\\s*[\"']?[ ]?([\\s\\S]*?)[\"']?[ ]?\\s*\\)");
	private static final char[] postPathSymbols = new char[]{'?', '#'};
	private final StringBuffer css = new StringBuffer();

	public CssUrlRewriter(File cssBasePath, final CharSequence input) throws ContentProcessingException
	{
		Matcher urlMatcher = URL_PATTERN.matcher(input);
		while (urlMatcher.find())
		{
			String relativePath = urlMatcher.group(1);
			//this is used to ignore any spacing so can be read as a URI correctly.
			String withoutSpacesOrNewLines = relativePath.replaceAll("(\\s)", "");

			boolean parsableUrl = true;
			try
			{
				/* if it parses as a URI don't rewrite */
				URI uri = new URI(withoutSpacesOrNewLines);
				if (uri.isAbsolute())
				{
					parsableUrl = false;
				}
			} catch (URISyntaxException ex)
			{
				 throw new RuntimeException("URI \"" + relativePath + "\" is invalid (" + ex.getReason() + ").", ex);
			}

			if (parsableUrl)
			{
				String parsedUrl = parseUrl(cssBasePath, relativePath);
				urlMatcher.appendReplacement(css, parsedUrl);
			}
		}
		urlMatcher.appendTail(css);
	}

	private String parseUrl(File cssBasePath, String relativePath) throws ContentProcessingException
	{
		String ending = "";
		for(char postPathSymbol: postPathSymbols)
		{
			if(relativePath.contains(Character.toString(postPathSymbol)))
			{
				int index = relativePath.indexOf(postPathSymbol);
				ending = relativePath.substring(index);
				relativePath = relativePath.substring(0, index);
				break;
			}
		}
		File imageFile = new File(getCanonicalPath(cssBasePath.getPath() + "/" + relativePath));
		String targetPath = TargetPathCreator.getRelativeBundleRequestForImage(imageFile);
		return "url(\"" + targetPath + ending + "\")";
	}

	private String getCanonicalPath(String imagePath) throws ContentProcessingException
	{
		try
		{
			return new File(imagePath).getCanonicalPath();
		} catch (IOException e)
		{
			throw new ContentProcessingException("referenced image ('" + imagePath + "') does not exist.");
		}
	}

	public String getCss()
	{
		return css.toString();
	}
}