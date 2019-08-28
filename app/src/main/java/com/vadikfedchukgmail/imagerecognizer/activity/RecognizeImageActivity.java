package com.vadikfedchukgmail.imagerecognizer.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.vadikfedchukgmail.imagerecognizer.R;
import com.vadikfedchukgmail.imagerecognizer.utils.GraphicOverlay;
import com.vadikfedchukgmail.imagerecognizer.utils.ResizeImage;
import com.vadikfedchukgmail.imagerecognizer.utils.TextGraphic;

import java.util.List;

public class RecognizeImageActivity extends AppCompatActivity {

    private EditText editSearch;
    private ImageView imageContent;
    private GraphicOverlay mGraphicOverlay;
    private Uri selectedImageUri;
    private Bitmap mSelectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_image);

        selectedImageUri = getIntent().getParcelableExtra("imageUri");
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageContent = findViewById(R.id.image_content);
        imageContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSelectedImage = ResizeImage.resizeSelectedImage(getApplicationContext(), selectedImageUri,
                        imageContent.getWidth(), imageContent.getHeight());
                imageContent.setImageBitmap(mSelectedImage);
            }
        });
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        editSearch = toolbar.findViewById(R.id.edit_search);

        ImageButton actionSearch = toolbar.findViewById(R.id.action_search);
        actionSearch.setOnClickListener(view -> {
            startRecognize(editSearch.getText().toString().trim());
            hideKeyboard(view);
        });
    }

    private void startRecognize(String inputText) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(texts -> processTextRecognitionResult(texts, inputText))
                .addOnFailureListener(
                        e -> Toast.makeText(getApplicationContext(),
                                getString(R.string.error_recognize), Toast.LENGTH_SHORT).show());
    }

    private void processTextRecognitionResult(FirebaseVisionText texts, String inputText) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this,
                    getString(R.string.empty_text_on_image), Toast.LENGTH_SHORT).show();
            return;
        }
        mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k), inputText);
                    mGraphicOverlay.add(textGraphic);

                }
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
