package org.bladerunnerjs.utility;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.appserver.util.TokenReplacingReader;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.*;

public class TokenReplacingResponseContentWrapper implements ResponseContent {

    private final ResponseContent wrappedResponse;
    private final TokenFinder tokenFinder;
    private final boolean ignoreFailedReplacements;

    public TokenReplacingResponseContentWrapper(ResponseContent wrappedResponse, TokenFinder tokenFinder, boolean ignoreFailedReplacements) {
        this.wrappedResponse = wrappedResponse;
        this.tokenFinder = tokenFinder;
        this.ignoreFailedReplacements = ignoreFailedReplacements;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        ByteArrayOutputStream wrappedContentByteOutput = new ByteArrayOutputStream();
        wrappedResponse.write( wrappedContentByteOutput );

        Reader reader = new InputStreamReader(new ByteArrayInputStream(wrappedContentByteOutput.toByteArray()));
        Reader tokenReplacingReader = new TokenReplacingReader(tokenFinder, reader, ignoreFailedReplacements);
        IOUtils.copy(tokenReplacingReader, outputStream);
    }

    @Override
    public void close() {
        wrappedResponse.close();
    }
}
