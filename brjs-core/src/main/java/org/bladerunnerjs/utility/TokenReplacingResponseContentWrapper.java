package org.bladerunnerjs.utility;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.appserver.util.NoTokenReplacementHandler;
import org.bladerunnerjs.appserver.util.TokenReplacingReader;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.*;

public class TokenReplacingResponseContentWrapper implements ResponseContent {

    private final ResponseContent wrappedResponse;
    private final TokenFinder tokenFinder;
    private final NoTokenReplacementHandler replacementHandler;
	private final TokenFinder brjsTokenFinder;

    public TokenReplacingResponseContentWrapper(ResponseContent wrappedResponse, TokenFinder brjsTokenFinder, TokenFinder tokenFinder, NoTokenReplacementHandler replacementHandler) {
    	this.brjsTokenFinder = brjsTokenFinder;
        this.wrappedResponse = wrappedResponse;
        this.tokenFinder = tokenFinder;
        this.replacementHandler = replacementHandler;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        ByteArrayOutputStream wrappedContentByteOutput = new ByteArrayOutputStream();
        wrappedResponse.write( wrappedContentByteOutput );

        Reader reader = new InputStreamReader(new ByteArrayInputStream(wrappedContentByteOutput.toByteArray()));
        Reader tokenReplacingReader = new TokenReplacingReader(brjsTokenFinder, tokenFinder, reader, replacementHandler);
        IOUtils.copy(tokenReplacingReader, outputStream);
    }

    @Override
    public void close() {
        wrappedResponse.close();
    }
}
