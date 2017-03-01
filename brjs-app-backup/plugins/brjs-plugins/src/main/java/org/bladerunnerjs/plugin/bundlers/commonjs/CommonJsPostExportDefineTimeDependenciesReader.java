package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;


public class CommonJsPostExportDefineTimeDependenciesReader extends Reader 
{

	private Reader postExportDefineTimeDependencesReader;

	public CommonJsPostExportDefineTimeDependenciesReader(DefaultCommonJsSourceModule sourceModule) throws IOException {
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.assetContainer().root(), sourceReader, false);
		Reader codeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(sourceModule.assetContainer().root(), commentStrippingReader);
		postExportDefineTimeDependencesReader = new JsModuleExportsStrippingReader(sourceModule.assetContainer().root(), codeBlockStrippingReader, false);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return postExportDefineTimeDependencesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		postExportDefineTimeDependencesReader.close();
	}
	
}