package org.bladerunnerjs.utility;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.plugin.BinaryResponseContent;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.appserver.util.MissingTokenHandler;
import org.bladerunnerjs.appserver.util.TokenReplacingReader;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.*;

public class TokenReplacingResponseContentWrapper implements ResponseContent {

    private final ResponseContent wrappedResponse;
    private final TokenFinder tokenFinder;
    private final MissingTokenHandler replacementHandler;
	private final TokenFinder brjsTokenFinder;
	private App app;

    public TokenReplacingResponseContentWrapper(App app, ResponseContent wrappedResponse, TokenFinder brjsTokenFinder, TokenFinder tokenFinder, MissingTokenHandler replacementHandler) {
    	this.app = app;
    	this.brjsTokenFinder = brjsTokenFinder;
        this.wrappedResponse = wrappedResponse;
        this.tokenFinder = tokenFinder;
        this.replacementHandler = replacementHandler;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
    	if (wrappedResponse instanceof TokenReplacingResponseContentWrapper || wrappedResponse instanceof BinaryResponseContent) {
    		wrappedResponse.write(outputStream);
		} else {
            ByteArrayOutputStream wrappedContentByteOutput = new ByteArrayOutputStream();
            wrappedResponse.write( wrappedContentByteOutput );
    
            Reader reader = new InputStreamReader(new ByteArrayInputStream(wrappedContentByteOutput.toByteArray()));
            Reader tokenReplacingReader = new TokenReplacingReader(app.getName(), brjsTokenFinder, tokenFinder, reader, replacementHandler);
            IOUtils.copy(tokenReplacingReader, outputStream, BladerunnerConf.OUTPUT_ENCODING);
		}
    }

    @Override
    public void close() {
        wrappedResponse.close();
    }
}
