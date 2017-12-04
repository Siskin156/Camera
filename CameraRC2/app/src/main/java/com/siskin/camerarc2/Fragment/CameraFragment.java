package com.siskin.camerarc2.Fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.siskin.camerarc2.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import android.view.MotionEvent;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiang.android.pbutton.CProgressButton;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.siskin.camerarc2.getLastPhotoByPath.getLastPhotoByPath;


public class CameraFragment extends Fragment {
    private CameraView camera;

    //圆形进度按钮
    private CProgressButton progressButton;

    //拍照录像按钮
    private ImageView captureButton;

    //显示照片
    private ImageView holderView;

    //录像倒计时
    private TextView videoTextView;

    //进度数值
    private float recLen;
    private float progress;


    private Handler handler = null;

    //照片
    private Bitmap bitmap;
    private byte[] jpeg;

    //关闭线程；
    private boolean isRun = false;

    //是否准备录像
    private boolean pendingVideoCapture;
    //是否开始录像
    private boolean capturingVideo;
    //是否准备拍照
    private boolean isImage;
    //保存选中的图片
    //private List<LocalMedia> selectList;

    //保存路径
    private File saveFile;
    //更新广播
    private Intent broadcastIntent;
    //保存文件夹
    private File dir;
    //拍照时间
    private String timeStamp;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.camera_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        //父目录
        dir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
        camera = (CameraView) view.findViewById(R.id.camera);
        //绑定监听器
        camera.bindCameraKitListener(this);


        //圆形进度按钮
        progressButton = (CProgressButton) view.findViewById(R.id.btn);
        progressButton.startDownLoad(); //you must call startDownload() before download(progress);


        //倒计时
        videoTextView = (TextView) view.findViewById(R.id.videoTextView);

        //照片显示
        holderView = (ImageView) view.findViewById(R.id.holderView);

