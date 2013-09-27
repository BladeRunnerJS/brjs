package com.caplin.cutlass.bundler.js.minification;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.caplin.cutlass.bundler.BundlerFileUtils;

public class ConcatenatingMinifier implements Minifier
{
	@Override
	public void minifySourceCode(List<File> sourceFiles, Writer outputWriter, int offsetLine, int offsetIndex) throws IOException
	{
		BundlerFileUtils.writeBundle(sourceFiles, outputWriter);
	}
	
	@Override
	public void writeSourceMap(Writer outputWriter)
	{
		// TODO
	}
}
