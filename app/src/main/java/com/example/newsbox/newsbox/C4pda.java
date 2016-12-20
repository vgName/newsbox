package com.example.newsbox.newsbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by tester on 12/16/2016.
 */
public class C4pda {
    public static final String TAG = "C4pda";
    private ArrayList<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();;
    ArrayList<Article> articles = new ArrayList<Article>();

    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_DATE = "date";
    public static final String TAG_TITLE = "title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_IMAGE = "image";

    private static final String URL_4PDA = "http://www.4pda.ru";

    public C4pda() {
    }

    private String getWebContent(Uri uri){
        return "test";
    }

    public ArrayList<Article> getNews() throws InterruptedException, ExecutionException {
        articles.clear();
        Parser p = new Parser();
        ArrayList<Article> articlesTmp = p.execute(URL_4PDA);
        articles.addAll(articlesTmp);

        return articles;
    }

    public ArrayList<Article> getNews(int page) throws InterruptedException, ExecutionException {
        Parser p = new Parser();
        ArrayList<Article> articlesTmp = p.execute(URL_4PDA + "/page/" + page);
        articles.addAll(articlesTmp);

        return articles;
    }

    public ArrayList<Article> getNewsByCategory(String category) throws InterruptedException, ExecutionException {
        Parser p = new Parser();
        ArrayList<Article> articlesTmp = p.execute(URL_4PDA + "/news/tag/" + category);
        articles.addAll(articlesTmp);

        return articles;
    }

    public ArrayList<Comment> getComments(String link) throws InterruptedException, ExecutionException {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        comments.clear();

        Parser p = new Parser();
        ArrayList<Comment> commentsTmp = p.parseComments(link);
        comments.addAll(commentsTmp);
        return comments;
    }

    //******** JSOUP ***********************************************
    private Document htmlDoc;

    private class Parser
    {
        Parser(){}

        public ArrayList<Article> execute(String url){
            ArrayList<Article> articlesRet = new ArrayList<Article>();
            try {
                htmlDoc = Jsoup.connect(url).get();
                //Element element = htmlDoc.getElementById("div1");
                //element.toString();

                //Elements elements = htmlDoc.getElementsByClass("post");
                //Elements elements = htmlDoc.select("article[class='post']");
                Elements elements = htmlDoc.select("article.post");
                //Elements elements = htmlDoc.getElementsByAttribute("article");
                //Elements elements = htmlDoc.getElementsByTag("p");
                //Elements elements = htmlDoc.getElementById("div1").children();

                for (int i = 1; i < elements.size(); i++) {
                    Element el = elements.get(i);

                    Article article = new Article();
                    article.title = el.select("a").first().attr("title");
                    article.img_link = el.select("img[itemprop='image']").attr("src");
                    article.date = el.select("em.date").text();
                    article.author = el.select("span.autor").text();
                    article.link = el.select("div.description").select("a[rel='bookmark']").attr("href");
                    article.description = el.select("div[itemprop='description']").text();

                    Log.d(TAG, "T: " + article.title);
                    Log.d(TAG, "I: " + article.img_link);
                    Log.d(TAG, "D: " + article.date);
                    Log.d(TAG, "A: " + article.author);
                    Log.d(TAG, "L: " + article.link);
                    Log.d(TAG, "D: " + article.description);

                    // load article image
                    Bitmap bm = null;
                    try {
                        InputStream in = new java.net.URL(article.img_link).openStream();
                        bm = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    article.image = bm;

                    // load full article. for offline mode
                    htmlDoc = Jsoup.connect(article.link).get();
                    article.htmlContent = htmlDoc.toString();

                    articlesRet.add(article);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return articlesRet;
        }


        public ArrayList<Comment> parseComments(String url){
            ArrayList<Comment> commentsRet = new ArrayList<Comment>();
            commentsRet.clear();
            try {
                htmlDoc = Jsoup.connect(url).get();
                //Element element = htmlDoc.getElementById("div1");
                //element.toString();

                //Elements elements = htmlDoc.getElementsByClass("post");
                //Elements elements = htmlDoc.select("article[class='post']");
                //Elements elements = htmlDoc.select("div[id^=comment-%d]");
                Elements elements = htmlDoc.select("div.comment-box[id=comments]").select("li");
                //Elements elements = htmlDoc.getElementsByAttribute("article");
                //Elements elements = htmlDoc.getElementsByTag("p");
                //Elements elements = htmlDoc.getElementById("div1").children();

                for (int i = 0; i < elements.size(); i++) {
                    Log.d(TAG, "Comment #: " + i);
                    Element el = elements.get(i);

                    Log.w(TAG, el.toString());

                    Comment com = new Comment();
                    com.name = el.select("a.nickname").attr("title");
                    com.content = el.select("p.content").first().text();
                    com.date = "";//el.select("span.h-meta").first().text();
                    com.isReplyTo = 0;
                    com.replyTo = "";

                    Log.d(TAG, "N: " + com.name);
                    Log.d(TAG, "C: " + com.content);
                    Log.d(TAG, "D: " + com.date);

                    commentsRet.add(com);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return commentsRet;
        }
    }
}
