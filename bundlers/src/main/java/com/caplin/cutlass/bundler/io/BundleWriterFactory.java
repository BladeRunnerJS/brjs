package com.caplin.cutlass.bundler.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class BundleWriterFactory
{
	public static Writer createWriter(OutputStream outputStream) throws ContentProcessingException
	{
		Writer writer = null;
		
		try
		{
			writer = new OutputStreamWriter(outputStream, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ContentProcessingException(e, "Unable to create OutputStreamWriter.");
		}
		
		return writer;
	}
	
	public static void closeWriter(Writer writer) throws ContentProcessingException
	{
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			throw new ContentProcessingException(e, "Unable to close ouput writer.");
		}
	}
}
