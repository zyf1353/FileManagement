package com;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Block {
    private int blockName;
    private File blockFile;
    private int fileNum;
    private ArrayList<File> files = new ArrayList<File>();

    public Block(int name, File file){
        this.blockName = name;
        this.blockFile = file;
        fileNum = 0;
        blockFile.mkdir();
    }

    public File getBlockFile(){
        return blockFile;
    }

    public void putFCB(File file) throws IOException {
        FileWriter newFileWriter = new FileWriter(file);
        newFileWriter.write("File\r\n");
        newFileWriter.write("capacity: " + file.length() + "\r\n");
        newFileWriter.write("Name: " + file.getName() + "\r\n");
        newFileWriter.write("Path: " + file.getPath() + "\r\n");
        String ctime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));
        newFileWriter.write("Date last updated: " + ctime + "\r\n");
        newFileWriter.write("--------------------------edit file blew ------------------------------\r\n");
        newFileWriter.close();
    }

    public void createFile(File file) throws IOException{
        files.add(file);
        file.createNewFile();          
        putFCB(file);
    }

    public boolean deleteFile(File file){
    	if (file.getName().equals("C") || file.getName().equals("D") || file.getName().equals("E") || file.getName().equals("F")){
            JOptionPane.showMessageDialog(null, "The dir is protected!!", "Access fail", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try{
            if (file.isFile()){
                try {
                    file.delete();
                }catch (Exception e){
                    e.printStackTrace();
                }
                fileNum--;             
                for (int i = 0; i < files.size(); i++){
                    if (files.get(i).getName().equals(file.getName())){
                        files.remove(i);
                        break;
                    }
                }
            }else{
                File [] files = file.listFiles();
                for(File myFile : files){
                    deleteFile(myFile);
                }
                while(file.exists()) {
                    file.delete();
                }
            }
            return true;
        }catch (Exception e){
            System.out.println("fail");
            return false;
        }
    }

    public boolean renameFile(File file, String name) throws IOException {
        String oldName = file.getName();
        String c = file.getParent();
        File mm;
        if(file.isFile()) {
            mm = new File(c + File.separator + name + ".txt");
            if (file.renameTo(mm)){
                file = mm;              
                // Put FCB
                putFCB(file);
                for (int i = 0; i < files.size(); i++){
                    if (files.get(i).getName().equals(oldName)){
                        files.remove(i);
                        files.add(file);
                        break;
                    }
                }
                return true;
            }else{
                return false;
            }
        }
        else {
            mm = new File(c + File.separator + name);
            file.renameTo(mm);
            return true;
        }
    }

    public int getFileNum() {
        return fileNum;
    }

}
