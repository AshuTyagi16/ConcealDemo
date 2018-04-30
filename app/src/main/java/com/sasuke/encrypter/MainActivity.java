package com.sasuke.encrypter;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "encryptcheck";

    //    private static final String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator
//            + "content" + File.separator
//            + "eCom" + File.separator;

    private static final String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator;

    private static final String FILE_NAME = "ashu.pdf";

    public static final String PREFIX_E = "encrypt_", PREFIX_D = "decrypt_";

    Button mBtnEncrypt;
    Button mBtnDecrypt;
    Button mBtnStartPdf;
    EditText mEtFolderName;
    EditText mEtFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnEncrypt = (Button) findViewById(R.id.btn_encrypt);
        mBtnDecrypt = (Button) findViewById(R.id.btn_decrypt);
        mBtnStartPdf = (Button) findViewById(R.id.btn_pdf);
        mEtFolderName = (EditText) findViewById(R.id.et_folder_name);
        mEtFileType = (EditText) findViewById(R.id.et_file_type);

        mBtnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConcealHelper.getInstance(MainActivity.this).encryptFile(new File(BASE_PATH + FILE_NAME));
//                encryptAll(mEtFolderName.getText().toString(), mEtFileType.getText().toString());
            }
        });

        mBtnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConcealHelper.getInstance(MainActivity.this).decryptFile(new File(BASE_PATH + PREFIX_E + FILE_NAME));
//                decryptAll(mEtFolderName.getText().toString(), mEtFileType.getText().toString());
            }
        });

        mBtnStartPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(PdfViewerActivity.newIntent(MainActivity.this,
                        Environment.getExternalStorageDirectory() + File.separator + PREFIX_E + FILE_NAME));
            }
        });
    }

    private void encryptAll(String folderName, String filetype) {
        String path = BASE_PATH;
        if (folderName.length() > 1)
            path = path + folderName + File.separator;

        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (File file : files) {
            ConcealHelper.getInstance(MainActivity.this).encryptFile(new File(path + file.getName()));
            Log.d("Files", "FileName:" + file.getName());
        }
    }

    private void decryptAll(String folderName, String filetype) {
        String path = BASE_PATH;
        if (folderName.length() > 1)
            path = path + folderName + File.separator;

        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (File file : files) {
            ConcealHelper.getInstance(MainActivity.this).decryptFile(new File(path + file.getName()));
            Log.d("Files", "FileName:" + file.getName());
        }
    }

}