        Glide.with(this).load(getLastPhotoByPath(this)).asBitmap().error(R.mipmap.ic_launcher).into(holderView);
        holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开选择器
/*
                PictureSelector.create(getActivity())
                        .openGallery(ofAll())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(6)// 每行显示个数 int
                        .selectionMode(2)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(true)// 是否可预览视频 true or false
                        .enablePreviewAudio(true) // 是否可播放音频 true or false
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .cropCompressQuality(90)// 裁剪压缩质量 默认90 int
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .cropWH(1, 1)// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
*/
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.Fragment, new AlbumFragment());
                transaction.commit();


            }
        });

        captureButton = (ImageView) view.findViewById(R.id.captureButton);
        //设置单击，长按，弹起事件。
        captureButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        actionDowm();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        actionUP();
                    }
                    break;
                }
                return true;
            }
        });
    }

    //拍照按钮按下事件
    private void actionDowm() {
        //可以执行录像或拍照
        pendingVideoCapture = true;
        isImage = true;

        //按下延迟0.6s进行录像
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果可以执行录像则录像
                if (pendingVideoCapture) {
                    camera.setFocus(CameraKit.Constants.FOCUS_OFF);
                    //Toast.makeText(MainActivity.this, "录像中", Toast.LENGTH_SHORT).show();
                    videoTextView.setVisibility(View.VISIBLE);
                    captureButton.setVisibility(View.INVISIBLE);
                    progressButton.setVisibility(View.VISIBLE);
                    //设置圆形按钮进度
                    progressButton.startDownLoad(); //you must call startDownload() before download(progress);


                    //设置倒计时和圆形进度
                    recLen = 11;
                    progress = 0;
                    //可以执行倒计时
                    isRun = true;

                    //更新倒计时UI
                    handler.post(runnableUi);

                    //进行录像则弹起时不触发拍照事件
                    isImage = false;
                    //正在录像
                    capturingVideo = true;
                    //延迟11.6s结束录像
                    handler.postDelayed(stopVideoRunnable, 10600);
                    //录像结束后的回调
                    camera.captureVideo(new CameraKitEventCallback<CameraKitVideo>() {
                        @Override
                        public void callback(CameraKitVideo event) {
                            Toast.makeText(getActivity(), "VideoPath:" + event.getVideoFile().toString(), Toast.LENGTH_LONG).show();
                            broadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(event.getVideoFile()));
                            getActivity().sendBroadcast(broadcastIntent);
                        }
                    });
                }
            }
        }, 600);


    }

    //拍照按钮弹起事件
    private void actionUP() {
        //移除或许还在运行的延迟停止录像进程
        handler.removeCallbacks(stopVideoRunnable);
        //如果进行了录像
        if (capturingVideo) {
            //取消录像准备
            pendingVideoCapture = false;
            //录像设为结束
            capturingVideo = false;
            videoTextView.setVisibility(View.GONE);
            captureButton.setVisibility(View.VISIBLE);
            progressButton.setVisibility(View.GONE);
            //取消更新倒计时ui
            isRun = false;
            handler.post(runnableUi);

            //停止录像
            camera.stopVideo();
            //设置对焦
            camera.setFocus(CameraKit.Constants.FOCUS_TAP);
        }
        //如果没进行录像，则触发拍照事件
        else if (isImage) {
            //取消允许录像
            pendingVideoCapture = false;
            //设置对焦方式
            camera.setFocus(CameraKit.Constants.FOCUS_TAP);
            //拍照回调
            camera.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                @Override
                public void callback(final CameraKitImage events) {
                    new Thread() {
                        public void run() {
                            //获取照片
                            bitmap = events.getBitmap();
                            jpeg = events.getJpeg();

                            //获取拍照事件
                            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            //父目录如果不存在则创建
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            //设置拍照获得源URI
                            //Uri originUri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
                            //设置保存路径
                            saveFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", timeStamp + ".jpg");
                            if (!saveFile.exists()) {
                                try {
                                    saveFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            BufferedOutputStream bos = null;
                            try {
                                bos = new BufferedOutputStream(new FileOutputStream(saveFile));

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                bos.flush();
                                bos.close();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                                            dir.getAbsolutePath(), timeStamp + ".jpg", null);
                            } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                //广播扫描刚保存的文件
                                broadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(saveFile));
                                getActivity().sendBroadcast(broadcastIntent);

                                handler.post(upDateIv);



 /*
                            //获得保存路径的URI
                            Uri destinationUri = Uri.fromFile(saveFile);
                            //剪裁照片，如果保存则保存在destinationUri
                           UCrop.of(originUri, destinationUri)
                                    .withAspectRatio(16, 9) //16:9比例
                                    .withMaxResultSize(
                                            5000, 5000)
                                    .start(getContext(),CameraFragment.this, UCrop.REQUEST_CROP);
                    */
                        }
                    }.start();
                }
            });
        }
    }

    Runnable upDateIv = new Runnable() {
        @Override
        public void run() {
            Glide.with(CameraFragment.this).load(jpeg).asBitmap().error(R.mipmap.ic_launcher).into(holderView);

        }
    };

    //录像停止线程
    Runnable stopVideoRunnable = new Runnable() {
        @Override
        public void run() {
            //如果正在录像
            if (capturingVideo) {
                //取消录像许可
                pendingVideoCapture = false;
                //录像标志设为停止
                capturingVideo = false;
                videoTextView.setVisibility(View.GONE);
                captureButton.setVisibility(View.VISIBLE);
                progressButton.setVisibility(View.GONE);
                //设置倒计时UI标志为false
                isRun = false;
                //停止更新倒计时UI
                handler.post(runnableUi);
                //停止录像
                camera.stopVideo();
                //设置对焦方式
                camera.setFocus(CameraKit.Constants.FOCUS_TAP);

            }


        }
    };


    //更新倒计时线程
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (isRun) {
                //倒计时秒速
                recLen = (float) (recLen - 0.1);
                //圆形按钮进度 以1为单位
                progress++;
                //更新倒计时
                if (recLen < 10) {
                    videoTextView.setText("00:0" + (int) recLen);
                } else {
                    videoTextView.setText("00:" + (int) recLen);
                }
                //更新圆形按钮进度
                progressButton.download((int) progress);
                if (recLen > 0) {
                    handler.postDelayed(runnableUi, 100);
                }
            } else {
                return;
            }

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    public void onPause() {
        camera.stop();
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 /*
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PictureConfig.CHOOSE_REQUEST:
                    //选中图片的List
                    selectList = PictureSelector.obtainMultipleResult(data);
                    String imgPath = new String();
                    for (int i=0;i<selectList.size();i++){
                        imgPath += selectList.get(i).getPath();
                    }
                    Toast.makeText(getActivity(),imgPath,Toast.LENGTH_LONG).show();
                    break;

                case UCrop.REQUEST_CROP:

                    holderView.setImageURI(UCrop.getOutput(data));
                  Toast.makeText(getActivity(),String.valueOf(UCrop.getOutput(data)),Toast.LENGTH_SHORT).show();
                    //获取保存路径
                    saveFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", timeStamp + ".jpg");
                    //更新相册
                    try {
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                                saveFile.getAbsolutePath(), timeStamp + ".jpg", null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //广播扫描刚保存的文件
                    broadcastIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(saveFile));
                    getActivity().sendBroadcast(broadcastIntent);
                    break;
            }
        }
        */

}


}

