package com.example.logingps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class SignatureActivity extends AppCompatActivity {
    public static final String EXTRA_SIGNATURE = "signature";
    MyDrawView myDrawView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Signature");

        myDrawView = findViewById(R.id.draw);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File folder = new File(getCacheDir().toString());
                boolean success = false;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }

                System.out.println(success + "folder");

                File file = null;
                Intent intent = getIntent();
                Uri uri = intent.getParcelableExtra("Signature");
                if (uri != null) {
                    file = new File(String.valueOf(uri));
                } else {
                    String fileName = UUID.randomUUID().toString() + ".png";
                    file = new File(getCacheDir().toString() + "/" + fileName);
                }

                if (!file.exists()) {
                    try {
                        success = file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);

                    View targetView = myDrawView;

                    // myDrawView.setDrawingCacheEnabled(true);
                    //   Bitmap save = Bitmap.createBitmap(myDrawView.getDrawingCache());
                    //   myDrawView.setDrawingCacheEnabled(false);
                    // copy this bitmap otherwise distroying the cache will destroy
                    // the bitmap for the referencing drawable and you'll not
                    // get the captured view
                    //   Bitmap save = b1.copy(Bitmap.Config.ARGB_8888, false);
                    //BitmapDrawable d = new BitmapDrawable(b);
                    //canvasView.setBackgroundDrawable(d);
                    //   myDrawView.destroyDrawingCache();
                    // Bitmap save = myDrawView.getBitmapFromMemCache("0");
                    // myDrawView.setDrawingCacheEnabled(true);
                    //Bitmap save = myDrawView.getDrawingCache(false);
                    Bitmap well = myDrawView.getBitmap();
                    Bitmap save = Bitmap.createBitmap(320, 480, Config.ARGB_8888);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    Canvas now = new Canvas(save);
                    now.drawRect(new Rect(0, 0, 320, 480), paint);
                    now.drawBitmap(well, new Rect(0, 0, well.getWidth(), well.getHeight()), new Rect(0, 0, 320, 480), null);

                    // Canvas now = new Canvas(save);
                    //myDrawView.layout(0, 0, 100, 100);
                    //myDrawView.draw(now);
                    if (save == null) {
                        System.out.println("NULL bitmap save\n");
                    }
                    save.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    //bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    //fileOutputStream.flush();
                    //fileOutputStream.close();

                    setResults(Uri.fromFile(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setResults(Uri uri) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SIGNATURE, uri);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.digitalsignature_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                myDrawView.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}