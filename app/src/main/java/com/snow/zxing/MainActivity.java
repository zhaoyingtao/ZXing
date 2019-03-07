package com.snow.zxing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snow.zxingl.activity.CaptureActivity;
import com.snow.zxingl.encoding.QRCodeUtil;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        tvResult = findViewById(R.id.tv_result);

        findViewById(R.id.btn_01).setOnClickListener(view -> {
            Bitmap bitmapa = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Bitmap bitmap = QRCodeUtil.init().createQRCodeBitmap("nihao", 400, 400, bitmapa);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        });
        findViewById(R.id.btn_02).setOnClickListener(view -> {
            Bitmap bitmap2 = QRCodeUtil.init().createQRCodeBitmap("nihao", 400, 400);
            if (bitmap2 != null)
                imageView.setImageBitmap(bitmap2);
        });
        findViewById(R.id.btn_03).setOnClickListener(view -> {
            startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class)
                    , CaptureActivity.REQ_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.REQ_CODE:
                    tvResult.setText(data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN));
                    break;
                default:
                    break;
            }
        }
    }
}
