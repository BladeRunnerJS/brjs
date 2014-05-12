package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public class AppRequestHandler {
	private final ContentPathParser contentPathParser;
	private final App app;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("<aspect>/").as("locale-forwarding-request")
				.and("<aspect>/<locale>/").as("index-page-request")
				.and("<aspect>/v/<version>/<content-path>").as("bundle-request")
				.and("<aspect>/workbench/<bladeset>/<blade>/").as("workbench-locale-forwarding-request")
				.and("<aspect>/workbench/<bladeset>/<blade>/<locale>/").as("workbench-index-page-request")
				.and("<aspect>/workbench/<bladeset>/<blade>/v/<version>/<content-path>").as("workbench-bundle-request")
			.where("aspect").hasForm("(/" + ContentPathParserBuilder.NAME_TOKEN + ")?")
				.and("workbench").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("version").hasForm("[0-9]+")
				.and("locale").hasForm("[a-z]{2}_[A-Z]{2}");
		contentPathParser = contentPathParserBuilder.build();
	}
	
	public AppRequestHandler(App app) {
		this.app = app;
	}
	
	public boolean canHandleLogicalRequest(String requestPath) {
		return contentPathParser.canParseRequest(requestPath);
	}
	
	public void handleLogicalRequest(String requestPath, OutputStream os, PageAccessor pageAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		ParsedContentPath parsedContentPath = contentPathParser.parse(requestPath);
		Map<String, String> pathProperties = parsedContentPath.properties;
		String aspectName = getAspectName(requestPath, pathProperties);
		
		switch(parsedContentPath.formName) {
			case "locale-forwarding-request":
			case "workbench-locale-forwarding-request":
				writeLocaleForwardingPage(os);
				break;
			
			case "index-page-request":
				writeIndexPage(app.aspect(aspectName), pathProperties.get("locale"), pageAccessor, os);
				break;
			
			case "workbench-index-page-request":
				writeIndexPage(app.bladeset(pathProperties.get("bladset")).blade(pathProperties.get("blade")).workbench(), pathProperties.get("locale"), pageAccessor, os);
				break;
			
			case "bundle-request":
				app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), os);
				break;
			
			case "workbench-bundle-request":
				app.bladeset(pathProperties.get("bladset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), os);
				break;
		}
	}
	
	private void writeIndexPage(BrowsableNode browsableNode, String locale, PageAccessor pageAccessor, OutputStream os) throws ContentProcessingException {
		try {
			pageAccessor.serveIndexPage(browsableNode, locale);
		}
		catch (IOException e) {
			throw new ContentProcessingException(e);
		}
	}

	private String getAspectName(String requestPath, Map<String, String> contentPathProperties) throws MalformedRequestException {
		String aspectName = contentPathProperties.get("aspect");
		
		if(aspectName.equals("/default")) {
			throw new MalformedRequestException(requestPath, "The '/default' prefix should be omitted for the default aspect.");
		}
		else if(aspectName.isEmpty()) {
			aspectName = "default";
		}
		else {
			aspectName = aspectName.substring(1);
		}
		
		return aspectName;
	}
	
	private void writeLocaleForwardingPage(OutputStream os) throws ContentProcessingException {
		try(Writer writer = new OutputStreamWriter(os, app.root().bladerunnerConf().getBrowserCharacterEncoding())) {
			writer.write("<script>alert('locale switching page');</script>");
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}
}
