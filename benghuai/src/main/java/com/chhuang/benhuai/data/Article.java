package com.chhuang.benhuai.data;

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
            int chapterNumber = source.readInt();
            String title = (String) source.readValue(String.class.getClassLoader());
            String url = (String) source.readValue(String.class.getClassLoader());
            return new Article(chapterNumber, title, url);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    /**
     * 已读字数
     */
    private int offset;
    private String content;
    private int chapterNumber;
    private String title;
    private String url;

    public Article(int chapterNumber, String title, String url) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.url = url;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        dest.writeInt(chapterNumber);
        dest.writeValue(title);
        dest.writeValue(url);
    }
}
