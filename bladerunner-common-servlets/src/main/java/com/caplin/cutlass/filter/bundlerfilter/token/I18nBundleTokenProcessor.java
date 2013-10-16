package com.caplin.cutlass.filter.bundlerfilter.token;

import java.util.Map;

import com.caplin.cutlass.filter.bundlerfilter.TokenProcessor;
import com.caplin.cutlass.filter.bundlerfilter.TokenProcessorException;
import com.caplin.cutlass.request.LocaleHelper;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class I18nBundleTokenProcessor implements TokenProcessor
{
	
	@Override
	public String process(String locale, String userAgent, Map<String, String> attributes) throws TokenProcessorException
	{
		String language = LocaleHelper.getLanguageFromLocale(locale);
		
		return "<script type=\"text/javascript\" src=\"" + BundlePathsFromRoot.I18N + language + "_i18n.bundle\"></script>\n" +
		 "<script type=\"text/javascript\" src=\"" + BundlePathsFromRoot.I18N + locale + "_i18n.bundle\"></script>\n";
	}

}
