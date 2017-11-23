package com.siskin.camerarc2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.luck.picture.lib.config.PictureMimeType.ofAll;


public class MainActivity extends AppCompatActivity {

    private CameraView camera;

    private ImageView captureButton;
    private ImageView holderView;
    private Handler handler=null;



    private Bitmap bitmap;


    private boolean pendingVideoCapture;
    private boolean capturingVideo;
    private boolean isImage;
    private List<LocalMedia> selectList;

    private File saveFile;
    private Intent broadcastIntent;
    private File dir;
    private String timeStamp;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        List<String> mPermissionList = new ArrayList<>();
        mPermissionList.clear();
//5465465465456

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了

        } else {//请求权限方法
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }

        handler = new Handler();
        dir=new File(Environment.getExternalStorageDirectory() +"/DCIM/Camera/");
        camera = (CameraView) findViewById(R.id.camera);
        camera.bindCameraKitListener(this);
        camera.performClick();

        holderView = (ImageView) findViewById(R.id.holderView);
        holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PictureSelector.create(MainActivity.this)
                        .openGallery(ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                       // .theme(R.style.picture_white_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture_white_style

                        .maxSelectNum(9)// 最大图片选择数量 int
                         .minSelectNum(1)// 最小选择数量 int
                         .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(2)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        //   .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //    .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        //    .enableCrop(true)// 是否裁剪 true or false
                        //    .compress(true)// 是否压缩 true or false
                        //     .glideOverride(200, 200)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        //   .withAspectRatio(16, 9)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        //    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示 true or false
                        //    .isGif(true)// 是否显示gif图片 true or false
                        //   .compressSavePath("/CustomPath1")//压缩图片保存地址
                        //    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                        //     .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                        //     .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        //     .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        //      .openClickSound(true)// 是否开启点击声音 true or false
                        // .selectionMedia(selectlist)// 是否传入已选图片 List<LocalMedia>
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .cropWH(1, 1)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        //     .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                        //     .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                        //  .videoQuality(1)// 视频录制质量 0 or 1 int
                          .videoMaxSecond(30)// 显示多少秒以内的视频or音频也可适用 int
                            .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
                        //.recordVideoSecond(10)//视频秒数录制 默认60s int
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

            }
        });

        captureButton = (ImageView) findViewById(R.id.captureButton);

        captureButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")

            @Override
            public boolean onTouch(View v, MotionEvent event) {



                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.performClick();
                        pendingVideoCapture = true;
                        isImage=true;

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                if (pendingVideoCapture) {
                                    camera.setFocus(CameraKit.Constants.FOCUS_OFF);
                                    Toast.makeText(MainActivity.this,"录像中",Toast.LENGTH_SHORT).show();

                                    isImage=false;
                                    capturingVideo = true;
                                    camera.captureVideo(new CameraKitEventCallback<CameraKitVideo>() {
                                        @Override
                                        public void callback(CameraKitVideo event) {
                                            Toast.makeText(MainActivity.this,"VideoPath:"+event.getVideoFile().toString(),Toast.LENGTH_LONG).show();

                                            broadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(event.getVideoFile()));
                                            sendBroadcast(broadcastIntent);



                                        }
                                    });


                                }
                            }
                        }, 600);

                          handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (capturingVideo) {
                                    pendingVideoCapture = false;
                                    capturingVideo = false;
                                    camera.stopVideo();
                                    camera.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);

                                }
                            }
                        }, 10600);

                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        v.performClick();
                        if (capturingVideo) {
                            pendingVideoCapture = false;
                            capturingVideo = false;
                            camera.stopVideo();
                            camera.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);

                        }else if(isImage){
                            pendingVideoCapture = false;
                            camera.setFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER);

                            camera.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                                @Override
                                public void callback(final CameraKitImage events) {


                                    new Thread() {
                                        public void run() {

                                            bitmap = events.getBitmap();
                                            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                            if (!dir.exists()) {
                                                dir.mkdir();
                                            }

                                           /*
                                            saveFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", timeStamp + ".jpg");

                                            FileOutputStream fOut = null;
                                            try {
                                                fOut = new FileOutputStream(saveFile);
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                fOut.flush();
                                                fOut.close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            //更新相册

                                            Uri uri = Uri.fromFile(saveFile);
                                            try {
                                                MediaStore.Images.Media.insertImage(getContentResolver(),
                                                        saveFile.getAbsolutePath(), timeStamp + ".jpg", null);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            */
                                            Uri originUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                                            saveFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", timeStamp + ".jpg");
                                            Uri  destinationUri = Uri.fromFile(saveFile);
                                            UCrop.of(originUri,destinationUri)
                                                    .withAspectRatio(16, 9)
                                                    .withMaxResultSize(
                                                            5000, 5000)
                                                    .start(MainActivity.this,UCrop.REQUEST_CROP);
                                          //  handler.post(runnableUi);
                                        }
                                    }.start();


                                }
                            });
                            }


                        break;
                    }
                }
                return true;
            }
        });

    }
/*
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
          //  holderView.setImageBitmap(bitmap);

        }

    };
*/

    @Override
    protected void onResume() {
        super.onResume();
       camera.start();
    }

    @Override
    protected void onPause() {
       camera.stop();
        super.onPause();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            if (isFastDoubleClick()) {
                return true;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
    long lastClickTime;
    public  boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:


                   selectList = PictureSelector.obtainMultipleResult(data);
                    String imgPath=null;
                  for (int i=0;i<selectList.size();i++){
                  imgPath += selectList.get(i).getPath();
                                }
                    Toast.makeText(MainActivity.this,imgPath,Toast.LENGTH_LONG).show();
                    break;
                case UCrop.REQUEST_CROP:
                    holderView.setImageURI(UCrop.getOutput(data));
                    saveFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", timeStamp + ".jpg");
                    //更新相册
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(),
                                saveFile.getAbsolutePath(), timeStamp + ".jpg", null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    broadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(saveFile));
                    sendBroadcast(broadcastIntent);
                    break;


            }


        }

    }

}
