package com.example.newsbox.newsbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNews extends Fragment {

    public static final String TAG = "FragmentNews";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int TAG_MENU_NEWS_ITEM = 0;
    public static final int TAG_MENU_FULL_ARTICLE_ITEM = 1;
    public static final int TAG_MENU_FULL_ARTICLE_OFFLINE_ITEM = 2;
    public static final int TAG_MENU_COMMENTS_ITEM = 3;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button buttonStart;
    private ListView lv;
    private SwipeRefreshLayout swipeContainer;
    private boolean swipeRefreshStarted = false;

    ArrayList<HashMap<String, String>> newsList = new ArrayList<HashMap<String, String>>();
    ArrayList<Article> articles = new ArrayList<Article>();
    ListAdapter adapter;

    public static int pageNumber = 1;

    TextView responseTextView;

    public static FragmentNews newInstance(String param1, String param2) {
        FragmentNews fragment = new FragmentNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentNews() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
        }
        else {
            articles = savedInstanceState.getParcelableArrayList("key");
        }
    }

    public void setArticleList(ArrayList<Article> ar_list) {
        articles.clear();
        articles.addAll(ar_list);
        ((ArticlesListAdapter)adapter).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_news, container, false);

        // toDelete
        responseTextView = (TextView) view.findViewById(R.id.response_Fragment);
        // *****************************************************************************************
        /*
        buttonStart = (Button) view.findViewById(R.id.buttonStartFragment);
        buttonStart.requestFocus();
        android.view.View.OnClickListener listenerButtonStart = new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onUpdateNewsList();
            }
        };
        buttonStart.setOnClickListener(listenerButtonStart);
*/
        //******************************************************************************************
        lv = (ListView) view.findViewById(R.id.listNews);
        lv.setOnCreateContextMenuListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int Position,
                                    long offset) {
                if (mListener != null) {
                    mListener.onShowArticleDescription(articles.get(Position));
                }
            }
        });

        //******************************************************************************************

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshStarted = true;
                //onUpdateNewsList();
                new UpdateNewsListTask().execute();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //******************************************************************************************


        adapter = new ArticlesListAdapter(this, this.getActivity(),  0, articles);

        lv.setAdapter(adapter);

        if (articles.size() == 0) {
            new UpdateNewsListTask().execute();
        }


        //******************************************************************************************
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                this.currentScrollState = i;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    new LoadNewPageTask().execute();
                }
            }
        });

        return view;
    }

    private class UpdateNewsListTask extends AsyncTask<String, Void, String> {
        public UpdateNewsListTask() {
            Log.w(TAG, "Start");
        }

        protected String doInBackground(String... urls) {
            Log.w(TAG, "doInBackground");
            newsList.clear();
            articles.clear();

            C4pda c4pda = new C4pda();
            try {
                articles.addAll(c4pda.getNews());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        protected void onPostExecute(String result) {
            Log.w(TAG, "onPostExecute");
            ((ArticlesListAdapter)adapter).notifyDataSetChanged();

            if(swipeRefreshStarted)
            {
                swipeRefreshStarted = false;
                swipeContainer.setRefreshing(false);
            }

            if (mListener != null) {
                mListener.onGetNews(newsList, articles);
            }
        }
    }

    public void collectNewsByCategory(String category){
        new UpdateNewsListByCategoryTask().execute(category);
    }
    private class UpdateNewsListByCategoryTask extends AsyncTask<String, Void, String> {
        public UpdateNewsListByCategoryTask() {
            Log.w(TAG, "Start");
        }

        protected String doInBackground(String... categories) {
            Log.w(TAG, "doInBackground");
            articles.clear();

            C4pda c4pda = new C4pda();
            try {
                articles.addAll(c4pda.getNewsByCategory(categories[0]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        protected void onPostExecute(String result) {
            Log.w(TAG, "onPostExecute");
            ((ArticlesListAdapter)adapter).notifyDataSetChanged();

            if(swipeRefreshStarted)
            {
                swipeRefreshStarted = false;
                swipeContainer.setRefreshing(false);
            }

            if (mListener != null) {
                mListener.onGetNews(newsList, articles);
            }
        }
    }

    private class LoadNewPageTask extends AsyncTask<String, Void, String> {
        public LoadNewPageTask() {
            Log.w(TAG, "Start");
            if (mListener != null) {
                mListener.onLoadNewPageStarted();
            }
            pageNumber++;
        }

        protected String doInBackground(String... urls) {
            Log.w(TAG, "doInBackground");
            int numberOfArticles = articles.size();

            C4pda c4pda = new C4pda();
            try {
                articles.addAll(c4pda.getNews(pageNumber));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return "OK";
        }

        protected void onPostExecute(String result) {
            Log.w(TAG, "onPostExecute");
            ((ArticlesListAdapter)adapter).notifyDataSetChanged();

            responseTextView.setText("Start Load new page " + pageNumber);

            if (mListener != null) {
                mListener.onLoadNewPageCompleted(newsList, articles);
            }
        }
    }

    private class getCommentsTask extends AsyncTask<String, Void, String> {
        public getCommentsTask() {
            Log.w(TAG, "Start");
        }

        protected String doInBackground(String... urls) {
            Log.w(TAG, "doInBackground");

            C4pda c4pda = new C4pda();
            int i = 0;
            for(Article art:articles) {
                try
                {
                    art.coments.clear();
                    art.coments.addAll(c4pda.getComments(art.link));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                i++;
            }

            return "OK";
        }

        protected void onPostExecute(String result) {
            Log.w(TAG, "onPostExecute");
        }
    }

    public void LoadNewPage() {
        if (mListener != null) {
            mListener.onLoadNewPageStarted();
        }
        pageNumber++;

        int numberOfArticles = articles.size();

        C4pda c4pda = new C4pda();
        try {
            articles.addAll(c4pda.getNews(pageNumber));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (articles.size() > 0) {

            for (int i = numberOfArticles; i < articles.size(); i++) {
                HashMap<String, String> art = new HashMap<String, String>();
                art.put(c4pda.TAG_DESCRIPTION, articles.get(i).description);
                art.put(c4pda.TAG_DATE, articles.get(i).date);
                art.put(c4pda.TAG_TITLE, articles.get(i).title);
                art.put(c4pda.TAG_AUTHOR, articles.get(i).author);
                art.put(c4pda.TAG_IMAGE, articles.get(i).img_link);

                newsList.add(art);
            }
        }
        else{
            Log.e(TAG, "ERROR: read failure.");
        }

        ((ArticlesListAdapter)adapter).notifyDataSetChanged();

        // Download Images
        for (int i = numberOfArticles; i < articles.size(); i++)
        {
            View item1 = (View) getViewByPosition(i, lv);
            ImageView imageView = (ImageView) item1.findViewById(R.id.image_news_photo);
            //new DownloadImageTask(imageView, i, listBitmap).execute(articles.get(i).img_link);

            imageView.setImageBitmap(articles.get(i).image);
        }

        responseTextView.setText("Start Load new page " + pageNumber);

        if (mListener != null) {
            mListener.onLoadNewPageCompleted(newsList, articles);
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
        public void onGetNews(ArrayList<HashMap<String, String>> newslist, ArrayList<Article> articles);
        public void onLoadNewPageStarted();
        public void onLoadNewPageCompleted(ArrayList<HashMap<String, String>> newNews, ArrayList<Article> newArticles);
        public void onShowArticleDescription(Article article);
        public void onShowArticleDescriptionFull(Article article, boolean isOnline);
        public void onShowArticleComments(Article article);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.listNews: {
                ListView lv = (ListView) v;
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

                View mView = acmi.targetView;
                TextView text = (TextView) mView.findViewById(R.id.tv_news_content);
                String str = text.getText().toString();

                //final String str = String.format("Item: %d", acmi.position);
                menu.setHeaderTitle(str);

                menu.add(0, TAG_MENU_NEWS_ITEM, 0, "Open");
                menu.add(0, TAG_MENU_FULL_ARTICLE_ITEM, 0, "Read Full Article(Online)");
                menu.add(0, TAG_MENU_FULL_ARTICLE_OFFLINE_ITEM, 0, "Read Full Article(Offline)");
                menu.add(0, TAG_MENU_COMMENTS_ITEM, 0, "Read Comments");

            }
            break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            // menu items for tvColor
            case TAG_MENU_NEWS_ITEM: {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                if (mListener != null) {
                    mListener.onShowArticleDescription(articles.get(info.position));
                }
                break;
            }
            case TAG_MENU_FULL_ARTICLE_ITEM: {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                if (mListener != null) {
                    mListener.onShowArticleDescriptionFull(articles.get(info.position), true);
                }

                break;
            }
            case TAG_MENU_FULL_ARTICLE_OFFLINE_ITEM: {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                if (mListener != null) {
                    mListener.onShowArticleDescriptionFull(articles.get(info.position), false);
                }

                break;
            }
            case TAG_MENU_COMMENTS_ITEM: {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                if (mListener != null) {
                    mListener.onShowArticleComments(articles.get(info.position));
                }

                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", articles);
        super.onSaveInstanceState(outState);
    }
}
