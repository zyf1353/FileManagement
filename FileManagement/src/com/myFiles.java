package com;

import java.io.File;


public class myFiles {
    private int blockName;
    private File myFile;
    private String fileName;
    public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    long space;

    public myFiles(File myFile, int blockName){
        space = myFile.length();
        this.myFile = myFile;
        this.blockName = blockName;
        fileName = myFile.getName();
    }

    public String getFileName(){
        return myFile.getName();
    }

    public String getFilePath(){
        return myFile.toString();
    }

    public File getMyFile(){
        return myFile;
    }

    public int getBlockName() {
        return blockName;
    }

    public double getSpace() {
        return space;
    }

    @Override
    public String toString(){
        return fileName;
    }
}
