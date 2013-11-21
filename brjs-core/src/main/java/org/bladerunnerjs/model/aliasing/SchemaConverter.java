package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.translate.Formats;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

public class SchemaConverter {
	public static File convertToRng(String schemaPath) throws SchemaCreationException {
		String rngSchemaName = schemaPath.substring(schemaPath.lastIndexOf('/') + 1).replaceAll("\\.rnc$", ".rng");
		File rngOutputFile = new File(Files.createTempDir(), rngSchemaName);
		
		try {
			String rncFilePath = SchemaConverter.class.getClassLoader().getResource(schemaPath).toString().replace('\\', '/').replaceAll(" ","%20");
			InputFormat inputFormat = Formats.createInputFormat("rnc");
			OutputFormat outputFormat = Formats.createOutputFormat("rng");
			String[] inputOptions = {};
			SchemaCollection schemaCollection = inputFormat.load(rncFilePath, inputOptions, "rng", new ErrorHandlerImpl(), null);
			OutputDirectory outputDirectory = new LocalOutputDirectory(schemaCollection.getMainUri(), rngOutputFile, "rng", "UTF-8", 72, 2);
			String[] outputOptions = {};
			
			outputFormat.output(schemaCollection, outputDirectory, outputOptions, "rnc", new ErrorHandlerImpl());
		}
		catch(IOException | InputFailedException | InvalidParamsException | SAXException | OutputFailedException e) {
			throw new SchemaCreationException(e);
		}
		
		return rngOutputFile;
	}
}
