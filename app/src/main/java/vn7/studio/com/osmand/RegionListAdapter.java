package vn7.studio.com.osmand;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import vn7.studio.com.osmand.app.OsmAndDownloadMapsApplication;
import vn7.studio.com.osmand.entity.RegionChangeListener;
import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.entity.RegionType;

public class RegionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RegionItem> mRegions;
    private OnRegionListener mOnRegionListener;
    private OnDownloadClickListener mOnDownloadClickListener;

    public RegionListAdapter(ArrayList<RegionItem> mRegions, OnRegionListener onRegionListener,
                             OnDownloadClickListener onDownloadClickListener) {
        this.mRegions = mRegions;
        this.mOnRegionListener = onRegionListener;
        this.mOnDownloadClickListener = onDownloadClickListener;
    }

    public static class RegionItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, RegionChangeListener {
        private final TextView tvRegionName;
        private ImageView btnDownload;
        private ImageView imgMap;
        private OnRegionListener onRegionListener;
        private ProgressBar pbDownload;
        private OnDownloadClickListener onDownloadClickListener;
        private RegionItem currentRegion;

        public RegionItemViewHolder(View view, OnRegionListener onRegionListener,
                                    final OnDownloadClickListener onDownloadClickListener) {
            super(view);
            this.tvRegionName = view.findViewById(R.id.tvRegionName);
            this.btnDownload = view.findViewById(R.id.imgDownload);
            this.pbDownload = view.findViewById(R.id.pbDownload);
            this.imgMap = view.findViewById(R.id.imgMap);
            this.onRegionListener = onRegionListener;
            this.onDownloadClickListener = onDownloadClickListener;

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDownloadClickListener.onDownloadClick(getAdapterPosition(), btnDownload, pbDownload);
                }
            });
            tvRegionName.setOnClickListener(this);
        }

        public void setRegion(RegionItem currentRegion) {
            this.currentRegion = currentRegion;
        }

        public void setName(String name) {
            tvRegionName.setText(name);
        }

        @Override
        public void onClick(View view) {
            onRegionListener.onRegionClick(getAdapterPosition());
        }

        public void makeDownloadButtonInvisible() {
            btnDownload.setVisibility(View.INVISIBLE);
        }

        public void makeDownloadButtonVisible() {
            btnDownload.setVisibility(View.VISIBLE);
        }

        public void setMapImage(int mapImageId) {
            imgMap.setImageResource(mapImageId);
        }

        public void setProgressVisibility(int visibility) {
            pbDownload.setVisibility(visibility);
        }

        public void setImageImport(int imgId) {
            btnDownload.setImageResource(imgId);
        }

        public void setDownloadProgress(int progress) {
            pbDownload.setProgress(progress);
        }

        @Override
        public void onProgressChange() {
            pbDownload.setProgress(currentRegion.getDownloadProgress());
        }

        @Override
        public void onProcessTypeChange() {
            if (currentRegion.isInDownloadProcess()){
                setProgressVisibility(View.VISIBLE);
                setImageImport(R.drawable.ic_action_remove_dark);
                setDownloadProgress(currentRegion.getDownloadProgress());
            } else {
                setProgressVisibility(View.INVISIBLE);
                setImageImport(R.drawable.ic_action_import);
            }
        }

        @Override
        public void onMapDownloaded() {
            makeMapGreen();
//            makeDownloadButtonInvisible();
        }

        public void makeMapGreen(){
            int color = (OsmAndDownloadMapsApplication.getAppContext().getResources().getColor(R.color.green));
            imgMap.setColorFilter(color);
        }

        public void clearMapColorFilter() {
            imgMap.clearColorFilter();
        }
    }

    public static class ContinentItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContinentName;

        public ContinentItemViewHolder(View view) {
            super(view);
            this.tvContinentName = view.findViewById(R.id.tvContinentName);
        }

        public void setName(String name) {
            tvContinentName.setText(name);
        }
    }

    @Override
    public int getItemViewType(int position) {
        RegionItem region = mRegions.get(position);
        return region.getRegionType().ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;

        if (viewType == RegionType.CONTINENT.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.continent_list_item, parent, false);
            holder = new ContinentItemViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.region_list_item, parent, false);
            holder = new RegionItemViewHolder(view, mOnRegionListener, mOnDownloadClickListener);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RegionItem region = mRegions.get(position);

        int itemType = getItemViewType(position);

        //continent
        if(itemType==RegionType.CONTINENT.ordinal()){
            ContinentItemViewHolder vh = (ContinentItemViewHolder) holder;
            vh.setName(region.getName());
        }

        //region
        else if (itemType==RegionType.OTHER.ordinal()){
            RegionItemViewHolder vh = (RegionItemViewHolder) holder;
            vh.setName(region.getName());

            region.setChangeListener(vh);
            vh.setRegion(region);

            if (!region.hasMap()) {
                vh.makeDownloadButtonInvisible();
            } else {
                vh.makeDownloadButtonVisible();
            }

            if (region.isBaseMap()) {
                vh.setMapImage(R.drawable.ic_world_globe_dark);
            } else {
                vh.setMapImage(R.drawable.ic_map);
            }
            if (region.isMapDownloaded()) {
                vh.makeMapGreen();
            } else {
                vh.clearMapColorFilter();
            }

            if (region.isInDownloadProcess()){
                vh.setProgressVisibility(View.VISIBLE);
                vh.setImageImport(R.drawable.ic_action_remove_dark);
                vh.setDownloadProgress(region.getDownloadProgress());
            } else {
                vh.setProgressVisibility(View.INVISIBLE);
                vh.setImageImport(R.drawable.ic_action_import);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRegions.size();
    }

    public interface OnRegionListener {
        void onRegionClick(int position);
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(int position, ImageView btnDownload, ProgressBar pbDownload);
    }
}