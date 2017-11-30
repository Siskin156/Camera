package com.siskin.camerarc2.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilibili.boxing.model.entity.BaseMedia;
import com.siskin.camerarc2.R;
import com.siskin.camerarc2.beanPath;

import java.util.List;


public class FloatingAlbumFragment extends Fragment {
    private  View view;
    private TextView floatingAlbum;
    private List<BaseMedia> medias;
    private BaseMedia mMedia;
    private String path;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_floating_album,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh();

    }

    public void refresh(){
        path="";
        floatingAlbum=(TextView)view.findViewById(R.id.floatingAlbum);

        medias=(List<BaseMedia>)getArguments().get("medias");
        for(int i=0;i<medias.size();i++){
            BaseMedia media = mMedia = medias.get(i);
            path += media.getPath();
        }
        floatingAlbum.setText(path);

    }
}
