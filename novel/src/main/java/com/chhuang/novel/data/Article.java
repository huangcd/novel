package com.chhuang.novel.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.chhuang.novel.data.dao.ContentKey;

import static com.chhuang.novel.data.dao.ArticleInfo.*;

/**
 * Date: 2014/5/23
 * Time: 16:01
 *
 * @author chhuang@microsoft.com
 */
@SuppressWarnings("UnusedDeclaration")
public class Article implements Parcelable {
    public final static Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            ClassLoader stringClassLoader = String.class.getClassLoader();
            int chapterNumber = source.readInt();
            String title = (String) source.readValue(stringClassLoader);
            String url = (String) source.readValue(stringClassLoader);
            Article article = new Article(chapterNumber, title, url);
            article.setPercentage(source.readDouble());
            article.setContent((String) source.readValue(stringClassLoader));
            article.setDateTime(source.readLong());
            article.setBookName((String) source.readValue(stringClassLoader));
            return article;
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    @ContentKey(key = NOVEL_NAME)
    private String bookName;
    @ContentKey(key = PERCENTAGE)
    private double percentage;
    @ContentKey(key = CONTENT)
    private String content;
    @ContentKey(key = ID)
    private int    id;
    @ContentKey(key = TITLE)
    private String title;
    @ContentKey(key = URL)
    private String url;
    @ContentKey(key = TIME)
    private long   dateTime;

    public Article() {
    }

    public Article(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeValue(title);
        dest.writeValue(url);
        dest.writeDouble(percentage);
        dest.writeValue(content);
        dest.writeLong(dateTime);
        dest.writeValue(bookName);
    }


    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
