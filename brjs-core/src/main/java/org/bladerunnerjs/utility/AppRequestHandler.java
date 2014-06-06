package org.bladerunnerjs.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;

import com.google.common.base.Joiner;


public class AppRequestHandler {
	public static final String LOCALE_FORWARDING_REQUEST = "locale-forwarding-request";
	public static final String INDEX_PAGE_REQUEST = "index-page-request";
	public static final String BUNDLE_REQUEST = "bundle-request";
	public static final String WORKBENCH_LOCALE_FORWARDING_REQUEST = "workbench-locale-forwarding-request";
	public static final String WORKBENCH_INDEX_PAGE_REQUEST = "workbench-index-page-request";
	public static final String WORKBENCH_BUNDLE_REQUEST = "workbench-bundle-request";
	
	private final App app;
	private final MemoizedValue<ContentPathParser> contentPathParser;
	
	public AppRequestHandler(App app) {
		this.app = app;
		contentPathParser = new MemoizedValue<>("AppRequestHandler.contentPathParser", app.root(), app.dir());
	}
	
	public boolean canHandleLogicalRequest(String requestPath) {
		return getContentPathParser().canParseRequest(requestPath);
	}
	
	public void handleLogicalRequest(String requestPath, OutputStream os, PageAccessor pageAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		ParsedContentPath parsedContentPath = getContentPathParser().parse(requestPath);
		Map<String, String> pathProperties = parsedContentPath.properties;
		String aspectName = getAspectName(requestPath, pathProperties);
		
		switch(parsedContentPath.formName) {
			case LOCALE_FORWARDING_REQUEST:
			case WORKBENCH_LOCALE_FORWARDING_REQUEST:
				writeLocaleForwardingPage(os);
				break;
			
			case INDEX_PAGE_REQUEST:
				writeIndexPage(app.aspect(aspectName), pathProperties.get("locale"), "dev", pageAccessor, os, RequestMode.Dev);
				break;
			
			case WORKBENCH_INDEX_PAGE_REQUEST:
				writeIndexPage(app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench(), pathProperties.get("locale"), "dev", pageAccessor, os, RequestMode.Dev);
				break;
			
			case BUNDLE_REQUEST:
				app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), os);
				break;
			
			case WORKBENCH_BUNDLE_REQUEST:
				app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), os);
				break;
		}
	}
	
	public String createRequest(String requestFormName, String... args) throws MalformedTokenException {
		return getContentPathParser().createRequest(requestFormName, args);
	}
	
	public void writeIndexPage(BrowsableNode browsableNode, String locale, String version, PageAccessor pageAccessor, OutputStream os, RequestMode requestMode) throws ContentProcessingException {
		try {
			File indexPage = (browsableNode.file("index.jsp").exists()) ? browsableNode.file("index.jsp") : browsableNode.file("index.html");
			String indexPageContent = pageAccessor.getIndexPage(indexPage);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			
			String browserCharacterEncoding = browsableNode.root().bladerunnerConf().getBrowserCharacterEncoding();
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, browserCharacterEncoding)) {
				browsableNode.filterIndexPage(indexPageContent, locale, version, writer, requestMode);
			}
			
			os.write(byteArrayOutputStream.toByteArray());
		}
		catch (IOException | ConfigException | ModelOperationException e) {
			throw new ContentProcessingException(e);
		}
	}

	private String getAspectName(String requestPath, Map<String, String> contentPathProperties) throws MalformedRequestException {
		String aspectName = contentPathProperties.get("aspect");
		
		if(aspectName.equals("default/")) {
			throw new MalformedRequestException(requestPath, "The '/default' prefix should be omitted for the default aspect.");
		}
		else if(aspectName.isEmpty()) {
			aspectName = "default";
		}
		else {
			aspectName = aspectName.substring(0, aspectName.length() - 1);
		}
		
		return aspectName;
	}
	
	public void writeLocaleForwardingPage(OutputStream os) throws ContentProcessingException {
		try(Writer writer = new OutputStreamWriter(os, app.root().bladerunnerConf().getBrowserCharacterEncoding());
				Reader reader = new FileReader(app.root().sdkLibsDir().file("locale-forwarder.js"))) {
			writer.write("<head>\n");
			writer.write("<noscript><meta http-equiv='refresh' content='0; url=" + app.appConf().getDefaultLocale() + "/'></noscript>\n");
			writer.write("<script type='text/javascript'>\n");
			writer.write("var $appSupportedLocales = {'" + Joiner.on("':true, '").join(app.appConf().getLocales()) + "':true};\n");
			IOUtils.copy(reader, writer);
			writer.write("\n</script>\n");
			writer.write("</head>\n");
			writer.write("<body onload='forwardToLocalePage()'></body>\n");
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	private ContentPathParser getContentPathParser() {
		return contentPathParser.value(() -> {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("<aspect>").as(LOCALE_FORWARDING_REQUEST)
					.and("<aspect><locale>/").as(INDEX_PAGE_REQUEST)
					.and("<aspect>v/<version>/<content-path>").as(BUNDLE_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/").as(WORKBENCH_LOCALE_FORWARDING_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/<locale>/").as(WORKBENCH_INDEX_PAGE_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/v/<version>/<content-path>").as(WORKBENCH_BUNDLE_REQUEST)
				.where("aspect").hasForm("((" + getAspectNames() + ")/)?")
					.and("workbench").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("version").hasForm("(dev|[0-9]+)")
					.and("locale").hasForm("[a-z]{2}(_[A-Z]{2})?")
					.and("content-path").hasForm(ContentPathParserBuilder.PATH_TOKEN);
			
			return contentPathParserBuilder.build();
		});
	}
	
	private String getAspectNames() {
		List<String> aspectNames = new ArrayList<>();
		
		for(Aspect aspect : app.aspects()) {
			aspectNames.add(aspect.getName());
		}
		
		return Joiner.on("|").join(aspectNames);
	}
}
