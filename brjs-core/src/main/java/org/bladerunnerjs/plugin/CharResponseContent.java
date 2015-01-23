package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BladerunnerConf;

import com.Ostermiller.util.ConcatReader;


public class CharResponseContent implements ResponseContent
{

	private Reader reader;
	
	public CharResponseContent(BRJS brjs, Reader reader) {
		this.reader = reader;
	}
	
	public CharResponseContent(App app, Reader reader) {
		this(app.root(), reader);
	}
	
	public CharResponseContent(BRJS brjs, String content) {
		this(brjs, new StringReader(content));
	}
	
	public CharResponseContent(BRJS brjs, Reader... readers) {
		this(brjs, new ConcatReader(readers));
	}
	
	public CharResponseContent(BRJS brjs, List<Reader> readers) {
		this(brjs, readers.toArray(new Reader[0]));
	}
	
	public Reader getReader() {
		return reader;
	}

	@Override
	public void write(OutputStream outputStream) throws IOException
	{
		IOUtils.copy( reader, outputStream, BladerunnerConf.OUTPUT_ENCODING );
		outputStream.flush();
	}

	@Override
	public void close()
	{
		try
		{
			reader.close();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
}
