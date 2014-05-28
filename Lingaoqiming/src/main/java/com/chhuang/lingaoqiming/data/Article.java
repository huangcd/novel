package com.chhuang.lingaoqiming.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Date: 2014/5/23
 * Time: 16:01
 *
 * @author chhuang@microsoft.com
 */
public class Article implements Parcelable {
    public final static Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            Integer chapterNumber = (Integer) source.readValue(Integer.class.getClassLoader());
            String title = (String) source.readValue(String.class.getClassLoader());
            String url = (String) source.readValue(String.class.getClassLoader());
            return new Article(chapterNumber, title, url);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    private int    chapterNumber;
    private String title;
    private String url;

    public Article(int chapterNumber, String title, String url) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(chapterNumber);
        dest.writeValue(title);
        dest.writeValue(url);
    }
}
