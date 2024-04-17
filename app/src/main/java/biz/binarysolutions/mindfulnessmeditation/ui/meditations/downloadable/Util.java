package biz.binarysolutions.mindfulnessmeditation.ui.meditations.downloadable;

/**
 *
 */
public class Util {

    /**
     *
     * @param bytes
     * @return
     */
    public static long convertBtoMB(long bytes) {
        return (long) Math.ceil(Math.ceil(bytes / 1024.00) / 1024.00);
    }
}
