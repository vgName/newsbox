package com.example.newsbox.newsbox;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by qphv63 on 12/16/2016.
 */

public class CommentsFragment extends Fragment {

    public static final String TAG = "CommentsFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LINK = "param1";
    private static final String ARG_DESCRIPTION = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String link;

    private OnFragmentInteractionListener mListener;

    ArrayList<Comment> comments;

    ListView commentsListView;
    ListAdapter adapterComments;

    public static CommentsFragment newInstance(String param1, String param2) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LINK, param1);
        args.putString(ARG_DESCRIPTION, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_LINK);
            mParam2 = getArguments().getString(ARG_DESCRIPTION);
        }

        comments = new ArrayList<Comment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comments_layout, container, false);

        commentsListView = (ListView) view.findViewById(R.id.listComments);

        adapterComments = new CommentsListAdapter(this, this.getActivity(),  0, comments);

        commentsListView.setAdapter(adapterComments);

        if (comments.size() == 0) {
            new getCommentsTask().execute(mParam1);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onCommentsLoaded(String article_link, ArrayList<Comment> comments);
    }

    private class getCommentsTask extends AsyncTask<String, Void, String> {
        public getCommentsTask() {
            Log.w(TAG, "Start");
        }

        protected String doInBackground(String... urls) {
            Log.w(TAG, "doInBackground");

            C4pda c4pda = new C4pda();

            try
            {
                comments.clear();
                comments.addAll(c4pda.getComments(urls[0]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        protected void onPostExecute(String result) {
            Log.w(TAG, "onPostExecute");
            ((CommentsListAdapter)adapterComments).notifyDataSetChanged();

            if (mListener != null) {
                mListener.onCommentsLoaded(mParam1, comments);
            }
        }
    }
}
