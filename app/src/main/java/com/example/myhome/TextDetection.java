package com.example.myhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class TextDetection extends AppCompatActivity {
    TextView textView;
    Button button1,button2,button3,button4;
    ImageView imageView;
    private Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);
        textView=(TextView)findViewById(R.id.textView);
        button1=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        imageView=(ImageView)findViewById(R.id.imageView);
        button3=(Button)findViewById(R.id.search);
        button4=(Button)findViewById(R.id.Gallery);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();


            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText().toString();

                if(text.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"No text",Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY, text);
                        startActivity(intent);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
    }

    private void detectText() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

        // FirebaseVisionTextDetector detector;
        //detector = FirebaseVision.getInstance().getVisionTextDetector();
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                processtext(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error","Failure");
            }
        });
    }
    private void processtext(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks=firebaseVisionText.getTextBlocks();
        if(blocks.size()==0){
            Toast.makeText(getApplicationContext(),"No text",Toast.LENGTH_LONG).show();
            return;
        }
        for(FirebaseVisionText.TextBlock block:firebaseVisionText.getTextBlocks())
        {
            String txt=block.getText();
            textView.setTextSize(24);
            textView.setText(txt);
        }

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_REQUEST_CODE = 12;


    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {
//            Uri selectedImage = data.getData();
//            imageView.setImageURI(selectedImage);

            //data.getData return the content URI for the selected Image
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            //Get the column index of MediaStore.Images.Media.DATA
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Gets the String value in the column
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            // Set the Image in ImageView after decoding the String

            Uri uri = data.getData();
            try {
                imageBitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;;
            imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void pickFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }




}
