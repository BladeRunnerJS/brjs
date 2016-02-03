package org.bladerunnerjs.plugin.bundlers.servicepopulator;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class ServicePopulatorContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
{
    private ContentPathParser contentPathParser;
    private BRJS brjs;

    {
        ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
        contentPathParserBuilder
                .accepts("service/initialization.js").as("service-request");
        contentPathParser = contentPathParserBuilder.build();
    }

    @Override
    public void setBRJS(BRJS brjs)
    {
        this.brjs = brjs;
    }

    @Override
    public String getRequestPrefix()
    {
        return "service";
    }

    @Override
    public String getCompositeGroupName() {
        return "text/javascript";
    }

    @Override
    public ContentPathParser getContentPathParser()
    {
        return contentPathParser;
    }

    @Override
    public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
    {
        List<String> requestPaths = new ArrayList<String>();

        try {
            requestPaths.add(contentPathParser.createRequest("service-request"));
        }
        catch(MalformedTokenException e) {
            throw new ContentProcessingException(e);
        }

        return requestPaths;
    }

    @Override
    public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
    {
        try {
            ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
            if (parsedContentPath.formName.equals("service-request"))
            {
                List<Reader> readerList = new ArrayList<Reader>();
                readerList.add(new StringReader(
                    "try {\n" +
                    "    var serviceBox = require('br/servicebox/serviceBox');\n" +
                    "    var ServicePopulatorClass = require('br/servicepopulator/ServicePopulatorClass');\n" +
                    "    var servicePopulator = new ServicePopulatorClass(serviceBox);\n" +
                    "    var syncResolve = require('syncResolve');\n" +
                    "    servicePopulator.populate();\n" +
                    "    syncResolve(function() {\n" +
                    "        return serviceBox.resolveAll();\n" +
                    "    });\n" +
                    "}\n" +
                    "catch(e) {\n" +
                    "    console.log('The exception is probably caused by the fact that no asset requires the service registry');\n" +
                    "}")
                );
                return new CharResponseContent( brjs, readerList );
            }
            else {
                throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
            }
        }
        catch(Exception ex) {
            throw new ContentProcessingException(ex);
        }
    }
}
