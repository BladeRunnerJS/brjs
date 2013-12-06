package com.caplin.cutlass.bundler.parser;

import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.utility.ContentPathParserBuilder;

public class RequestParserFactory {
	
	public static ContentPathParser createCssBundlerRequestParser()
	{
		ContentPathParserBuilder cssRequestParserBuilder = new ContentPathParserBuilder();
		cssRequestParserBuilder
			.accepts("css/<theme>_css.bundle").as("simple-request")
				.and("css/<theme>_<languageCode>_css.bundle").as("language-request")
				.and("css/<theme>_<languageCode>_<countryCode>_css.bundle").as("locale-request")
				.and("css/<theme>_<browser>_css.bundle").as("browser-request")
			.where("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("languageCode").hasForm("[a-z]{2}")
				.and("countryCode").hasForm("[A-Z]{2}")
				.and("browser").hasForm("[a-z]+[0-9]+");
		
		return cssRequestParserBuilder.build();
	}
	
	public static ContentPathParser createHtmlBundlerRequestParser()
	{
		ContentPathParserBuilder htmlRequestParserBuilder = new ContentPathParserBuilder();
		htmlRequestParserBuilder.accepts("html.bundle").as("simple-request");
		
		return htmlRequestParserBuilder.build();
	}
	
	public static ContentPathParser createI18nBundlerRequestParser()
	{
		ContentPathParserBuilder i18nRequestParserBuilder = new ContentPathParserBuilder();
		i18nRequestParserBuilder
			.accepts("i18n/<languageCode>_<countryCode>_i18n.bundle").as("locale-request")
				.and("i18n/<languageCode>_i18n.bundle").as("language-request")
			.where("languageCode").hasForm("[a-z]{2}")
				.and("countryCode").hasForm("[A-Z]{2}");
		
		return i18nRequestParserBuilder.build();
	}
	
	public static ContentPathParser createImageBundlerRequestParser()
	{
		ContentPathParserBuilder imageRequestParserBuilder = new ContentPathParserBuilder();
		imageRequestParserBuilder
			.accepts("images/bladeset_<bladeset>/blade_<blade>/workbench/<imagePath>_image.bundle").as("blade-workbench-request")
				.and("images/bladeset_<bladeset>/blade_<blade>/theme_<theme>/<imagePath>_image.bundle").as("blade-request")
				.and("images/bladeset_<bladeset>/theme_<theme>/<imagePath>_image.bundle").as("bladeset-request")
				.and("images/theme_<theme>/<imagePath>_image.bundle").as("aspect-request")
				.and("images/<sdk>/<imagePath>_image.bundle").as("sdk-request")
			.where("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("imagePath").hasForm(ContentPathParserBuilder.PATH_TOKEN)
				.and("sdk").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		
		return imageRequestParserBuilder.build();
	}
	
	public static ContentPathParser createThirdPartyBundlerRequestParser()
	{
		ContentPathParserBuilder thirdPartyRequestParserBuilder = new ContentPathParserBuilder();
		thirdPartyRequestParserBuilder
			.accepts("thirdparty-libraries/<library>/<resourcePath>_thirdparty.bundle").as("simple-request")
			.where("library").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("resourcePath").hasForm(ContentPathParserBuilder.PATH_TOKEN);
		
		return thirdPartyRequestParserBuilder.build();
	}
	
	public static ContentPathParser createJsBundlerRequestParser()
	{
		ContentPathParserBuilder jsRequestParserBuilder = new ContentPathParserBuilder();
		jsRequestParserBuilder.accepts("js/js.bundle").as("bundle-request")
			.and("js/map_js.bundle").as("source-map-request")
			.and("js/src/<path>_js.bundle").as("source-file-request");
		
		return jsRequestParserBuilder.build();
	}
	
	public static ContentPathParser createXmlBundlerRequestParser()
	{
		ContentPathParserBuilder xmlRequestParser = new ContentPathParserBuilder();
		xmlRequestParser.accepts("xml.bundle").as("simple-request");
		
		return xmlRequestParser.build();
	}
	
}
