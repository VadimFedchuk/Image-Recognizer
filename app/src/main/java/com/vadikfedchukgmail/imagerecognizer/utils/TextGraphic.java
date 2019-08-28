package com.vadikfedchukgmail.imagerecognizer.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextGraphic extends GraphicOverlay.Graphic {

    private static final int TEXT_COLOR = Color.RED;
    private static final int TEXT_COLOR_INPUT = Color.GREEN;
    private static final float TEXT_SIZE = 30.0f;
    private static final float STROKE_WIDTH = 1.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final FirebaseVisionText.Element element;

    public TextGraphic(GraphicOverlay overlay, FirebaseVisionText.Element element, String inputText) {
        super(overlay);

        this.element = element;

        rectPaint = new Paint();
        textPaint = new Paint();
        if (inputText.equals(element.getText().toLowerCase())) {
            rectPaint.setColor(TEXT_COLOR_INPUT);
            textPaint.setColor(TEXT_COLOR_INPUT);
        } else {
            rectPaint.setColor(TEXT_COLOR);
            textPaint.setColor(TEXT_COLOR);
        }
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint.setTextSize(TEXT_SIZE);


        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (element == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        RectF rect = new RectF(element.getBoundingBox());
        canvas.drawRect(rect, rectPaint);
        canvas.drawText(element.getText(), rect.left, rect.bottom, textPaint);
    }
}
