package com.example.newsbox.newsbox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements FragmentNews.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener, CommentsFragment.OnFragmentInteractionListener {

	public static final String TAG = "MainActivity";

	Button buttonStart;
	// toDelete
	TextView responseTextView;
	private ProgressDialog pDialog;
	FragmentNews newsFragment;
	CacheManager cacheManager;

	public ArrayList<Article> mArticles = new ArrayList<Article>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// toDelete
		responseTextView = (TextView) findViewById(R.id.response);


		buttonStart = (Button) findViewById(R.id.buttonStart);
		android.view.View.OnClickListener listenerButtonStart = new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pressButtonStart(v);
			}
		};
		buttonStart.setOnClickListener(listenerButtonStart);

		// *****************************************************************************************

		if(findViewById(R.id.fragment_container) != null){
			if (savedInstanceState != null ){
				return;
			}

			//FragmentNews newsFragment = new FragmentNews();
			newsFragment = new FragmentNews();
			newsFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, newsFragment).commit();
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if(findViewById(R.id.fragment_article_container) != null){
				if (savedInstanceState != null ){
					return;
				}

				ArticleFragment newsFragmentArticle = new ArticleFragment();
				newsFragmentArticle.setArguments(getIntent().getExtras());
				getSupportFragmentManager().beginTransaction().add(R.id.fragment_article_container, newsFragmentArticle).commit();
			}
		}
		// *****************************************************************************************

		cacheManager = new CacheManager(getApplicationContext());

		showProgress();
    }

	private void pressButtonStart(View v) {
		responseTextView.setText("Start button clicked in MainActivity");
	}

	@Override
	public void onShowArticleDescription(Article article) {

		if(findViewById(R.id.fragment_article_container) != null){

			// toDelete
			ArticleFragment news_item = new ArticleFragment().newInstance(article.title, article.description, article.date, article.link);
			//news_item.setArguments(getIntent().getExtras());
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_article_container, news_item);
			//ft.addToBackStack(null);
			ft.commit();
		}
		else {
			if (findViewById(R.id.fragment_container) != null) {


				ArticleFragment news_item = new ArticleFragment().newInstance(article.title, article.description, article.date, article.link);
				//news_item.setArguments(getIntent().getExtras());
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, news_item);
				ft.addToBackStack(null);
				ft.commit();
			}
		}
		// toDelete
		responseTextView.setText("Start button clicked in MainActivity");
	}

	@Override
	public void onShowArticleComments(Article article) {

		if (findViewById(R.id.fragment_article_container) != null) {

			CommentsFragment comments_fragment = new CommentsFragment().newInstance(article.link, article.description);
			//news_item.setArguments(getIntent().getExtras());
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_article_container, comments_fragment);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			if (findViewById(R.id.fragment_container) != null) {

				CommentsFragment comments_fragment = new CommentsFragment().newInstance(article.link, article.description);
				//news_item.setArguments(getIntent().getExtras());
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, comments_fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		}
		responseTextView.setText("ShowComments");
	}

	@Override
	public void onShowComments(String link) {

		if (findViewById(R.id.fragment_article_container) != null) {

			CommentsFragment comments_fragment = new CommentsFragment().newInstance(link, "");
			//news_item.setArguments(getIntent().getExtras());
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_article_container, comments_fragment);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			if (findViewById(R.id.fragment_container) != null) {

				CommentsFragment comments_fragment = new CommentsFragment().newInstance(link, "");
				//news_item.setArguments(getIntent().getExtras());
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, comments_fragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		}
		responseTextView.setText("ShowComments");
	}

	@Override
	public void onOpenFullArticle(String article_link, boolean isOnline){
		Intent intent = new Intent(getApplicationContext(),	ArticleFullActivity.class);
		intent.putExtra("EXTRA_URI", article_link);
		String htmlContent = "";


		if(!isOnline)
		{
			if (cacheManager == null)
			{
				cacheManager = new CacheManager(getApplicationContext());
			}
			htmlContent = cacheManager.loadOfflineHtmlbyLink(article_link);
		}


		/*for (Article ar : mArticles ) {
			Log.w("********", ar.link + " ==" + article_link);
			if (ar.link.equals(article_link)){
				htmlContent = ar.htmlContent;
				break;
			}
		}*/
		Log.w("********", " html: " + htmlContent);
		intent.putExtra("EXTRA_CACHED_CONTENT", htmlContent);
		intent.putExtra("EXTRA_ONLINE_MODE", (isOnline) ? "ONLINE" : "OFFLINE");
		startActivity(intent);
	}

	@Override
	public void onShowArticleDescriptionFull(Article article, boolean isOnline) {
		Intent intent = new Intent(getApplicationContext(),	ArticleFullActivity.class);

		String htmlContent = "";
		if(!isOnline)
		{
			if (cacheManager == null)
			{
				cacheManager = new CacheManager(getApplicationContext());
			}

			htmlContent = cacheManager.loadOfflineHtmlbyLink(article.link);
		}
		else
		{
			htmlContent = article.htmlContent;
		}

		intent.putExtra("EXTRA_URI", article.link);
		intent.putExtra("EXTRA_CACHED_CONTENT", htmlContent);
		intent.putExtra("EXTRA_ONLINE_MODE", (isOnline) ? "ONLINE" : "OFFLINE");
		startActivity(intent);
	}

	private void showProgress() {
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Loading...");
		pDialog.show();
		pDialog.setCancelable(false);
	}

	private void hideProgress() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	@Override
	public void onGetNews(ArrayList<HashMap<String, String>> news, ArrayList<Article> articles) {
		responseTextView.setText("Start button clicked in fragment");

		mArticles.clear();
		mArticles.addAll(articles);

		if(mArticles.size() > 0) {
			if (cacheManager == null) {
				cacheManager = new CacheManager(getApplicationContext());
			}

			cacheManager.saveAll(mArticles);
		}

		hideProgress();
	}

	@Override
	public void onLoadNewPageStarted() {
		showProgress();
	}

	@Override
	public void onLoadNewPageCompleted(ArrayList<HashMap<String, String>> newNews, ArrayList<Article> newArticles) {
		mArticles.addAll(newArticles);

		if(mArticles.size() > 0) {
			if (cacheManager == null) {
				cacheManager = new CacheManager(getApplicationContext());
			}

			cacheManager.saveAll(mArticles);
		}

		hideProgress();
	}

	@Override
	public void onCommentsLoaded(String article_link, ArrayList<Comment> comments) {

		for (Article article : mArticles ) {
			if(article.link.equals(article_link))
			{
				article.coments.clear();
				article.coments.addAll(comments);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		/*if (id == R.id.action_settings) {
			return true;
		}
		else*/ if (id == R.id.menu_clear_cache) {
			if (cacheManager == null)
			{
				cacheManager = new CacheManager(getApplicationContext());
			}

			cacheManager.deleteCacheFile();
			return true;
		}
		/*else if (id == R.id.menu_cache_save) {

			if (cacheManager == null)
			{
				cacheManager = new CacheManager(getApplicationContext());
			}

			cacheManager.saveAll(mArticles);

			return true;
		}*/
		else if (id == R.id.menu_cache_load)
		{
			if (cacheManager == null)
			{
				cacheManager = new CacheManager(getApplicationContext());
			}

			cacheManager.load();
			mArticles = (ArrayList<Article>)cacheManager.getArticles();

			newsFragment.setArticleList(mArticles);
		}
		else if (id == R.id.menu_news_by_category) {
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				final EditText new_id = new EditText(MainActivity.this);
				alert.setMessage("Enter category name");
				alert.setTitle("Category");

				alert.setView(new_id);

				alert.setPositiveButton("Start", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String enteredCategory = new_id.getText().toString();

						if (!enteredCategory.isEmpty()) {
							newsFragment.collectNewsByCategory(enteredCategory);
							showProgress();
						}
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// what ever you want to do with No option.
					}
				});

				alert.show();
			}
		}

		return super.onOptionsItemSelected(item);
	}
}
