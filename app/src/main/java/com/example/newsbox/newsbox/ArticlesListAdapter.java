package com.example.newsbox.newsbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tester on 12/15/2016.
 */
public class ArticlesListAdapter extends ArrayAdapter<Article> {

    private Context mContext;
    private List<Article> mList;
    private WeakReference<FragmentNews> mActivityRef;

    public ArticlesListAdapter(FragmentNews activityRef, Context context, int resource, ArrayList<Article> objects)
    {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mActivityRef = new WeakReference<FragmentNews>(activityRef);
    }

    private static class ViewHolder {
        ImageView image_news_photo;
        TextView tv_news_content;
        TextView tv_news_date;
    }

    public void updateList(ArrayList<Article> newList)
    {
        mList.clear();
        mList.addAll(newList);
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_item, null);

            viewHolder = new ViewHolder();
            viewHolder.image_news_photo = (ImageView) convertView.findViewById(R.id.image_news_photo);
            viewHolder.tv_news_content = (TextView) convertView.findViewById(R.id.tv_news_content);
            viewHolder.tv_news_date = (TextView) convertView.findViewById(R.id.tv_news_date);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position < mList.size()) {
            viewHolder.image_news_photo.setImageBitmap(mList.get(position).image);
            viewHolder.tv_news_content.setText(mList.get(position).title);
            viewHolder.tv_news_date.setText(mList.get(position).date);
        }



        return convertView;
    }

    @Override
    public Article getItem(int i){
        return mList.get(i);
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public int getCount(){
        return mList.size();
    }
}
