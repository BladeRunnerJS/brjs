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
import org.bladerunnerjs.model.ContentOutputStream;


public class StaticContentOutputStream extends ContentOutputStream
{

	private App app;

	public StaticContentOutputStream(App app, OutputStream outputStream) throws IOException
	{
		super(outputStream);
		this.app = app;
	}
	
	public StaticContentOutputStream(App app, File file) throws IOException
	{
		super( new FileOutputStream(file) );
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
		writeLocalUrlContentsToAnotherStream( url, this );
	}
	
}
