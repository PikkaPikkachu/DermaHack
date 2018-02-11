package com.example.prakritibansal.dermahack;

/**
 * Created by prakritibansal on 2/11/18.
 */

/*import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Upload extends AppCompatActivity {

    private TextView mImageDetails;
    private ImageView mMainImage;

    private ListView MessageListView;
    private MessageAdapter MessageAdapter;
    private android.widget.ProgressBar ProgressBar;
    private EditText MessageEditText;
    private ImageButton SendButton;
    private ImageButton uploadImage;
    private EditText userTextInput;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public static final String FILE_NAME = "temp.jpg";
    private Uri downloadUrl;

    private static final String TAG = Upload.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        MessageListView = (ListView) findViewById(R.id.conversationListView);
        ProgressBar = (android.widget.ProgressBar) findViewById(R.id.progressBar);

        userTextInput = (EditText) findViewById(R.id.userInputEditText);

        // Set text edit listener.
        userTextInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    textEntered();
                    return true;
                }
                return false;
            }
        });




        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference().child("user");

        List<Message> conversationMessage = new ArrayList<Message>();
        MessageAdapter = new MessageAdapter(this, R.layout.item_message, conversationMessage);
        MessageListView.setAdapter(MessageAdapter);

        ProgressBar.setVisibility(View.INVISIBLE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TextMessage message = dataSnapshot.getValue(TextMessage.class);
                MessageAdapter.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        MessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    SendButton.setEnabled(true);
                } else {
                    SendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextMessage friendlyMessage = new TextMessage();
                friendlyMessage.setMessage(MessageEditText.getText().toString());

                databaseReference.push().setValue(friendlyMessage);

                // Clear input box
                MessageEditText.setText("");
            }
        });

//        ImageView fab = findViewById(R.id.fab_selector);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
//                builder
//                        .setMessage(R.string.dialog_select_prompt)
//                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startGalleryChooser();
//                            }
//                        })
//                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startCamera();
//                            }
//                        });
//                builder.create().show();
//            }
//        });


    }

//    public void startGalleryChooser() {
//        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
//                    GALLERY_IMAGE_REQUEST);
//        }
//    }
//
//    public void startCamera() {
//        if (PermissionUtils.requestPermission(
//                this,
//                CAMERA_PERMISSIONS_REQUEST,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.CAMERA)) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
//        }
//    }
//    public File getCameraFile() {
//        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        return new File(dir, FILE_NAME);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            uploadImage(data.getData());
//        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
//            uploadImage(photoUri);
//        }
//    }
//
//    private void uploadImage(Uri uri) {
//        Bitmap bitmap = null;
//        try {
//            bitmap =
//                    scaleBitmapDown(
//                            MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
//                            1200);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        storageReference = firebaseStorage.getReference().child("images").child(uri.getLastPathSegment());
//        UploadTask uploadTask = storageReference.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                downloadUrl = taskSnapshot.getDownloadUrl();
//                TextMessage message = new TextMessage();
//                message.setPhotoUrl(downloadUrl.toString());
//
//                Log.i(TAG, "onSuccess: " + downloadUrl);
//            }
//        });
//
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA_PERMISSIONS_REQUEST:
//                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
//                    startCamera();
//                }
//                break;
//            case GALLERY_PERMISSIONS_REQUEST:
//                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
//                    startGalleryChooser();
//                }
//                break;
//        }
//    }
//
//    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
//
//        int originalWidth = bitmap.getWidth();
//        int originalHeight = bitmap.getHeight();
//        int resizedWidth = maxDimension;
//        int resizedHeight = maxDimension;
//
//        if (originalHeight > originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
//        } else if (originalWidth > originalHeight) {
//            resizedWidth = maxDimension;
//            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
//        } else if (originalHeight == originalWidth) {
//            resizedHeight = maxDimension;
//            resizedWidth = maxDimension;
//        }
//        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
//    }
}
*/
