package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;


public abstract class ContentPluginOutput
{
	public class Messages {
		public static final String SET_READER_ALREAD_CALLED_MESSAGE = "setReader has already been called. Other output sources cannot now be set.";
		public static final String GET_OUTPUT_STREAM_ALREADY_CALLED_MESSAGE = "getOutputStream has already been called. Other output sources cannot now be set.";
		public static final String GET_WRITER_ALREADY_CALLED_MESSAGE = "getWriter has already been called. Other output sources cannot now be set.";
	}
	
	private OutputStream sourceOutputStream;
	private String encoding;
	
	private OutputStream outputStream = null;
	private Reader reader = null;
	private Writer writer = null;
	
	public ContentPluginOutput(OutputStream outputStream, String encoding) {
		this.sourceOutputStream = outputStream;
		this.encoding = encoding;
	}

	public OutputStream getOutputStream() {
		assertReaderNotSet();
		assertWriterNotSet();
		
		if (outputStream == null) { // do this lazily so it's easier to check which of the reader/outputstream/writer have been used
			outputStream = sourceOutputStream;
		}
		return outputStream;
	}

	public Reader getReader() {
		return reader;
	}

	public void setReader(Reader reader) {
		assertOutputStreamNotSet();
		assertWriterNotSet();
		
		this.reader = reader;
	}

	public Writer getWriter() {
		assertReaderNotSet();
		assertOutputStreamNotSet();
		
		if (writer == null) { // do this lazily so it's easier to check which of the reader/outputstream/writer have been used
			try {
				writer = new OutputStreamWriter(sourceOutputStream, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}			
		}
		return writer;
	}
	
	public void flush() {
		if(reader == null){
			return;
		}
		try {
			IOUtils.copy(reader, writer);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Write the contents of the reply from the given URL to the writer. Binary unsafe since it's assume the content is a String.
	 */
	public abstract void writeLocalUrlContentsToWriter(String urlPath, Writer writer) throws IOException;
	
	/**
	 * 
	 * Write the contents of the reply from the given URL to the output stream. A binary safe equivalent of writeLocalUrlContentsToWriter 
	 */
	public abstract void writeLocalUrlContentsToAnotherStream(String urlPath, OutputStream output) throws IOException;
	
	/**
	 * Behaves the same as writeLocalUrlContentsToAnotherStream, writing the contents to this OutputStream.
	 */
	public abstract void writeLocalUrlContents(String url) throws IOException;
	
	
	private void assertReaderNotSet() {
		if (reader != null) {
			throw new IllegalStateException(Messages.SET_READER_ALREAD_CALLED_MESSAGE);
		}
	}
	
	private void assertOutputStreamNotSet() {
		if (outputStream != null) {
			throw new IllegalStateException(Messages.GET_OUTPUT_STREAM_ALREADY_CALLED_MESSAGE);
		}
	}
	
	private void assertWriterNotSet() {
		if (writer != null) {
			throw new IllegalStateException(Messages.GET_WRITER_ALREADY_CALLED_MESSAGE);
		}
	}
	
}
