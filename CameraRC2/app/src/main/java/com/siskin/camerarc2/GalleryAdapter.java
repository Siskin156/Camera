package com.siskin.camerarc2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.boxing.model.entity.BaseMedia;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by header on 2017/11/30.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>   {
    private LayoutInflater mInflater;
    private List<BaseMedia> mMedias;
    private Context mContext;

    private OnItemClickLitener mOnItemClickLitener;

    public GalleryAdapter(Context context, List<BaseMedia> medias)
    {
        mContext=context;

        mInflater = LayoutInflater.from(context);
        mMedias = medias;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView mImg;

    }

    @Override
    public int getItemCount()
    {
        return mMedias.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.fragment_floating_album_item,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (ImageView) view.findViewById(R.id.fragment_floating_album_item_image);
        return viewHolder;
    }



    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        //viewHolder.mImg.setImageResource(mMedias.get(i));


        Glide.with(mContext).load(mMedias.get(i).getPath()).into(viewHolder.mImg);
        if (mOnItemClickLitener != null)
        {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });

        }

    }
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }



    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


}
