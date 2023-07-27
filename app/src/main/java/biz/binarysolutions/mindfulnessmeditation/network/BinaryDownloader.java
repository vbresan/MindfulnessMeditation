package biz.binarysolutions.mindfulnessmeditation.network;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback.Progress.CONNECT_SUCCESS;
import static biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback.Progress.DOWNLOAD_INPUT_STREAM_IN_PROGRESS;
import static biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback.Progress.DOWNLOAD_INPUT_STREAM_SUCCESS;
import static biz.binarysolutions.mindfulnessmeditation.network.DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS;

/**
 *
 */
public class BinaryDownloader {

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    static private class DownloadTask extends AsyncTask<String, Integer, Integer> {

        private static final int TIMEOUT = 5000;

        private final DownloadCallback<Integer> callback;
        private final File                      directory;

        /**
         *
         * @param callback
         * @param directory
         */
        DownloadTask(DownloadCallback<Integer> callback, File directory) {
            this.callback  = callback;
            this.directory = directory;
        }

        /**
         *
         * @param url
         * @return
         */
        private HttpsURLConnection getConnection(URL url) throws IOException {

            HttpsURLConnection connection = (HttpsURLConnection)
                url.openConnection();

            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            return connection;
        }

        /**
         * @param stream
         * @param fileName
         * @return
         */
        private int saveStream(InputStream stream, String fileName)
            throws IOException {

            File             file   = new File(directory, fileName);
            FileOutputStream fos    = new FileOutputStream(file);
            byte[]           buffer = new byte[4 * 1024];

            int count     = 0;
            int bytesRead = 0;
            int total     = 0;

            while ((bytesRead = stream.read(buffer)) > 0 ) {
                fos.write(buffer, 0, bytesRead);
                total += bytesRead;
                count++;

                if (count % 25 == 0) { // every 100k
                    publishProgress(DOWNLOAD_INPUT_STREAM_IN_PROGRESS, total);
                }

                if (isCancelled()) {
                    total = 0;
                    break;
                }
            }

            if (total > 0) {
                publishProgress(DOWNLOAD_INPUT_STREAM_SUCCESS);
            }

            fos.close();

            return total;
        }

        /**
         *
         * @param url
         * @param fileName
         * @return
         * @throws IOException
         */
        private int downloadUrl(URL url, String fileName) throws IOException {

            int size = 0;

            InputStream        stream     = null;
            HttpsURLConnection connection = null;

            try {
                connection = getConnection(url);
                publishProgress(CONNECT_SUCCESS);

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                stream = connection.getInputStream();
                if (stream != null) {

                    publishProgress(GET_INPUT_STREAM_SUCCESS);
                    size = saveStream(stream, fileName);
                }
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return size;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (callback == null) {
                return;
            }

            int code = 0;
            int size = 0;

            if (values.length >= 1) {
                code = values[0];
            }
            if (values.length >= 2) {
                size = values[1];
            }

            callback.onProgressUpdate(code, size);
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected Integer doInBackground(String... params) {

            int size = 0;

            if (!isCancelled() && params != null && params.length >= 2) {

                String urlString = params[0];
                String fileName  = params[1];

                try {
                    size = downloadUrl(new URL(urlString), fileName);
                } catch(Exception e) {
                    // do nothing
                }
            }

            return size;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Integer size) {

            if (callback == null) {
                return;
            }

            if (size != null && size > 0) {
                callback.onDownloadFinished(size);
            } else {
                callback.onDownloadFailed();
            }
        }
    }

    private DownloadTask downloadTask;

    /**
     *
     * @param callback
     * @param url
     * @param directory
     * @param fileName
     */
    public void startDownload
        (
            DownloadCallback<Integer> callback,
            String url,
            File   directory,
            String fileName
        ) {
        cancelDownload();
        downloadTask = new DownloadTask(callback, directory);
        downloadTask.execute(url, fileName);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }
}
