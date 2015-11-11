package com.commonsware.empublite;

/**
 * Created by ivan on 10/11/15.
 */
public class NoteLoadedEvent {
    private int position = -1;
    private String prose;

    public NoteLoadedEvent(int position, String prose) {
        this.position = position;
        this.prose = prose;
    }

    public int getPosition() {
        return position;
    }

    public String getProse() {
        return prose;
    }
}
