package com.caplin.cutlass.filter.bundlerfilter.token;

import java.util.Map;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.filter.bundlerfilter.BrowserCssHelper;
import com.caplin.cutlass.filter.bundlerfilter.TokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.TokenProcessorException;
import com.caplin.cutlass.request.LocaleHelper;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class CSSBundleTokenProcessor implements TokenProcessor
{
	private static final String ATTR_THEME = "theme";
	private static final String ATTR_ALT_THEME = "alternateTheme";
	
	@Override
	public String process(String locale, String browser, Map<String, String> attributes) throws TokenProcessorException
	{
		if (attributes.size() > 1)
		{
			throw new TokenProcessorException("Expected one attribute " + ATTR_THEME + " or " + ATTR_ALT_THEME);
		}		
		
		String content = "";
		
		if (attributes.containsKey(ATTR_THEME))
		{
			String theme = attributes.get(ATTR_THEME);
			if (theme.trim().equals(""))
			{
				throw new TokenProcessorException("Missing theme");
			}
			
			content += getMainCssGroup("", CutlassConfig.COMMON_CSS, locale, browser);
			content += getMainCssGroup(theme, theme, locale, browser);
		}
		else if (attributes.containsKey(ATTR_ALT_THEME))
		{
			String theme = attributes.get(ATTR_ALT_THEME);
			if (theme.trim().equals(""))
			{
				throw new TokenProcessorException("Missing theme");
			}
			content += getAlternativeCssGroup(theme, theme, locale, browser);
		} else {
			throw new TokenProcessorException("Missing attribute " + ATTR_THEME + " or " + ATTR_ALT_THEME);			
		}
		
		return content;
	}
	
	private String getMainCssGroup(String title, String filename, String locale, String browser)
	{
		return getCssGroup("stylesheet", title, filename, locale, browser);
	}
	
	private String getAlternativeCssGroup(String title, String filename, String locale, String browser)
	{
		return getCssGroup("alternate stylesheet", title, filename, locale, browser);
	}
	
	private String getCssGroup(String stylesheet, String title, String filename, String locale, String browser)
	{
		String content = "";
		content += getStyleSheet(stylesheet, title, BundlePathsFromRoot.CSS + filename + "_css.bundle");
		
		String languageCode = LocaleHelper.getLanguageFromLocale(locale);
		content += getStyleSheet(stylesheet, title, BundlePathsFromRoot.CSS + filename + "_" + languageCode + "_css.bundle");
		
		if (!languageCode.equals(locale))
		{
			content += getStyleSheet(stylesheet, title, BundlePathsFromRoot.CSS + filename + "_" + locale + "_css.bundle");			
		}

		if (!browser.equals(BrowserCssHelper.UNKNOWN_BROWSER) && !browser.equals(""))
		{
			content += getStyleSheet(stylesheet, title, BundlePathsFromRoot.CSS + filename + "_" + browser + "_css.bundle");
		}
	
		return content;
	}

	private String getStyleSheet(String stylesheet, String title, String href)
	{
		if (!title.equals(""))
		{
			title = "title=\"" + title + "\" ";
		}
		return "<link rel=\"" + stylesheet + "\" " + title + "href=\"" + href + "\"/>\n";
	}
	
}
