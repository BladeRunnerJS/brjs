package org.bladerunnerjs.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class Locale
{
	static class Messages {
		public static final String INVALID_LOCALE_EXCEPTION = "The locale '%s' is not a valid locale. It must be in the format "+LANGUAGE_AND_COUNTRY_CODE_FORMAT.toString();
	}
	
	public static final String LANGUAGE_CODE_FORMAT = "[a-z]{2}";
	public static final String COUNTRY_CODE_FORMAT = "[A-Z]{2}";
	public static final String LANGUAGE_AND_COUNTRY_CODE_FORMAT = LANGUAGE_CODE_FORMAT+"(_"+COUNTRY_CODE_FORMAT+")?";
	public static final String LOCALE_FILENAME_FORMAT = "("+LANGUAGE_AND_COUNTRY_CODE_FORMAT+")\\.[\\S]+";
	
	private static final Pattern LANGUAGE_AND_COUNTRY_CODE_PATTERN = Pattern.compile(LANGUAGE_AND_COUNTRY_CODE_FORMAT);
	
	private String languageCode = "";
	private String countryCode = "";

	public Locale() { 
		// an empty locale
	}
	
	public Locale(String languageCode, String countryCode) throws IllegalArgumentException {
		this.languageCode = (languageCode != null) ? languageCode : "";
		this.countryCode = (countryCode != null) ? countryCode : "";;
		assertIsValidLocale();
	}

	public Locale(String locale) throws IllegalArgumentException {
		if (locale == null) { 
			return;
		}
		languageCode = StringUtils.substringBefore(locale, "_");
		countryCode = StringUtils.substringAfter(locale, languageCode+"_");
		assertIsValidLocale();
	}
	
	public static Locale createLocaleFromFilepath(String filePath) throws IllegalArgumentException {
		return createLocaleFromFilepath("", filePath);
	}
	
	public static Locale createLocaleFromFilepath(String prefix, String filePath) throws IllegalArgumentException {
		filePath = filePath.replace("\\","/");
		String filename = filePath.contains("/") ? StringUtils.substringAfterLast( filePath, "/" ) : filePath;
		Pattern localeFilenamePattern = Pattern.compile(prefix+LOCALE_FILENAME_FORMAT);
		Matcher filenameMatcher = localeFilenamePattern.matcher(filename);
		if (!filenameMatcher.matches()) {
			return new Locale();
		}
		return new Locale( filenameMatcher.group(1) );
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	public boolean hasLanguageCode() {
		return languageCode != null && !languageCode.equals("");
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public boolean hasCountryCode() {
		return countryCode != null && !countryCode.equals("");
	}
	
	public boolean isCompleteLocale() {
		return hasLanguageCode() && hasCountryCode();
	}
	
	public boolean isEmptyLocale() {
		return !hasLanguageCode() && !hasCountryCode();
	}
	
	public boolean equals(Locale locale)
	{
		return toString().equals(locale);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( !(obj instanceof Locale) ) {
			return false;
		}
		return ((Locale) obj).toString().equals(toString());
	}
	
	public boolean isAbsoluteOrPartialMatch(Locale locale)
	{
		return toString().startsWith(locale.toString());
	}
	
	@Override
	public String toString()
	{
		if (hasCountryCode()) {
			return languageCode+"_"+countryCode;
		}
		return languageCode;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public String getLocaleFilePattern(String extension) {
		return getLocaleFilePattern("",extension);
	}
	
	public String getLocaleFilePattern(String prefix, String extension) {
		String extensionRegex = (extension.startsWith(".")) ? extension : "\\."+extension;
		if (hasLanguageCode()) {
			// .*_en_GB.css
			return prefix+"("+toString()+")"+extensionRegex;
		} else {
			/* a funky bit of regex magic so we can support filenames 
			 * with an _ that dont have the format of a locale (e.g. style_sheet.css)
			 * 
			 * (?!.*_[a-z]{2}\\.css$) - negative lookahead that prevents matching .*_en.css files
			 * (?!.*_[a-z]{2}_[A-Z]{2}\\.css) - another negative lookahead that prevents matching .*_en_GB.css files
			 * .* match anything else that doesnt fail with the negative lookaheads
			 */
			return "(?!.*_[a-zA-Z]{2}\\.css$)(?!.*_[a-zA-Z]{2}_[a-zA-Z]{2}\\.css).*\\.css";
		}
	}
	
	
	private void assertIsValidLocale() throws IllegalArgumentException
	{
		if (!hasLanguageCode() && !hasCountryCode()) {
			return;
		}
		if (!LANGUAGE_AND_COUNTRY_CODE_PATTERN.matcher(toString()).matches()) {
			throw new IllegalArgumentException( String.format(Messages.INVALID_LOCALE_EXCEPTION, toString()) );
		}
	}
	
}
