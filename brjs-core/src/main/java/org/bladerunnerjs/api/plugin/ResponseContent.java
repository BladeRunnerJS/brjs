package org.bladerunnerjs.api.plugin;

import java.io.IOException;
import java.io.OutputStream;


public interface ResponseContent extends AutoCloseable
{
	public void write(OutputStream outputStream) throws IOException;
	public void close();
}
