package org.bladerunnerjs.utility.reader.factory;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.XmlCommentStrippingDependenciesReader;

public class JsAndXmlCommentStrippingReaderFactory implements AssetReaderFactory {
	
	private Asset asset;

	public JsAndXmlCommentStrippingReaderFactory(Asset asset)
	{
		this.asset = asset;
	}
	
	@Override
	public Reader createReader(CharBufferPool pool) throws IOException {
		
		Reader jsCommentStrippingReader = new JsCommentStrippingReader(asset.getReader(), false, pool);
		Reader jsCommentStrippingAndXmlStrippingReader = new XmlCommentStrippingDependenciesReader(jsCommentStrippingReader, pool);
		
		return jsCommentStrippingAndXmlStrippingReader;
		
	}
}
