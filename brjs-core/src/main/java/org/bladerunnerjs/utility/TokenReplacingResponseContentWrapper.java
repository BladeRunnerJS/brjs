package org.bladerunnerjs.utility;

import org.bladerunnerjs.api.plugin.ResponseContent;

import java.io.IOException;
import java.io.OutputStream;

public class TokenReplacingResponseContentWrapper implements ResponseContent {

    private final ResponseContent wrappedResponse;

    public TokenReplacingResponseContentWrapper(ResponseContent wrappedResponse) {
        this.wrappedResponse = wrappedResponse;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        wrappedResponse.write(outputStream);
    }

    @Override
    public void close() {
        wrappedResponse.close();
    }
}
