package com.example.newsbox.newsbox;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by qphv63 on 12/16/2016.
 */

public class ArticleFragment extends Fragment {

    public static final String TAG = "ArticleFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "param1";
    private static final String ARG_DESCRIPTION = "param2";
    private static final String ARG_DATE = "param3";
    private static final String ARG_LINK = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    private OnFragmentInteractionListener mListener;

    TextView titleTextView;
    TextView dateTextView;
    TextView descriptionTextView;
    Button buttonGetComments;
    Button buttonOpenFullArticle;
    Button buttonOpenFullArticleOffline;

    NetworkModule networkModule = new NetworkModule();

    public static ArticleFragment newInstance(String param1, String param2, String param3, String param4) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, param1);
        args.putString(ARG_DESCRIPTION, param2);
        args.putString(ARG_DATE, param3);
        args.putString(ARG_LINK, param4);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_TITLE);
            mParam2 = getArguments().getString(ARG_DESCRIPTION);
            mParam3 = getArguments().getString(ARG_DATE);
            mParam4 = getArguments().getString(ARG_LINK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news_layout, container, false);

        titleTextView = (TextView) view.findViewById(R.id.news_title);
        dateTextView = (TextView) view.findViewById(R.id.news_date);
        descriptionTextView = (TextView) view.findViewById(R.id.news_description);

        titleTextView.setText(mParam1);
        descriptionTextView.setText(mParam2);
        dateTextView.setText(mParam3);

        // *****************************************************************************************

        buttonGetComments = (Button) view.findViewById(R.id.buttonComments);
        android.view.View.OnClickListener listenerButtonComments = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButtonGetComments(v);
            }
        };
        buttonGetComments.setOnClickListener(listenerButtonComments);

        // *****************************************************************************************

        buttonOpenFullArticle = (Button) view.findViewById(R.id.buttonOpenFullArticle);
        android.view.View.OnClickListener listenerButtonFullArticle = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButtonOpenFullArticle(v);
            }
        };
        buttonOpenFullArticle.setOnClickListener(listenerButtonFullArticle);
        // *****************************************************************************************

        buttonOpenFullArticleOffline = (Button) view.findViewById(R.id.buttonOpenFullArticleOffline);
        android.view.View.OnClickListener listenerButtonFullArticleOffline = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressButtonOpenFullArticleOffline(v);
            }
        };
        buttonOpenFullArticleOffline.setOnClickListener(listenerButtonFullArticleOffline);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void pressButtonGetComments(View view) {
        if (mListener != null) {
            mListener.onShowComments(mParam4);
        }
    }

    public void pressButtonOpenFullArticle(View view) {
        if (mListener != null) {
            mListener.onOpenFullArticle(mParam4, true);
        }
    }

    public void pressButtonOpenFullArticleOffline(View view) {
        if (mListener != null) {
            mListener.onOpenFullArticle(mParam4, false);
        }
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
        // TODO: Update argument type and name
        public void onShowComments(String article_link);
        public void onOpenFullArticle(String article_link, boolean isOnline);
    }
}
