package com.siskin.camerarc2.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.boxing.model.entity.BaseMedia;
import com.bumptech.glide.Glide;
import com.siskin.camerarc2.GalleryAdapter;

import com.siskin.camerarc2.R;


import java.util.List;


public class FloatingAlbumFragment extends Fragment {
    private  View view;
    private TextView floatingAlbum;
    private List<BaseMedia> medias;
    private BaseMedia mMedia;
    private String path;
    private ImageView floatingAlbum_photo;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<BaseMedia> mDatas;
    private View currentView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_floating_album,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        floatingAlbum=(TextView)view.findViewById(R.id.floatingAlbum);
        floatingAlbum_photo=(ImageView)view.findViewById(R.id.floatingAlbum_photo);

        //得到控件
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        reFresh();

    }

    public void reFresh() {
        medias=(List<BaseMedia>)getArguments().get("medias");


        if(medias!=null&&medias.size()>0){
            Glide.with(getContext()).load(medias.get(0).getPath()).into(floatingAlbum_photo);
        }

        //设置适配器
        mAdapter = new GalleryAdapter(getContext(), medias);
        mAdapter.setOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Glide.with(getContext()).load(medias.get(position).getPath()).into(floatingAlbum_photo);
                //Toast.makeText(getContext(),medias.get(position).getPath(),Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }


}
