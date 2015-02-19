package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.RootNode;

/**
 * Provides similar methods to {@link File} and wraps a {@link File} object. Several of the methods' return values are 'memoized'
 * and only regenerated if properties on the underlying file on disk change. Changes are detected by using {@link MemoizedValue} and 
 * the {@link FileModificationRegistry}.
 *
 */
public class MemoizedFile extends File implements Comparable<File>
{
	private static final long serialVersionUID = 7406703034536312889L;
	private RootNode rootNode;
	private File wrappedFile;
	private String name;
	private MemoizedFile parentFile;
	
	private MemoizedValue<Boolean> isFile;
	private MemoizedValue<Boolean> isDirectory;
	private MemoizedValue<Boolean> exists;
	private MemoizedValue<List<MemoizedFile>> filesAndDirs;
	
	
	MemoizedFile(RootNode rootNode, String file) {
		super( file );
		this.rootNode = rootNode;
		wrappedFile = new File( FilenameUtils.normalize(file) );
			// ^^ use composition so we don't have a chicken and egg problem when trying to read memoized files but we're forced to extend java.io.File since its not an interface
		
		String className = this.getClass().getSimpleName();
		isFile = new MemoizedValue<>(className+"_"+wrappedFile.getAbsolutePath()+".isFile", rootNode, this);
		isDirectory = new MemoizedValue<>(className+"_"+wrappedFile.getAbsolutePath()+".isDirectory", rootNode, this);
		exists = new MemoizedValue<>(className+"_"+wrappedFile.getAbsolutePath()+".exists", rootNode, this);
		filesAndDirs = new MemoizedValue<>(className+"_"+wrappedFile.getAbsolutePath()+".filesAndDirs", rootNode, this);
	}	
	
	// ---- Methods Using Memoized Values ----
	
	public String getName()
	{
		if (name == null) {
			name = wrappedFile.getName();
		}
		return name;
	}
	
	public String getAbsolutePath() {
		return wrappedFile.getAbsolutePath();
	}
	
	public boolean exists() {
		return exists.value(() -> {
			return wrappedFile.exists();
		});
	}
	
	public boolean isDirectory() {
		return isDirectory.value(() -> {
			return wrappedFile.isDirectory();
		});
	}
	
	public boolean isFile() {
		return isFile.value(() -> {
			return wrappedFile.isFile();
		});
	}
	
	public List<MemoizedFile> filesAndDirs() {		
		List<MemoizedFile> filesAndDirsList = filesAndDirs.value(() -> {
			if (!wrappedFile.isDirectory()) {
				return Collections.emptyList();
			}
			List<File> listedFiles = Arrays.asList(wrappedFile.listFiles());
			Collections.sort(listedFiles);
			List<MemoizedFile> memoizedFileList = new ArrayList<>();
			for (File file : listedFiles) {
				memoizedFileList.add( rootNode.getMemoizedFile(file) );
			}
			return memoizedFileList;
		});
		
		List<MemoizedFile> wrappedFilesAndDirs = new ArrayList<>();
		wrappedFilesAndDirs.addAll( filesAndDirsList ); // return a copy so multiple callers dont have the same object by reference
		return wrappedFilesAndDirs;
		
	}
	
	// ---- End Methods Using Memoized Values ----
	
	public MemoizedFile getAbsoluteFile() {
		return rootNode.getMemoizedFile( super.getAbsoluteFile() );
	}
	
	
	public MemoizedFile getParentFile()
	{
		if (parentFile == null) {
			parentFile = rootNode.getMemoizedFile( wrappedFile.getParentFile() );
		}
		return parentFile;
	}
	
	public MemoizedFile[] listFiles(FileFilter filter) {
		List<MemoizedFile> listedFiles = new ArrayList<>();
		for (MemoizedFile file : filesAndDirs()) {
			if (filter.accept(file.wrappedFile)) {
				listedFiles.add(file);
			}
		}
		return listedFiles.toArray(new MemoizedFile[0]);
	}
	
	public MemoizedFile[] listFiles(FilenameFilter filter) {
		return listFiles( (FileFilter) FileFilterUtils.asFileFilter(filter) );
	}
	
	public MemoizedFile[] listFiles() {
		return listFiles( (FileFilter) TrueFileFilter.INSTANCE);
	}
	
	public String[] list(FilenameFilter filter) {
		List<String> listedNames = new ArrayList<>();
		for (MemoizedFile file : filesAndDirs()) {
			if (filter.accept(file.getParentFile().wrappedFile, file.getName())) {
				listedNames.add(file.getName());
			}
		}
		return listedNames.toArray(new String[0]);
	}
	
	public String[] list() {
		return list( TrueFileFilter.INSTANCE );
	}
	
	public List<MemoizedFile> filesAndDirs(IOFileFilter fileFilter) {
		List<MemoizedFile> returnedFilesAndDirsCopy = new ArrayList<>();
		for (MemoizedFile file : filesAndDirs()) {
			if (fileFilter.accept(file.wrappedFile)) {
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
			if(file.isFile()) {
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
	
	public MemoizedFile file(String childPath)
	{
		return rootNode.getMemoizedFile(this, childPath);
	}
	
	public File getUnderlyingFile()
	{
		return wrappedFile;
	}
	
	public String getRelativePath(MemoizedFile childFile) {
		return MemoizedFileRelativePathUtility.getRelativePath(this, childFile);
	}
	
	public void incrementFileVersion()
	{
    	rootNode.getFileModificationRegistry().incrementFileVersion( wrappedFile );
	}
	
	public void incrementChildFileVersions()
	{
		rootNode.getFileModificationRegistry().incrementChildFileVersions( wrappedFile );
	}
	
	@Override
	public boolean mkdir() {
		boolean returnVal = super.mkdir();
		incrementFileVersion();
		return returnVal;
	}
	
	@Override
	public boolean mkdirs() {
		boolean returnVal = super.mkdirs();
		incrementFileVersion();
		return returnVal;
	}
	
	@Override
	public boolean createNewFile() throws IOException {
		boolean returnVal = super.createNewFile();
		incrementFileVersion();
		return returnVal;
	}
	
	@Override
	public boolean delete() {
		boolean returnVal = super.delete();
		incrementChildFileVersions();
		return returnVal;
	}
	
	@Override
	public boolean renameTo(File dest) {
		boolean returnVal = super.renameTo(dest);
		incrementChildFileVersions();
		rootNode.getMemoizedFile(dest).incrementChildFileVersions();
		return returnVal;
	}
	
	// -- Private Stuff --
	
	private void populateNestedFilesAndDirs(MemoizedFile file, List<MemoizedFile> nestedFilesAndDirs) {
		nestedFilesAndDirs.addAll(file.filesAndDirs());
		
		for(MemoizedFile dir : file.dirs()) {
			populateNestedFilesAndDirs(dir, nestedFilesAndDirs);
		}
	}

	public boolean isEmpty() {
		return filesAndDirs().isEmpty();
	}
	
}
