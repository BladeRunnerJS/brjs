package com.caplin.cutlass.bundler.js.minification;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.caplin.cutlass.EncodingAccessor;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.google.common.io.Files;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.SourceMap.DetailLevel;
import com.google.javascript.jscomp.SourceMap.Format;

public abstract class AbstractClosureCompilerMinifier implements Minifier
{
	protected CompilerOptions compilerOptions = new CompilerOptions();
	private Compiler compiler;
	private Result result;
	
	public AbstractClosureCompilerMinifier()
	{
		compilerOptions.setSourceMapOutputPath("map_js.bundle"); // TODO: should this be a temp file?
		compilerOptions.setSourceMapFormat(Format.V3);
		compilerOptions.setSourceMapDetailLevel(DetailLevel.ALL);
	}
	
	@Override
	public void minifySourceCode(List<File> sourceFiles, Writer outputWriter, int offsetLine, int offsetIndex) throws IOException
	{
		compiler = new Compiler();
		List<File> jsFilesWhichCannotBeMinified = getAndRemoveJsFilesWhichCannotBeMinifiedFromListOfFilesToMinify(sourceFiles);
		result = compiler.compile(new ArrayList<SourceFile>(), convertSourceFiles(sourceFiles), compilerOptions);
		result.sourceMap.setStartingPosition(offsetLine, offsetIndex);
		String minifiedSource = compiler.toSource();
		String preMinifiedSource = concatUnminifiableJsFiles(jsFilesWhichCannotBeMinified);
		
		outputWriter.write(minifiedSource);
		outputWriter.write(preMinifiedSource + "\n");
	}
	
	@Override
	public void writeSourceMap(Writer outputWriter) throws IOException
	{
		compiler.getSourceMap().appendTo(outputWriter, "js.bundle");
	}
	
	private List<SourceFile> convertSourceFiles(List<File> sourceFiles) throws IOException
	{
		List<SourceFile> convertedSourceFiles = new ArrayList<SourceFile>();
		
		for(File sourceFile : sourceFiles)
		{
			try(Reader fileReader = BundlerFileReaderFactory.getBundlerFileReader(sourceFile))
			{
				convertedSourceFiles.add(SourceFile.fromReader(sourceFile.getPath(), fileReader));
			}
		}
		
		return convertedSourceFiles;
	}

	private String concatUnminifiableJsFiles(List<File> jsFilesWhichCannotBeMinified) throws IOException{
		StringBuffer concatenatedFiles = new StringBuffer(); 
		
		for (File file : jsFilesWhichCannotBeMinified)
		{
			concatenatedFiles.append(readFile(file));
		}
		
		return concatenatedFiles.toString();
	}
	
	private String readFile(File file) throws IOException{
		String charsetName = EncodingAccessor.getDefaultOutputEncoding();
		
		return Files.toString(file, Charset.forName(charsetName));
	}

	private List<File> getAndRemoveJsFilesWhichCannotBeMinifiedFromListOfFilesToMinify(List<File> sourceFiles) {
		
		List<File> removedFiles = new ArrayList<File>();
		
		for (File file : sourceFiles)
		{
			if (file.getName().startsWith("ext-"))
			{
				removedFiles.add(file);
			}
		}

		sourceFiles.remove(removedFiles);
		return removedFiles;
	}

}