package vn7.studio.com.osmand.loader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.util.ArrayList;

import vn7.studio.com.osmand.MemoryChangeListener;
import vn7.studio.com.osmand.app.OsmAndDownloadMapsApplication;
import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.util.StringFormater;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadService {
    public final static String BASE_URL = "http://download.osmand.net/download.php?standard=yes&file=";
    public final static String SUFFIX = "_2.obf.zip";
    public final static String FILE_DIRECTORY = "/OsmAnd/map/";

    public final static String TITLE_PREFIX = "Downloading ";
    public final static String DESCRIPTION_PREFIX = "Download file ";

    private MemoryChangeListener onMemoryChangeListener;
    private Context context = OsmAndDownloadMapsApplication.getAppContext();

    private boolean isInDownloadProcess;

    private ArrayList<RegionItem> deque;

    private DownloadMapAsyncTask currentTask;

    public static DownloadService getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private DownloadService(){
        deque = new ArrayList<>();
    }

    private static class InstanceHolder {
        private final static DownloadService INSTANCE = new DownloadService();
    }

    public String getFullPathForRegion(String fullPath) {
        return new StringBuilder(BASE_URL)
                .append(StringFormater.makeFirstLetterCapital(fullPath.toLowerCase()))
                .append(SUFFIX)
                .toString();
    }

    public boolean isAlreadyDownloading(RegionItem region){
        return deque.contains(region);
    }

    public void downloadMap(RegionItem region) {
        deque.add(region);
        region.setDownloadProcess(true);
        if (!isInDownloadProcess) {
            isInDownloadProcess = true;
            startDownload(region);
        }
    }

    public void cancelDownload(RegionItem region) {
        if (deque.size() >= 1) {
            RegionItem currentTaskRegion = currentTask.getCurrentRegion();

            if (currentTaskRegion != null){
                if (currentTaskRegion.equals(region)) {
                    currentTask.cancelDownloading();
                }
            }
            deque.remove(region);
            region.setDownloadProcess(false);
        }
    }

    private String getResultFileName(String name) {
        return FILE_DIRECTORY + name + SUFFIX;
    }

    public void setMemoryChangeListener(MemoryChangeListener onMemoryChangeListener) {
        this.onMemoryChangeListener = onMemoryChangeListener;
    }

    private class DownloadMapAsyncTask extends AsyncTask<Void , Integer, Void> {
        private RegionItem currentRegion;
        private DownloadManager downloadManager;
        private long currentDownloadId;
        private boolean downloadInterrupted;

        @Override
        protected Void doInBackground(Void... params) {
            String fullRegionPath = currentRegion.getPathWithContinent();
            String url = getFullPathForRegion(fullRegionPath);
            String resultFileName = getResultFileName(fullRegionPath);

            Uri uri = Uri.parse(url);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

            request.setTitle(TITLE_PREFIX + currentRegion.getName());
            request.setDescription(DESCRIPTION_PREFIX + currentRegion.getPathWithContinent() + SUFFIX);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, resultFileName);
            downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

            currentDownloadId = downloadManager.enqueue(request);

            long bytes_total;
            long bytes_downloaded;
            int progress = 0;

            try {
                while (progress < 100 && !downloadInterrupted) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(currentDownloadId);
                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();

                    bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    progress = (int) ((bytes_downloaded * 100) / bytes_total);

                    publishProgress(progress);

                    Thread.sleep(10);

                    cursor.close();
                }
            } catch (Exception e){}
            return null;
        }

        public void cancelDownloading(){
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            downloadManager.remove(currentDownloadId);
            downloadInterrupted = true;
        }

        public long getCurrentDownloadId() {
            return currentDownloadId;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            currentRegion.setDownloadProgress(progress);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (onMemoryChangeListener!=null) {
                onMemoryChangeListener.onMemoryChange();
            }

            if (downloadInterrupted) {
                downloadInterrupted = false;
            } else {
                currentRegion.setDownloadProcess(false);
                currentRegion.setMapDownloaded(true);
                deque.remove(currentRegion);
            }
            if (deque.size() > 0) {
                startDownload(deque.get(0));
            } else {
                isInDownloadProcess = false;
            }
        }

        public RegionItem getCurrentRegion() {
            return currentRegion;
        }

        public void setCurrentRegion(RegionItem currentRegion) {
            this.currentRegion = currentRegion;
        }
    }

    private void startDownload(RegionItem region) {
        currentTask = new DownloadMapAsyncTask();
        currentTask.setCurrentRegion(region);
        currentTask.execute();
    }
}