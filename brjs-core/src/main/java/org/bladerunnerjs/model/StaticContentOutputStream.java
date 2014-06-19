package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public String getLocalUrlContents(String urlPath) throws IOException {		
		File requestPathFile = app.file(urlPath);
		try (InputStream fileInput = new FileInputStream(requestPathFile)) {
			return IOUtils.toString(fileInput);
		}
	}
	
	@Override
	public void writeLocalUrlContents(String url) throws IOException {
		IOUtils.write( getLocalUrlContents(url), this );
	}
	
}
