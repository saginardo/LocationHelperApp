package com.notinglife.android.LocationHelper.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.notinglife.android.LocationHelper.R;
import com.notinglife.android.LocationHelper.utils.LogUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-17 15:14
 */

public class CaptureActivity extends AppCompatActivity {

    @BindView(R.id.bt_flash_light)
    Button mLight;
    @BindView( R.id.bt_scan_from_gallery)
    Button mFromGallery;

    public static boolean isOpen = false;
    private static final String TAG = "CaptureActivity";
    //startActivityForResult 标志位
    private final static int REQUEST_CODE = 1028;
    private final static int REQUEST_IMAGE = 1029;
    private final static int RESULT_FROM_GALLERY = 1030;
    private final static int REQUEST_FROM_TOOLBAR = 1031;
    private final static int RESULT_FROM_TOOLBAR = 1032;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);

        // 为二维码扫描界面设置定制化界面
        CaptureFragment captureFragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_scan_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initView();
    }


    private void initView() {
        mLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }

            }
        });

        mFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
                intentFromGallery.setType("image/*");
                startActivityForResult(intentFromGallery, REQUEST_FROM_TOOLBAR);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode==REQUEST_IMAGE ){
            //intent传递给MainActivity
            this.setResult(RESULT_FROM_GALLERY, data);
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }
        if( requestCode == REQUEST_FROM_TOOLBAR){
            //intent传递给MainActivity
            LogUtil.i(TAG," data.getData().toString()");
            this.setResult(RESULT_FROM_TOOLBAR, data);
            super.onActivityResult(requestCode, resultCode, data);
            finish();
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }
    };
}
