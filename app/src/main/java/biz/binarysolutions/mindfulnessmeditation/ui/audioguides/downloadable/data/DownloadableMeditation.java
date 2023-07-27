package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.data;

import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.TotalChangeListener;
import biz.binarysolutions.mindfulnessmeditation.ui.audioguides.downloadable.Util;

/**
 *
 */
public class DownloadableMeditation extends Meditation {

    private boolean isSelected = false;
    private final TotalChangeListener listener;

    /**
     *
     * @param meditation
     */
    public DownloadableMeditation
        (
            Meditation          meditation,
            TotalChangeListener listener
        ) {
        super(
            meditation.getId(),
            meditation.getTitle(),
            meditation.getAuthor(),
            meditation.getSize(),
            meditation.getDuration(),
            meditation.getUrl()
        );

        this.listener = listener;
    }

    /**
     *
     * @param isSelected
     */
    public void setSelected(boolean isSelected) {

        if (this.isSelected != isSelected) {

            this.isSelected = isSelected;

            long size = Util.convertBtoMB(getSize());
            listener.onTotalChanged(isSelected? size : -size);
        }
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return isSelected;
    }
}
