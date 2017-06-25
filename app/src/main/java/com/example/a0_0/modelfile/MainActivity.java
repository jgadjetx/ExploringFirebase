package com.example.a0_0.modelfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    private Button btnUpload;
    private ImageButton btnChooseFile;
    private EditText txtName;
    private ImageView imageView;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private static final int  GALLERY_INTENT = 2;

    ///MAGIC
    private Intent intent;
    private Uri uri;
    private String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnChooseFile = (ImageButton) findViewById(R.id.btnChooseFile);
        txtName = (EditText) findViewById(R.id.txtName);
        imageView = (ImageView) findViewById(R.id.image);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isInternetAvailable())
                {
                    if (txtName.getText() != null && txtName.getText().length() >= 2)
                    {
                        progressDialog.setMessage("Any Moment Now...");
                        progressDialog.show();

                        final String  key = databaseReference.push().getKey();

                        //Saving Photo to Storage
                        StorageReference filePath = storageReference.child("Photos").child(key);
                        if (uri != null)
                        {
                            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                    downloadUrl = downloadUri.toString();

                                    //Saving Model Instance to Database
                                    String name = txtName.getText().toString();

                                    Model model = new Model(name,downloadUrl);
                                    databaseReference.child("Users").child(key).setValue(model);
                                    txtName.setText(null);

                                    //Send image to imageView
                                    createText("Fetching image from database");
                                    Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(imageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    createText("Error occured");
                                }
                            });
                        }
                        else
                        {
                            String name = txtName.getText().toString();

                            Model model = new Model(name,downloadUrl);
                            databaseReference.child("Users").child(key).setValue(model);
                            txtName.setText(null);
                            progressDialog.dismiss();
                            createText("Data sent");
                        }
                    }
                    else
                    {
                        createText("Cannot append submit empty name");
                    }

                }
                else
                {
                    createText("No Internet Access");
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK)
        {
            uri = data.getData();
            createText("File Selected");
        }
    }

    public void createText(String text)
    {
        Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
    }

    public boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}
