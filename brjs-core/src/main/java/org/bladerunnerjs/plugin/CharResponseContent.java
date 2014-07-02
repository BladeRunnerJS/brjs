package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;

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
		IOUtils.copy( reader, outputStream );
	}
	
}
