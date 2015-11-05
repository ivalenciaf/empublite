package com.commonsware.empublite;

import java.util.List;

/**
 * Created by ND on 06/02/2015.
 */
public class BookContents {
    String title;
    List<Chapter> chapters;

    int getChapterCount() {
        return chapters.size();
    }

    String getChapterFile(int position) {
        return chapters.get(position).file;
    }

    String getTitle() {
        return title;
    }

    static class Chapter {
        String file;
        String title;
    }
}
