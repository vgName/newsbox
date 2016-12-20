package com.example.newsbox.newsbox;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qphv63 on 12/19/2016.
 */
public class CacheManager {
    private final static String TAG = "CacheManager";

    private final static String CACHE_FILE_MAME = "cache_newsbox";

    private final static String TokenArticleBegin        = "beginarticle";
    private final static String TokenArticleEnd          = "endarticle";
    private final static String TokenArticleTitle        = "articletitle";
    private final static String TokenArticleDate         = "articledate";
    private final static String TokenArticleLink         = "articlelink";
    private final static String TokenArticleFullHtml     = "articlefullhtml";
    private final static String TokenArticleDescription  = "articledescription";

    private File mCacheFile;
    public List<Article> mList = new ArrayList<Article>();
    private final Context mContext;

    public CacheManager(Context context) {
        mContext = context;
        mCacheFile = mContext.getFileStreamPath(CACHE_FILE_MAME);
        mList.clear();
    }

    public void saveAll(ArrayList<Article> _articles) {
        Log.d(TAG, "saveAll");

        deleteCacheFile();

        for (Article article : _articles) {
            save(article);
        }
    }

    public void save(Article g) {
        Log.d(TAG, "save");

        String string = TokenArticleBegin + "\n" +
                TokenArticleTitle + " " + g.title + "\n" +
                TokenArticleDate + " " + g.date + "\n" +
                TokenArticleDescription + " " + g.description + "\n" +
                TokenArticleLink + " " + g.link + "\n" +
                TokenArticleFullHtml + " " + g.htmlContent + "\n" +
                TokenArticleEnd + "\n";

        if(!mCacheFile.exists())
        {
            Log.d(TAG, "save: File not exist. Creating...");
            mCacheFile = new File(mContext.getFilesDir(), CACHE_FILE_MAME);
        }

        FileOutputStream outputStream;

        try {
            Log.d(TAG, "save: Writing to file...");
            outputStream = mContext.openFileOutput(CACHE_FILE_MAME, Context.MODE_APPEND);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Log.d(TAG, "load");

        BufferedReader bufferedReader = null;
        FileReader reader = null;
        Article _article = null;
        boolean _article_parsed = true;
        boolean _success = true;

        mList.clear();

        if(mCacheFile.exists()) {
            try {
                reader = new FileReader(mCacheFile);
                bufferedReader = new BufferedReader(reader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {

                    Log.w(TAG, "cur_line: " + line);
                    if (line.startsWith(TokenArticleBegin)) {
                        /* start new artticle */
                        if (true == _article_parsed)
                        {
                            _article = new Article();
                            _article_parsed = false;
                        }
                        else
                        {
                            Log.e(TAG, "ERROR: load: unexpected article begin.");
                            _success = false;
                            break;
                        }
                    }
                    else if (line.startsWith(TokenArticleEnd))
                    {
                        /* end of current article */
                        _article_parsed = true;

                        if (_article == null)
                        {
                            Log.e(TAG, "ERROR: load: memory for article not allocated.");
                            _success = false;
                            break;
                        }
                        else {
                            this.mList.add(_article);
                        }
                    }
                    else
                    {
                        /* filling current article info */
                        if (false == _article_parsed)
                        {
                            if (_article == null)
                            {
                                Log.e(TAG, "ERROR: load: memory for article not allocated.");
                                _success = false;
                                break;
                            }

                            if (line.startsWith(TokenArticleTitle))
                            {
                                /* article title */
                                _article.title = line.substring(line.indexOf(TokenArticleTitle) + TokenArticleTitle.length() + 1);

                            }
                            else if (line.startsWith(TokenArticleDate))
                            {
                                /* article date */
                                _article.date = line.substring(line.indexOf(TokenArticleDate) + TokenArticleDate.length() + 1);
                            }
                            else if (line.startsWith(TokenArticleDescription))
                            {
                                /* article description */
                                _article.description = line.substring(line.indexOf(TokenArticleDescription) + TokenArticleDescription.length() + 1);
                            }
                            else if (line.startsWith(TokenArticleLink))
                            {
                                /* article link */
                                _article.link = line.substring(line.indexOf(TokenArticleLink) + TokenArticleLink.length() + 1);
                            }
                            else if (line.startsWith(TokenArticleFullHtml))
                            {
                                /* article html content */
                                //_article.htmlContent = line.substring(line.indexOf(TokenArticleFullHtml) + TokenArticleFullHtml.length() + 1);
                            }
                            else
                            {
                                /*StringBuffer tmp = new StringBuffer();
                                tmp.append(line);*/


                                //_article.htmlContent += line;
                            }
                        }
                        else
                        {
                            Log.e(TAG, "ERROR: load: unexpected article info: " + line);
                            _success = false;
                            break;
                        }
                    }
                }

                if (_success)
                {
                    Log.d(TAG, "load: article list has been loaded successfully");

                    //printAllCachedArticles();
                }
                else
                {
                    Log.e(TAG, "ERROR: load: failed to load article list");
                }

                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "load: Can't open/read file: " + e.getMessage());
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing bufferedReader: " + e.toString());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing reader: " + e.toString());
                }
            }
        }
        else {
            Log.w(TAG, "load: file not exist");
        }
    }

    public String loadOfflineHtmlbyLink(String link) {
        Log.d(TAG, "load");

        BufferedReader bufferedReader = null;
        FileReader reader = null;
        Article _article = null;
        boolean _article_parsed = true;
        boolean _success = true;


        if(mCacheFile.exists()) {
            try {
                reader = new FileReader(mCacheFile);
                bufferedReader = new BufferedReader(reader);
                String line;
                String htmlContent = "";

                boolean search_html_content_started = false;
                boolean search_html_content_found = false;

                while ((line = bufferedReader.readLine()) != null) {

                    if (line.startsWith(TokenArticleLink))
                    {
                        /* article link */
                        if(link.equals(line.substring(line.indexOf(TokenArticleLink) + TokenArticleLink.length() + 1)))
                        {
                            search_html_content_started = true;
                        }
                    } else if (line.startsWith(TokenArticleFullHtml))
                    {
                        /* article html content */
                        if(search_html_content_started) {
                            htmlContent = line.substring(line.indexOf(TokenArticleFullHtml) + TokenArticleFullHtml.length() + 1);
                            search_html_content_found = true;
                            search_html_content_started = false;
                        }
                    }
                    else if (line.startsWith(TokenArticleEnd))
                    {
                        if(search_html_content_found)
                        {
                            return htmlContent;
                        }
                        else if (search_html_content_started == true)
                        {
                            Log.e(TAG, "load: article html content not found");
                            return "";
                        }
                    }
                    else
                    {
                        if (search_html_content_found == true) {
                            htmlContent += line;
                        }
                    }
                }

                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "load: Can't open/read file: " + e.getMessage());
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing bufferedReader: " + e.toString());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing reader: " + e.toString());
                }
            }
        }
        else {
            Log.w(TAG, "load: file not exist");
        }

        return "";
    }

    public ArrayList<Article> getArticles(){
        return (ArrayList<Article>)mList;
    }

    public void deleteCacheFile() {
        Log.d(TAG, "deleteCacheFile");

        if(mCacheFile.exists())
        {
            mCacheFile.delete();
        }
    }

    public void printAllCachedArticles() {
        Log.d(TAG, "printAllCachedArticles");
        for (Article article : mList)
        {

            Log.d(TAG, "Title: " + article.title);
            Log.d(TAG, "Date: " + article.date);
            Log.d(TAG, "Description: " + article.description);
        }
    }

    public void printCacheContent() {
        Log.d(TAG, "printCacheContent");
        BufferedReader bufferedReader = null;
        FileReader reader = null;

        if(mCacheFile.exists()) {
            try {
                reader = new FileReader(mCacheFile);
                bufferedReader = new BufferedReader(reader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {

                    Log.w(TAG, "cur_line: " + line);
                }

                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                Log.e(TAG, "load: Can't open/read file: " + e.getMessage());
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing bufferedReader: " + e.toString());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "load: IOException closing reader: " + e.toString());
                }
            }
        }
        else {
            Log.w(TAG, "load: file not exist");
        }
    }
}
