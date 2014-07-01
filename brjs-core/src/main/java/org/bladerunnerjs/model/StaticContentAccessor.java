package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.UrlContentAccessor;


public class StaticContentAccessor extends UrlContentAccessor
{

	private App app;

	public StaticContentAccessor(App app)
	{
		this.app = app;
	}
	
	@Override
	public void writeLocalUrlContentsToOutputStream(String urlPath, OutputStream output) throws IOException {		
		File requestPathFile = app.file(urlPath);
		try (InputStream fileInput = new FileInputStream(requestPathFile)) {
			IOUtils.copy(fileInput, output);
		}
	}
	
}
