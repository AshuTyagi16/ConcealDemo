package com.sasuke.encrypter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    PDFView mPdfView;

    private static final String EXTRA_FILE_PATH = "FILEPATH";

    private String filePath;

    public static Intent newIntent(Context context, String path) {
        Intent intent = new Intent(context, PdfViewerActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        if (getIntent() != null) {
            filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        }

        mPdfView = (PDFView) findViewById(R.id.pdfView);

        mPdfView
                .fromStream(ConcealHelper.getInstance(this).decryptFileAndConvertIntoStream(new File(filePath)))
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
    }
}
