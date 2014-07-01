package org.bladerunnerjs.model;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AbstractContentPluginOutputTest extends SpecTest
{

	 @Rule
	 public ExpectedException exception = ExpectedException.none();

	
	private ContentPluginOutput contentPluginOutput;
	private OutputStream outputStream;

	@Before
	public void setup() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated();
		App app = brjs.app("test-app");
		this.outputStream = new ByteArrayOutputStream();
		this.contentPluginOutput = new StaticContentPluginOutput(app, outputStream);
	}
	
	@Test
	public void exceptionIsThrownIfGetOutputStreamUsedAfterSetReader() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.SET_READER_ALREAD_CALLED_MESSAGE );
		contentPluginOutput.setReader(new StringReader(""));
		contentPluginOutput.getOutputStream();
	}
	
	@Test
	public void exceptionIsThrownIfGetWriterUsedAfterSetReader() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.SET_READER_ALREAD_CALLED_MESSAGE );
		contentPluginOutput.setReader(new StringReader(""));
		contentPluginOutput.getWriter();
	}
	
	@Test
	public void exceptionIsThrownIfSetReaderUsedAfterGetOutputStream() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.GET_OUTPUT_STREAM_ALREADY_CALLED_MESSAGE );
		contentPluginOutput.getOutputStream();
		contentPluginOutput.setReader(new StringReader(""));
	}
	
	@Test
	public void exceptionIsThrownIfGetWriterUsedAfterGetOutputStream() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.GET_OUTPUT_STREAM_ALREADY_CALLED_MESSAGE );
		contentPluginOutput.getOutputStream();
		contentPluginOutput.getWriter();
	}
	
	@Test
	public void exceptionIsThrownIfSetReaderUsedAfterGetWriter() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.GET_WRITER_ALREADY_CALLED_MESSAGE );
		contentPluginOutput.getWriter();
		contentPluginOutput.setReader(new StringReader(""));
	}
	
	@Test
	public void exceptionIsThrownIfGetOutputStreamUsedAfterGetWriter() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage( ContentPluginOutput.Messages.GET_WRITER_ALREADY_CALLED_MESSAGE );
		contentPluginOutput.getWriter();
		contentPluginOutput.getOutputStream();
	}
	
}
