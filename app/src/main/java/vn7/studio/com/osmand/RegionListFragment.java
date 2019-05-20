package vn7.studio.com.osmand;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import vn7.studio.com.osmand.app.OsmAndDownloadMapsApplication;
import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.loader.DownloadService;

public class RegionListFragment extends Fragment implements RegionListAdapter.OnRegionListener, RegionListAdapter.OnDownloadClickListener{
    private RecyclerView rvRegions;
    private ArrayList<RegionItem> regions;

    private Context context = OsmAndDownloadMapsApplication.getAppContext();

    public RegionListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_region_list, container, false);

        rvRegions = view.findViewById(R.id.rvRegions);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvRegions.setLayoutManager(layoutManager);

        return view;
    }

    public void refreshView(){
        rvRegions.setAdapter(new RegionListAdapter(regions, this, this));
    }

    public void setRegions(ArrayList<RegionItem> regions) {
        this.regions = regions;
    }

    @Override
    public void onRegionClick(int position) {
        RegionItem chosenRegion = regions.get(position);
        if (chosenRegion.hasChildren()) {
            Intent intent = new Intent(context, RegionsActivity.class);

            intent.putExtra(RegionsActivity.FULL_REGION_PATH, chosenRegion.getPathWithContinent());
            startActivity(intent);
        }
    }

    @Override
    public void onDownloadClick(int position, ImageView btnDownload, ProgressBar pbDownload) {
        DownloadService downloader = DownloadService.getInstance();
        RegionItem region = regions.get(position);

        if (!downloader.isAlreadyDownloading(region)) {
            downloader.downloadMap(region);
        } else {
            downloader.cancelDownload(region);
        }
    }
}