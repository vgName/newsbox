package com.example.newsbox.newsbox;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tester on 12/15/2016.
 */
public class CommentsListAdapter extends ArrayAdapter<Comment> {

    private Context mContext;
    private List<Comment> mList;
    private WeakReference<CommentsFragment> mActivityRef;

    public CommentsListAdapter(CommentsFragment activityRef, Context context, int resource, ArrayList<Comment> objects)
    {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mActivityRef = new WeakReference<CommentsFragment>(activityRef);
    }

    private static class ViewHolder {
        TextView tv_com_author;
        TextView tv_com_date;
        TextView tv_com_content;
    }

    public void updateList(ArrayList<Comment> newList)
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
            convertView = inflater.inflate(R.layout.comment_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tv_com_author = (TextView) convertView.findViewById(R.id.tv_com_author);
            viewHolder.tv_com_date = (TextView) convertView.findViewById(R.id.tv_com_date);
            viewHolder.tv_com_content = (TextView) convertView.findViewById(R.id.tv_com_content);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position < mList.size()) {

            viewHolder.tv_com_author.setText(mList.get(position).name);
            viewHolder.tv_com_date.setText(mList.get(position).date);
            viewHolder.tv_com_content.setText(mList.get(position).content);
        }

        return convertView;
    }

    @Override
    public Comment getItem(int i){
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
