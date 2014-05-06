package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.Asset;

public class JsCommentStrippingReaderFactory implements AssetReaderFactory {
	
	private Asset asset;

	public JsCommentStrippingReaderFactory(Asset asset)
	{
		this.asset = asset;
	}
	
	@Override
	public Reader createReader() throws IOException {
		return new JsCommentStrippingReader(asset.getReader(), false);
	}
}
