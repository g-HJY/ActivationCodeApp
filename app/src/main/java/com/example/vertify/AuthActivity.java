package com.example.vertify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.revo.aesrsa.JniUtil;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity implements OnClickListener {

    private ImageView img_authentication_License;
    private Button    btn_authentication_confirm;
    private EditText  et_authentication_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_activity_authentication);

        initView();
        initEvent();
    }

    private void initView() {
        img_authentication_License = findViewById(R.id.img_authentication_License);
        btn_authentication_confirm = findViewById(R.id.btn_authentication_confirm);
        et_authentication_key = findViewById(R.id.et_authentication_key);
    }

    private void initEvent() {
        btn_authentication_confirm.setOnClickListener(this);
        try {
            //生成待激活二维码，供激活码生成工具APP扫码生成激活码
            img_authentication_License.setImageBitmap(Create2DCode(JniUtil.getSignature()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_authentication_confirm:
                verifyRegistration();
                break;
            default:
                break;
        }

    }

    /**
     * 验证注册
     */
    private void verifyRegistration() {

        String strLicense = et_authentication_key.getText().toString()
                .replace("-", "");

        if (strLicense.trim().length() <= 0) {
            Toast.makeText(this, "请输入激活码", Toast.LENGTH_LONG).show();
            return;
        }

        if (JniUtil.activeSuccess(strLicense)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "验证不通过", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 用字符串生成二维码
     *
     * @param str
     */
    public Bitmap Create2DCode(String str) throws WriterException {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, metric.widthPixels * 3 / 5,
                metric.widthPixels * 3 / 5);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
