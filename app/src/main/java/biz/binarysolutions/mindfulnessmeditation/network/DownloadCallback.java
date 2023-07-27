package biz.binarysolutions.mindfulnessmeditation.network;

/**
 *
 */
public interface DownloadCallback<T> {

    /**
     *
     */
    interface Progress {
        int ERROR                             = -1;
        int CONNECT_SUCCESS                   = 0;
        int GET_INPUT_STREAM_SUCCESS          = 1;
        int DOWNLOAD_INPUT_STREAM_IN_PROGRESS = 2;
        int DOWNLOAD_INPUT_STREAM_SUCCESS     = 3;
    }

    /**
     *
     * @param code
     * @param size
     */
    void onProgressUpdate(int code, int size);

    /**
     *
     * @param result
     */
    void onDownloadFinished(T result);

    /**
     *
     */
    void onDownloadFailed();
}
