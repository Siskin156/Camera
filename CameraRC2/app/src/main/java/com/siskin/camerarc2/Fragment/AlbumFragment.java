package com.siskin.camerarc2.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;


import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.BoxingMediaLoader;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;

import com.siskin.camerarc2.R;
import com.siskin.camerarc2.impl.BoxingGlideLoader;


import java.io.Serializable;
import java.util.List;


/**
 * Created by header on 2017/11/27.
 */

public class AlbumFragment extends Fragment {
    public static final String TAG = "AlbumFragment";

    private View view;
    private BaseMedia mMedia;
    private String path;
    public String mPath;
    private FrameLayout floatingAlbum;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.album_fragment,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BoxingMediaLoader.getInstance().init(new BoxingGlideLoader());
        final CustomAlbumFragment fragment = new CustomAlbumFragment();
       final  FloatingAlbumFragment floatingAlbumFragment=new FloatingAlbumFragment();
        BoxingConfig multiImgConfig = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .withMediaPlaceHolderRes(R.drawable.ic_boxing_default_image).withMaxCount(20).withAlbumPlaceHolderRes(R.drawable.ic_boxing_default_image);
        Boxing.of(multiImgConfig).setupFragment(fragment, new Boxing.OnBoxingFinishListener() {

            @Override
            public void onBoxingFinish(Intent intent, List<BaseMedia> medias) {

                if (medias != null && medias.size() > 0) {

                    /*
                    path="";
                    for(int i=0;i<medias.size();i++){
                        BaseMedia media = mMedia = medias.get(i);
                        path += media.getPath();
                    }
*/

                    Bundle bundle=new Bundle();
                    bundle.putSerializable("medias", (Serializable) medias);
                    floatingAlbumFragment.setArguments(bundle);


                    //Toast.makeText(getActivity(),"path:"+path,Toast.LENGTH_LONG).show();
                    floatingAlbum = (FrameLayout) getActivity().findViewById(R.id.floatingAlbum);

                    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                    //transaction.show(floatingAlbumFragment);
                    floatingAlbum.setVisibility(View.VISIBLE);
                    if(!floatingAlbumFragment.isAdded()){
                    transaction.replace(R.id.floatingAlbum,floatingAlbumFragment).commit();
                    }else{
                        floatingAlbumFragment.reFresh();
                    }

                    //   BoxingMediaLoader.getInstance().displayRaw(holderView, path, 1080, 720, null);
                }

            }
        });
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.Fragment, fragment, CustomAlbumFragment.TAG).commit();


    }


}
