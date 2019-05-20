package vn7.studio.com.osmand.loader;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class Downloader extends IntentService {
    public static final String DOWNLOAD_URL = "download_url";
    public static final String DOWNLOAD_DIR = "download_dir";
    public static long currentDownloadId;
    public static Thread backgroundThread;
    private static DownloadManager downloadManager;

    public Downloader() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(DOWNLOAD_URL);
        String resultFileName = intent.getStringExtra(DOWNLOAD_DIR);

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle("Downloading " + url);
        request.setDescription("Download file " + url);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, resultFileName);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        currentDownloadId = downloadManager.enqueue(request);
        backgroundThread = Thread.currentThread();
    }
}