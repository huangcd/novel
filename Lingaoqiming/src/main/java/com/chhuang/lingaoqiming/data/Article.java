package com.chhuang.lingaoqiming.data;

/**
 * Date: 2014/5/23
 * Time: 16:01
 *
 * @author chhuang@microsoft.com
 */
public class Article {
    private int chapterNumber;
    private String title;

    public Article(int chapterNumber, String title) {
        this.chapterNumber = chapterNumber;
        this.title = title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getTitle() {
        return title;
    }
}
