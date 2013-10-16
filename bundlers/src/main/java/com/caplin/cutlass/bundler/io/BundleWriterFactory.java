package com.caplin.cutlass.bundler.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.caplin.cutlass.EncodingAccessor;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class BundleWriterFactory
{
	public static Writer createWriter(OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = null;
		
		try
		{
			writer = new OutputStreamWriter(outputStream, EncodingAccessor.getDefaultOutputEncoding());
		}
		catch (UnsupportedEncodingException e)
		{
			throw new BundlerProcessingException(e, "'" + EncodingAccessor.getDefaultOutputEncoding() + "' is not a supported character encoding.");
		}
		
		return writer;
	}
	
	public static void closeWriter(Writer writer) throws BundlerProcessingException
	{
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			throw new BundlerProcessingException(e, "Unable to close ouput writer.");
		}
	}
}
