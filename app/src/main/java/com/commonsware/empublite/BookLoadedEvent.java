package com.commonsware.empublite;

/**
 * Created by ND on 06/02/2015.
 */
public class BookLoadedEvent {
    private BookContents contents = null;

    public BookLoadedEvent(BookContents contents) {
        this.contents = contents;
    }

    public BookContents getBook() {
        return contents;
    }
}
