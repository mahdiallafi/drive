package com.example.googledriveapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.sql.Driver;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
   DriveServiceHelper driveServiceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSignin();
    }
    //first we chech auth
    public void requestSignin(){
        GoogleSignInOptions signInClient=new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        ).requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE))


                .build();
        GoogleSignInClient client= GoogleSignIn.getClient(this,signInClient);
        startActivityForResult(client.getSignInIntent(),400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      switch (requestCode){
          case 400:
              if(resultCode ==RESULT_OK){
                  handleSignInIntent(data);
              }
              break;
      }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void handleSignInIntent(Intent data){
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        GoogleAccountCredential credential=GoogleAccountCredential.usingOAuth2(MainActivity.this,
                                Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Driver googledriverservices= (Driver) new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),new GsonFactory()
                                ,credential).setApplicationName("My driver").build();
                        driveServiceHelper=new DriveServiceHelper((Drive) googledriverservices);

                    }
                });
    }

    public void uploadFile(View v){
        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Uploading to google driver");
        progressDialog.setMessage("wait plz");
        progressDialog.show();
        String filePath="/storage/emulated/0/sample.pdf";
        driveServiceHelper.createFile(filePath).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
           progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "updated successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "check your drive", Toast.LENGTH_SHORT).show();

            }
        });
    }
}