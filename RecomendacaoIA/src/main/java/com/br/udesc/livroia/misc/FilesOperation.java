/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.br.udesc.livroia.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jordan
 */
public class FilesOperation {
    public static  String getFileNameSelectOnDisk(){
        String fileName="";
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("arff", 
                                                                    "txt","ARFF", "TXT");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName=chooser.getSelectedFile().getAbsolutePath();
        }
        return fileName;
    }
    public static  String getDataFromFile(String path) throws IOException{
        return new String(Files.readAllBytes(Paths.get(path)));
    }
    
    public static String getDataFromFileSelected() throws IOException{
       return getDataFromFile(getFileNameSelectOnDisk());
    }

}
