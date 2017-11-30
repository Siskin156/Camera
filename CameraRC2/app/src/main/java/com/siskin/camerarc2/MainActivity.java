package com.siskin.camerarc2;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.siskin.camerarc2.Fragment.AlbumFragment;
import com.siskin.camerarc2.Fragment.CameraFragment;
import com.siskin.camerarc2.Fragment.EmptyFragment;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button toCamera;
    private Button toEmpty;
    private Button toAlbum;
    private Button clearFloat;
    private FrameLayout floatingFrameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toEmpty=(Button)findViewById(R.id.toEmpty);
        toEmpty.setOnClickListener(this);
        toCamera=(Button)findViewById(R.id.toCamera);
        toCamera.setOnClickListener(this);
        toAlbum=(Button)findViewById(R.id.toAlbum);
        toAlbum.setOnClickListener(this);
        clearFloat=(Button)findViewById(R.id.clearFloat);
        clearFloat.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toEmpty:

                replaceFragment(new EmptyFragment());

                break;

            case R.id.toCamera:
                replaceFragment(new CameraFragment());
                break;

            case R.id.toAlbum:

                replaceFragment(new AlbumFragment());

                break;
            case R.id.clearFloat:
                floatingFrameLayout=(FrameLayout)findViewById(R.id.floatingAlbum);


                floatingFrameLayout.setVisibility(View.GONE);


                default:
                    break;
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.Fragment,fragment );
        transaction.commit();
    }


}


