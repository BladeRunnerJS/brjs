package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;

// TODO: merge this class with CommonJsPostExportDefineTimeDependenciesReader
public class CommonJsPreExportDefineTimeDependenciesReader extends Reader 
{

	private Reader preExportDefineTimeDependencesReader;

	public CommonJsPreExportDefineTimeDependenciesReader(CommonJsSourceModule sourceModule) throws IOException {
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false);
		Reader codeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader);
		preExportDefineTimeDependencesReader = new JsModuleExportsStrippingReader(codeBlockStrippingReader, true);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return preExportDefineTimeDependencesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		preExportDefineTimeDependencesReader.close();
	}
	
}