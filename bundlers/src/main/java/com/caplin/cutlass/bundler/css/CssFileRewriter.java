package com.caplin.cutlass.bundler.css;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;

public class CssFileRewriter
{
	private final File file;
	
	public CssFileRewriter(File cssFile)
	{
		this.file = cssFile;
	}

	public String getFileContents() throws IOException, ContentFileProcessingException
	{
		try
		{
			String unprocessedCss = "";
			
			try(Reader fileReader = BundlerFileReaderFactory.getBundlerFileReader(file))
			{
				unprocessedCss = IOUtils.toString(fileReader);
			}
			
			return new CssUrlRewriter(file.getParentFile(), unprocessedCss).getCss();
		}
		catch(CssImageReferenceException cssImageReferenceException)
		{
			cssImageReferenceException.setCssFileContainingImageReference(file.getAbsolutePath());
			throw cssImageReferenceException;
		}
		catch(Exception e)
		{
			throw new ContentFileProcessingException(file, e, "Error while bundling file.");
		}
	}

}
