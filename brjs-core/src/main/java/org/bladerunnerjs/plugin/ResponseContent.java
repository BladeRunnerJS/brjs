package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.OutputStream;


public interface ResponseContent
{
	public void write(OutputStream outputStream) throws IOException;	
}
