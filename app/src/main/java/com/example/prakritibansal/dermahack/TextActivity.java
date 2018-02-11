package com.example.prakritibansal.dermahack;

/**
 * Created by prakritibansal on 2/11/18.
 */


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.DialogInterface;
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





public class TextActivity extends Activity {
    private static final String TAG = "TextActivity";
    private EditText userTextInput;
    private Context appContext;
    private boolean inConversation;


    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ProgressBar progressBar;
    private Uri downloadUrl;

    public static final String FILE_NAME = "temp.jpg";
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        firebaseStorage = FirebaseStorage.getInstance();

        ImageView fab = (ImageView)findViewById(R.id.fab_selector);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TextActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        ImageView maps = (ImageView) findViewById(R.id.map_selector);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TextActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        init();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Initializes the application.
     */
    private void init() {
        Log.d(TAG, "Initializing text component: ");
        appContext = getApplicationContext();
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

    }

    /**
     * Read user text input.
     */
    private void textEntered() {
        // showToast("Text input not implemented");
        String text = userTextInput.getText().toString();
        addMessage(new TextMessage(text, "tx", getCurrentTimeStamp()));
        bot_response(text);

        clearTextInput();
    }

    public void bot_response(String user_response) {
        final String url = "https://pure-stream-52436.herokuapp.com/convo";
        String tag_string_req = "req_login";

        JSONObject user_det = new JSONObject();
        try {
            user_det.put("user_response", user_response);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                url, user_det, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON RESPONSE", "Request Response: " + response.toString());

                try {
                    String bot_reply = response.getString("response");
                    addMessage(new TextMessage(bot_reply.replace("\n", ""), "rx", getCurrentTimeStamp()));

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("url", url);
                Log.e("ON ERROR RESPONSE", "Request Error: " + error.getMessage());

            }
        }) {
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    /**
     * Clear text input field.
     */
    private void clearTextInput() {
        userTextInput.setText("");
    }

    /**
     * Show the text message on the screen.
     *
     * @param message
     */
    private void addMessage(final TextMessage message) {
        Conversation.add(message);
        final MessagesListAdapter listAdapter = new MessagesListAdapter(getApplicationContext());
        final ListView messagesListView = (ListView) findViewById(R.id.conversationListView);
        messagesListView.setDivider(null);
        messagesListView.setAdapter(listAdapter);
        messagesListView.setSelection(listAdapter.getCount() - 1);
    }

    /**
     * Current time stamp.
     *
     * @return
     */
    private String getCurrentTimeStamp() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }
    /**
     * Show a toast.
     *
     * @param message - Message text for the toast.
     */
    private void showToast(final String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.d(TAG, message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap =   (Bitmap) data.getExtras().get("data");
            Uri photoUri = getImageUri(this,bitmap);
            uploadImage(photoUri);
        }
    }
    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }




    private void uploadImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap =
                    scaleBitmapDown(
                            MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                            1200);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        progressBar.setVisibility(View.VISIBLE);
        storageReference = firebaseStorage.getReference().child("images").child(uri.getLastPathSegment());
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                TextMessage message = new TextMessage(downloadUrl.toString(), "cd", getCurrentTimeStamp());
                diseaseAPI(downloadUrl.toString());
                addMessage(message);
                Log.i(TAG, "onSuccess: " + downloadUrl);
            }
        });

    }

    public void diseaseAPI(String image_url){
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String url = "http://192.168.43.108:8080/diagnose";
        String tag_string_req = "req_login";

        JSONObject user_det = new JSONObject();
        try {
            user_det.put("url", image_url);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                url, user_det, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("JSON RESPONSE", "Request Response: " + response.toString());

                try {
                    String bot_reply = response.getString("response");
                    Log.d("bot_reply", bot_reply);
                    bot_reply = bot_reply.replace("[", "");
                    bot_reply = bot_reply.replace("]", "");
                    Log.d("bot_reply", bot_reply);

                    try{
                        JSONArray items = new JSONArray(bot_reply);
                        for(int i = 0; i< items.length(); i++){
                            JSONObject js = (JSONObject) items.get(i);
                            addMessage(new TextMessage("DISEASE\n"+js.getString("disease_name"), "rx", getCurrentTimeStamp()));
                            addMessage(new TextMessage("TREATMENT INFO\n"+js.getString("treatment_info"), "rx", getCurrentTimeStamp()));
                       }
                    }catch(JSONException e) {

                        JSONObject items = new JSONObject(bot_reply);

                        addMessage(new TextMessage("DISEASE\n" + items.getString("disease_name"), "rx", getCurrentTimeStamp()));
                        addMessage(new TextMessage("TREATMENT\n" + items.getString("treatment_info"), "rx", getCurrentTimeStamp()));
                    }
                    progressBar.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("url", url);
                Log.e("ON ERROR RESPONSE", "Request Error: " + error.getMessage());

            }
        }) {
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
       /* if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)) {*/
           /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);*/
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST);
        //}
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

