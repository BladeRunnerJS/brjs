package org.bladerunnerjs.memoization;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;

public class MemoizedFile extends File
{
	private static final long serialVersionUID = 7406703034536312889L;
	private MemoizedValue<Boolean> exists;
	private MemoizedValue<Boolean> isDirectory;
	private MemoizedValue<Boolean> isFile;
	private MemoizedValue<File[]> listFiles;
	
	public MemoizedFile(RootNode rootNode, String file) {
		super(file);
		String className = this.getClass().getSimpleName();
		exists = new MemoizedValue<>(className+".exists", rootNode, this);
		isDirectory = new MemoizedValue<>(className+".isDirectory", rootNode, this);
		isFile = new MemoizedValue<>(className+".isFile", rootNode, this);
		listFiles = new MemoizedValue<>(className+".listFiles", rootNode, this);
	}
	
	public MemoizedFile(RootNode rootNode, File file)
	{
		this( rootNode, file.getAbsolutePath() );		
	}
	
	public MemoizedFile(RootNode rootNode, File parent, String child)
	{
		this( rootNode, new File(parent, child).getAbsolutePath() );		
	}
	
	public boolean exists() {
		return exists.value(() -> {
			return super.exists();
		});
	}
	
	public boolean isDirectory() {
		return isDirectory.value(() -> {
			return super.isDirectory();
		});
	}
	
	public boolean isFile() {
		return isFile.value(() -> {
			return super.isFile();
		});
	}
	
	public File[] listFiles() {
		return listFiles.value(() -> {
			return super.listFiles();
		});
	}
	
	public String[] list() {
		File[] files = listFiles();
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			names[i] = files[i].getName();
		}
		return names;
	}
	
}
