package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FileUtility;


public class MemoizedFile extends File
{
	private static final long serialVersionUID = 7406703034536312889L;
	private MemoizedValue<Boolean> exists;
	private MemoizedValue<Boolean> isDirectory;
	private MemoizedValue<Boolean> isFile;
	private MemoizedValue<List<MemoizedFile>> filesAndDirs;
	private RootNode rootNode;
	private MemoizedFile canonicalFile;
	private File superFile;
	private String name;
	private MemoizedFile parentFile;
	
	MemoizedFile(RootNode rootNode, String file) {
		super( FileUtility.getCanonicalFileWhenPossible( new File(file) ).getAbsolutePath() );
		this.rootNode = rootNode;
		superFile = FileUtility.getCanonicalFileWhenPossible( new File(file) );
			// ^^ use composition so we don't have a chicken and egg problem when trying to read memoized files but we're forced to extend java.io.File since its not an interface
		
		String className = this.getClass().getSimpleName();
		exists = new MemoizedValue<>(className+".exists", rootNode, this);
		isDirectory = new MemoizedValue<>(className+".isDirectory", rootNode, this);
		isFile = new MemoizedValue<>(className+".isFile", rootNode, this);
		filesAndDirs = new MemoizedValue<>(className+".filesAndDirs", rootNode, this);
	}	
	
	// ---- Methods Using Memoized Values ----
	
//	@Override
//	public String getName()
//	{
//		if (name == null) {
//			name = superFile.getName();
//		}
//		return name;
//	}
//	
//	@Override
//	public boolean exists() {
//		return exists.value(() -> {
//			return superFile.exists();
//		});
//	}
//	
//	@Override
//	public boolean isDirectory() {
//		return isDirectory.value(() -> {
//			return superFile.isDirectory();
//		});
//	}
//	
//	@Override
//	public boolean isFile() {
//		return isFile.value(() -> {
//			return superFile.isFile();
//		});
//	}
	
	public List<MemoizedFile> filesAndDirs() {
		List<MemoizedFile> returnedFilesAndDirsCopy = new ArrayList<>();
		List<MemoizedFile> foundFilesAndDirs = filesAndDirs.value(() -> {
			List<MemoizedFile> returnedFilesAndDirs = new ArrayList<>();
			
			if (!exists()) {
				return Collections.emptyList();
			}
			
			for (File file : superFile.listFiles()) {
				returnedFilesAndDirs.add( rootNode.getMemoizedFile(file) );
			}
			Collections.sort(returnedFilesAndDirs);
			return returnedFilesAndDirs;
		});
		returnedFilesAndDirsCopy.addAll(foundFilesAndDirs);
		return returnedFilesAndDirsCopy; // return a copy so multiple callers dont have the same object by reference
	}
	
	// ---- End Methods Using Memoized Values ----
	
	@Override
	public MemoizedFile getCanonicalFile()
	{
//		if (canonicalFile == null) {
//			try {
//				canonicalFile = rootNode.getMemoizedFile(superFile.getCanonicalFile());
//			} catch (IOException e) {
//				rootNode.logger(this.getClass()).warn("Unable to calculate canonical path for path '%s'.", getPath());
//				canonicalFile = rootNode.getMemoizedFile(super.getAbsoluteFile());
//			}
//		}
//		return canonicalFile;
		return rootNode.getMemoizedFile( FileUtility.getCanonicalFileWhenPossible(superFile) );
	}
	
	@Override
	public String getCanonicalPath()
	{
		return getCanonicalFile().getAbsolutePath();
	}
	
	@Override
	public MemoizedFile getParentFile()
	{
//		if (parentFile == null) {
//			parentFile = rootNode.getMemoizedFile( superFile.getParentFile() );
//		}
//		return parentFile;
		return rootNode.getMemoizedFile( superFile.getParentFile() );
	}
//	
//	@Override
//	public MemoizedFile[] listFiles(FileFilter filter) {
//		List<MemoizedFile> listedFiles = new ArrayList<>();
//		for (MemoizedFile file : filesAndDirs()) {
//			if (file.isDirectory() && filter.accept(file)) {
//				listedFiles.add(file);
//			}
//		}
//		return listedFiles.toArray(new MemoizedFile[0]);
//	}
//	
//	@Override
//	public MemoizedFile[] listFiles(FilenameFilter filter) {
//		return listFiles( (FileFilter) FileFilterUtils.asFileFilter(filter) );
//	}
//	
//	@Override
//	public MemoizedFile[] listFiles() {
//		return listFiles( (FileFilter) TrueFileFilter.INSTANCE);
//	}
//	
//	@Override
//	public String[] list(FilenameFilter filter) {
//		List<String> listedNames = new ArrayList<>();
//		for (MemoizedFile file : filesAndDirs()) {
//			if (file.isDirectory() && filter.accept(file.getParentFile(), file.getName())) {
//				listedNames.add(file.getName());
//			}
//		}
//		return listedNames.toArray(new String[0]);
//	}
//	
//	@Override
//	public String[] list() {
//		return list( TrueFileFilter.INSTANCE );
//	}	
	
	public List<MemoizedFile> filesAndDirs(IOFileFilter fileFilter) {
		List<MemoizedFile> returnedFilesAndDirsCopy = new ArrayList<>();
		for (MemoizedFile file : filesAndDirs()) {
			if (fileFilter.accept(file)) {
				returnedFilesAndDirsCopy.add(file);
			}
		}
		return returnedFilesAndDirsCopy;
	}
	
	public List<MemoizedFile> files() {
		return filesAndDirs(FileFileFilter.FILE);
	}
	
	public List<MemoizedFile> dirs() {
		return filesAndDirs(DirectoryFileFilter.DIRECTORY);
	}
	
	public List<MemoizedFile> nestedFilesAndDirs() {
		List<MemoizedFile> nestedFilesAndDirs = new ArrayList<>();
		populateNestedFilesAndDirs(this, nestedFilesAndDirs);
		return nestedFilesAndDirs;
	}
	
	public List<MemoizedFile> nestedFiles() {
		List<MemoizedFile> nestedFiles = new ArrayList<>();
		for(MemoizedFile file : nestedFilesAndDirs()) {
			if(!file.isDirectory()) {
				nestedFiles.add(file);
			}
		}
		return nestedFiles;
	}
	
	public List<MemoizedFile> nestedDirs() {
		List<MemoizedFile> nestedDirs = new ArrayList<>();
		for(MemoizedFile file : nestedFilesAndDirs()) {
			if(file.isDirectory()) {
				nestedDirs.add(file);
			}
		}
		return nestedDirs;
	}	
	
	
	private void populateNestedFilesAndDirs(MemoizedFile file, List<MemoizedFile> nestedFilesAndDirs) {
		nestedFilesAndDirs.addAll(file.filesAndDirs());
		
		for(MemoizedFile dir : file.dirs()) {
			MemoizedFile memoizedFile = rootNode.getMemoizedFile(dir);
			populateNestedFilesAndDirs(memoizedFile, nestedFilesAndDirs);
		}
	}
	
}
