package biz.binarysolutions.mindfulnessmeditation.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class CompletedMeditation {

    @PrimaryKey
    private long   timeStamp;
    private String meditationId;

    /**
     *
     * @param meditationId
     */
    public CompletedMeditation(String meditationId) {
       this.timeStamp    = new Date().getTime();
       this.meditationId = meditationId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMeditationId() {
        return meditationId;
    }

    public void setMeditationId(String meditationId) {
        this.meditationId = meditationId;
    }
}
