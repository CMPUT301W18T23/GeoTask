package com.geotask.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.geotask.myapplication.R;

import java.util.List;



/**
 * Created by Kehan 2017/5/16.
 * this is the adapter for requester select the photo
 * reference by: https://blog.csdn.net/qq_26841579/article/details/72438318
 */

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private int mMaxPosition;//
    private List<byte[]> list;

    public PhotoAdapter(Context context, List<byte[]> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        mMaxPosition=list.size()+1;
        return mMaxPosition;
    }
    public int getMaxPosition(){
        return mMaxPosition;
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View v, ViewGroup
            parent) {
        ViewHolder vh=null;
        if (v==null){
            vh=new ViewHolder();
            v= LayoutInflater.from(context).inflate(R.layout.activity_photo_adapter,parent,false);
            vh.img= (ImageView) v.findViewById(R.id.img);
            vh.demimg= (ImageView) v.findViewById(R.id.delimg);
            v.setTag(vh);
        }else{
            vh= (ViewHolder) v.getTag();
        }
        if (position==mMaxPosition-1){
            Glide.with(context).load(R.drawable.id_photo).dontAnimate()
                    .centerCrop().into(vh.img);

            vh.img.setVisibility(View.VISIBLE);
            vh.demimg.setVisibility(View.GONE);
            if (position==10&&mMaxPosition==11){
                vh.img.setVisibility(View.GONE);
            }
        }else{
            vh.demimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(list.get(position)).into(vh.img);//设置
        }
        vh.demimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        return v;
    }

    public class ViewHolder{
        public ImageView img,demimg;
    }

}