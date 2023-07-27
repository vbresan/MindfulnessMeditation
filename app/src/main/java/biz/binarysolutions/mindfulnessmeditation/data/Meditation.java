package biz.binarysolutions.mindfulnessmeditation.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 *
 */
@Entity
public class Meditation implements Serializable {

    @PrimaryKey
    @NonNull
    private String id;

    private String title;
    private String author;
    private long   size;
    private String duration;
    private String url;

    private boolean isDownloaded = false;
    private boolean isTerminated = false;

    /**
     * @param id
     * @param title
     * @param author
     * @param size
     * @param duration
     * @param url
     */
    public Meditation
        (
            String id,
            String title,
            String author,
            long   size,
            String duration,
            String url
        ) {

        this.id       = id;
        this.title    = title;
        this.author   = author;
        this.size     = size;
        this.duration = duration;
        this.url      = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }
}
