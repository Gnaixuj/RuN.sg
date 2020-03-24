package com.example.cz2006trial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageDatabaseManager {

    public static void imageDatabase(final ImageCallback imageCallback, String type, final ImageView profilePhoto) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePhotoRef = storage.getReference().child("profilePhoto/" + UID + ".jpg");
        final String[] message = new String[1];
        message[0] = "Click profile photo to edit";
        switch (type) {

            case "retrieve": //retrieve photo from database
                profilePhotoRef.getBytes(Long.MAX_VALUE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                profilePhoto.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                profilePhoto.setVisibility(View.VISIBLE);
                                imageCallback.onCallback(message);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        profilePhoto.setVisibility(View.VISIBLE);
                        imageCallback.onCallback(message);
                    }
                });
                break;

            case "update": //update photo in database
                profilePhoto.setDrawingCacheEnabled(true);
                profilePhoto.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = profilePhotoRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        message[0] = "Photo Upload failed. PLease remove photo or try again";
                        imageCallback.onCallback(message);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        message[0] = "Profile Photo updated";
                        try {
                            DownloadFileManager.getDownloadUrl(new DownloadFileManager.DownloadCallback() {
                                @Override
                                public void onCallback(String[] message) {
                                    imageCallback.onCallback(message);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //Toast.makeText(getApplicationContext(), "Photo Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        message[0] = "Photo Upload is paused";
                        imageCallback.onCallback(message);
                    }
                });
                break;

            case "delete":  //delete photo from database
                profilePhotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message[0] = "Profile photo removed successfully";
                        imageCallback.onCallback(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        message[0] = "Profile photo failed to remove";
                        imageCallback.onCallback(message);
                    }
                });
                break;
            default:
                message[0] = "Error: Something went wrong.";
                imageCallback.onCallback(message);
        }
    }

    public interface ImageCallback {
        void onCallback(String[] message);
    }
}
