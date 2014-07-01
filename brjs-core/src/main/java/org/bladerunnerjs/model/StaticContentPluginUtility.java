package org.bladerunnerjs.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.ContentPluginUtility;


public class StaticContentPluginUtility extends ContentPluginUtility
{

	private App app;

	public StaticContentPluginUtility(App app)
	{
		this.app = app;
	}
	
	@Override
	public void writeLocalUrlContentsToWriter(String urlPath, Writer writer) throws IOException {		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writeLocalUrlContentsToOutputStream(urlPath, output);
		writer.write(output.toString());
		writer.flush();
	}
	
	@Override
	public void writeLocalUrlContentsToOutputStream(String urlPath, OutputStream output) throws IOException {		
		File requestPathFile = app.file(urlPath);
		try (InputStream fileInput = new FileInputStream(requestPathFile)) {
			IOUtils.copy(fileInput, output);
		}
	}
	
}
