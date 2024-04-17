package biz.binarysolutions.mindfulnessmeditation.ui.meditations.ondevice;

import biz.binarysolutions.mindfulnessmeditation.data.Meditation;

/**
 *
 */
public interface OnDeviceAdapterListener {

    void onDeleteMeditation(Meditation meditation);
    void onPlayMeditation(Meditation meditation);
}
