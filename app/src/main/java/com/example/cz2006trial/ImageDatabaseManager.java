package com.example.cz2006trial;

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

    public static void imageDatabase(final ImageCallback imageCallback, String type, final ImageView profilePhoto) {
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference profilePhotoRef = storage.getReference().child("profilePhoto/" + UID + ".jpg");
        final String[] message = new String[1];
        message[0] = "Click profile photo to edit";
        switch (type) {

            case "retrieve": //retrieve photo from database
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
                        imageCallback.onCallback(message, null);
                    }
                });
                break;

            case "delete":  //delete photo from database
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

    public interface ImageCallback {
        void onCallback(String[] message, byte[] bytes);
    }
}
