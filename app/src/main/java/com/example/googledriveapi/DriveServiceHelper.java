package com.example.googledriveapi;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor executor=Executors.newSingleThreadExecutor();
    private Drive drive;
    public DriveServiceHelper(Drive drive){
        this.drive=drive;
    }
    public Task<String> createFile(String filePath){
        return Tasks.call(executor,() ->{
            File filesetName=new File();
            filesetName.setName("MyPDF");
       java.io.File file=new java.io.File(filePath);
            FileContent mediaContent=new FileContent("application/pdf",file);
           File myfile=null;
           try {
               myfile=drive.files().create(filesetName,mediaContent).execute();
           }catch (Exception e){
               e.printStackTrace();
           }if(myfile == null){
               throw new IOException("Null result");
            }
           return  myfile.getId();
        });
    }

}
