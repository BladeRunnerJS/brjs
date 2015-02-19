package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.XmlCommentStrippingDependenciesReader;

public class LinkedFileAssetDependenciesReader extends Reader
{

	private XmlCommentStrippingDependenciesReader linkedFileAssetDependenciesReader;


	public LinkedFileAssetDependenciesReader(Asset asset) throws IOException
	{
		CharBufferPool pool = asset.assetLocation().root().getCharBufferPool();
		Reader jsCommentStrippingReader = new JsCommentStrippingReader(asset.getReader(), false, pool);
		linkedFileAssetDependenciesReader = new XmlCommentStrippingDependenciesReader(jsCommentStrippingReader, pool);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return linkedFileAssetDependenciesReader.read(cbuf, off, len);
	}


	@Override
	public void close() throws IOException
	{
		linkedFileAssetDependenciesReader.close();
	}
	
	static class Factory implements AssetReaderFactory {
		
		private Asset asset;

		public Factory(Asset asset)
		{
			this.asset = asset;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new LinkedFileAssetDependenciesReader(asset);
		}
	}
	
}
