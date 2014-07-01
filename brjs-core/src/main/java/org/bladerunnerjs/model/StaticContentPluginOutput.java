package org.bladerunnerjs.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.exception.ConfigException;


public class StaticContentPluginOutput extends ContentPluginOutput
{

	private App app;

	public StaticContentPluginOutput(App app, OutputStream outputStream)
	{
		super(outputStream, getEncoding(app));
		this.app = app;
	}
	
	private static String getEncoding(App app){
		try {
			return app.root().bladerunnerConf().getBrowserCharacterEncoding();
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public StaticContentPluginOutput(App app, File file) throws IOException
	{
		super( new FileOutputStream(file),getEncoding(app) );
		this.app = app;
	}
	
	@Override
	public void writeLocalUrlContentsToWriter(String urlPath, Writer writer) throws IOException {		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		writeLocalUrlContentsToAnotherStream(urlPath, output);
		writer.write(output.toString());
		writer.flush();
	}
	
	@Override
	public void writeLocalUrlContentsToAnotherStream(String urlPath, OutputStream output) throws IOException {		
		File requestPathFile = app.file(urlPath);
		try (InputStream fileInput = new FileInputStream(requestPathFile)) {
			IOUtils.copy(fileInput, output);
		}
	}
	
	@Override
	public void writeLocalUrlContents(String url) throws IOException {
		writeLocalUrlContentsToAnotherStream( url, this.getOutputStream() );
	}
	
}
