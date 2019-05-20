package vn7.studio.com.osmand;

import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.entity.RegionsHolder;
import vn7.studio.com.osmand.loader.DownloadService;
import vn7.studio.com.osmand.service.RegionService;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private RegionListFragment frRegions;
    private DeviceMemoryFragment frDeviceMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        frRegions = (RegionListFragment) fragmentManager.findFragmentById(R.id.frRegionList);
        frDeviceMemory = (DeviceMemoryFragment) fragmentManager.findFragmentById(R.id.frDeviceMemory);

        DownloadService downloadService = DownloadService.getInstance();
        downloadService.setMemoryChangeListener(frDeviceMemory);

        ArrayList<RegionItem> extendedRegionsWithContinent =
                RegionService.getContinentsAndInnerRegionsAsOneList(RegionsHolder.getRegions());
        frRegions.setRegions(extendedRegionsWithContinent);
        frRegions.refreshView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.appBarColor));
        }
    }
}
