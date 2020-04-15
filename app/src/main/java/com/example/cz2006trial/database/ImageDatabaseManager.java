package com.example.cz2006trial.database;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImageDatabaseManager {

    // perform actions on profile photo in firebase storage
    public static void imageDatabase(final ImageCallback imageCallback, String type, final ImageView profilePhoto) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePhotoRef = storage.getReference().child("profilePhoto/" + UID + ".jpg");
        final String[] message = new String[1];
        message[0] = "Click profile photo to edit";
        switch (type) {

            case "retrieve": //retrieve photo from database
                // and dynamically update the nested interface when the firebase storage reference updates
                FirebaseStorage storages = FirebaseStorage.getInstance();
                StorageReference profilePhotoRefs = storages.getReference().child("profilePhoto/" + UID + ".jpg");
                profilePhotoRefs.getBytes(Long.MAX_VALUE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                imageCallback.onCallback(message, bytes);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        profilePhoto.setVisibility(View.VISIBLE);
                        imageCallback.onCallback(message, null);
                    }
                });
                break;

            case "update": //update photo in database
                // and dynamically update the nested interface when the firebase storage reference updates
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
                        imageCallback.onCallback(message, null);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        message[0] = "Profile Photo updated";
                        imageCallback.onCallback(message, null);
                    }
                });
                break;

            case "delete":  //delete photo from database
                // and dynamically update the nested interface when the firebase storage reference updates
                profilePhotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message[0] = "Profile photo removed successfully";
                        imageCallback.onCallback(message, null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        message[0] = "Profile photo failed to remove";
                        imageCallback.onCallback(message, null);
                    }
                });
                break;
            default:
                message[0] = "Error: Something went wrong.";
                imageCallback.onCallback(message, null);
        }
    }

    // nested interface so as to allow relevant fragments to manipulate profile photo
    // only when the data has been retrieved, updated or deleted fully in firebase storage
    public interface ImageCallback {
        void onCallback(String[] message, byte[] bytes);
    }
}
