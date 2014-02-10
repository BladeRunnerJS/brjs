// class copied from <http://www.java2s.com/Code/Java/Development-Class/Unicodereader.htm>

package org.bladerunnerjs.utility;

import java.io.*;

/**
 * Reads away UNICODE Byte Order Mark on construction. See
 * http://www.unicode.org/unicode/faq/utf_bom.html
 * 
 * <pre>
 *  00 00 FE FF	= UTF-32, big-endian
 *  FF FE 00 00	= UTF-32, little-endian
 *  FE FF		  = UTF-16, big-endian
 *  FF FE		  = UTF-16, little-endian
 *  EF BB BF	   = UTF-8
 * </pre>
 */
public class UnicodeReader extends Reader
{
	private static final int BOM_MAX_SIZE = 4;
	
	private InputStreamReader delegate;
	
	public UnicodeReader(InputStream in, String defaultEnc) throws IOException
	{
		init(in, defaultEnc);
	}
	
	public UnicodeReader(InputStream in) throws IOException
	{
		this(in, null);
	}
	
	public UnicodeReader(File file, String defaultEnc) throws IOException {
		this(new BufferedInputStream(new FileInputStream(file)), defaultEnc);
	}
	
	/**
	 * Returns the encoding that was read from byte order mark if there was one.
	 */
	public String getEncoding()
	{
		return delegate.getEncoding();
	}
	
	/**
	 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
	 * back to the stream, only BOM bytes are skipped.
	 */
	private void init(InputStream in, String defaultEnc) throws IOException
	{
		String encoding;
		byte bom[] = new byte[BOM_MAX_SIZE];
		int n, unread;
		PushbackInputStream internalIn = new PushbackInputStream(in, BOM_MAX_SIZE);
		n = internalIn.read(bom, 0, bom.length);
		
		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
		{
			encoding = "UTF-8";
			unread = n - 3;
		}
		else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
		{
			encoding = "UTF-16BE";
			unread = n - 2;
		}
		else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
		{
			encoding = "UTF-16LE";
			unread = n - 2;
		}
		else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF))
		{
			encoding = "UTF-32BE";
			unread = n - 4;
		}
		else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00))
		{
			encoding = "UTF-32LE";
			unread = n - 4;
		}
		else
		{
			// Unicode BOM mark not found, unread all bytes
			encoding = defaultEnc;
			unread = n;
		}
		
		if (unread > 0)
			internalIn.unread(bom, (n - unread), unread);
		else if (unread < -1)
			internalIn.unread(bom, 0, 0);
		
		// Use BOM or default encoding
		if (encoding == null)
		{
			delegate = new InputStreamReader(internalIn);
		}
		else
		{
			delegate = new InputStreamReader(internalIn, encoding);
		}
	}
	
	@Override
	public boolean ready() throws IOException
	{
		return delegate.ready();
	}
	
	@Override
	public void close() throws IOException
	{
		delegate.close();
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return delegate.read(cbuf, off, len);
	}
}