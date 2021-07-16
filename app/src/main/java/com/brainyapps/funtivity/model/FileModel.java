package com.brainyapps.funtivity.model;

import android.net.Uri;
import android.text.format.DateFormat;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import com.brainyapps.funtivity.AppConstant;
import com.brainyapps.funtivity.AppGlobals;
import com.brainyapps.funtivity.BuildConfig;
import com.brainyapps.funtivity.FuntivityApp;
import com.brainyapps.funtivity.listener.BooleanListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.Date;

public class FileModel {
    public static int PHOTO_SIZE = 512;
    public static int AVATAR_SIZE = 256;

    public static void UploadAvatar(final String local_path, final BooleanListener listener) {
        String file_name = "avatar_";
        final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        final StorageReference imagePhotoRef = AppGlobals.mFirebaseStorage.getReferenceFromUrl(AppConstant.URL_STORAGE_REFERENCE).child(file_name + name);
        Uri photoURI = FileProvider.getUriForFile(FuntivityApp.getContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                new File(local_path));
        final UploadTask uploadTask = imagePhotoRef.putFile(photoURI);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    if (listener != null)
                        listener.done(false, task.getException().toString());
                }
                return imagePhotoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (listener != null)
                        listener.done(true, downloadUri.toString());
                } else {
                    if (listener != null)
                        listener.done(false, task.getException().toString());
                }
            }
        });
    }

    public static void UploadPhoto(final String local_path, final BooleanListener listener) {
        String file_name = "photo_";
        final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        final StorageReference imagePhotoRef = AppGlobals.mFirebaseStorage.getReferenceFromUrl(AppConstant.URL_STORAGE_REFERENCE).child(file_name + name);
        Uri photoURI = FileProvider.getUriForFile(FuntivityApp.getContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                new File(local_path));
        final UploadTask uploadTask = imagePhotoRef.putFile(photoURI);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    if (listener != null)
                        listener.done(false, task.getException().toString());
                }
                return imagePhotoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (listener != null)
                        listener.done(true, downloadUri.toString());
                } else {
                    if (listener != null)
                        listener.done(false, task.getException().toString());
                }
            }
        });
    }
}
