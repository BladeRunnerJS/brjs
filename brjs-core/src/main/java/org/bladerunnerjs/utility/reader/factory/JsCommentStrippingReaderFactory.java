package org.bladerunnerjs.utility.reader.factory;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

public class JsCommentStrippingReaderFactory implements AssetReaderFactory {
	
	private Asset asset;

	public JsCommentStrippingReaderFactory(Asset asset)
	{
		this.asset = asset;
	}
	
	@Override
	public Reader createReader(CharBufferPool pool) throws IOException {
		Reader reader = null;
		if(asset instanceof AugmentedContentSourceModule){
			AugmentedContentSourceModule source = (AugmentedContentSourceModule)asset;
			reader = source.getUnalteredContentReader();
		}else{
			reader = asset.getReader();
		}
		return new JsCommentStrippingReader(reader, false, pool);
	}
}
