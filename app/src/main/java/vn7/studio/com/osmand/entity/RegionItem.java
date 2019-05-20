package vn7.studio.com.osmand.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import vn7.studio.com.osmand.util.StringFormater;

public class RegionItem implements Serializable {
    private ArrayList<RegionItem> children;
    private RegionItem parent;
    private String name;
    private RegionType regionType = RegionType.OTHER;
    private boolean hasMap = true;
    private boolean baseMap;

    private boolean isMapDownloaded;
    private int downloadProgress;
    private boolean inDownloadProcess;

    private transient RegionChangeListener changeListener;

    public RegionChangeListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(RegionChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public void setChildren(ArrayList<RegionItem> childRegions) {
        this.children = childRegions;

        for (int i = 0; i < childRegions.size(); i++) {
            childRegions.get(i).setParent(this);
        }
    }

    public boolean isBaseMap() {
        return baseMap;
    }

    public void setBaseMap(boolean baseMap) {
        this.baseMap = baseMap;
    }

    public void setName(String name) {
        this.name = StringFormater.makeFirstLetterCapital(name);
    }

    public ArrayList<RegionItem> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public void setType(RegionType type) {
        this.regionType = type;
    }

    public void setHasMap(boolean hasMap) {
        this.hasMap = hasMap;
    }

    public boolean hasMap() {
        return hasMap;
    }

    public String getPath() {

        if (parent != null && parent.getRegionType() != RegionType.CONTINENT) {
            return parent.getPath() + "_" + getName();
        }
        return getName();
    }

    public String getPathWithContinent() {
        RegionItem continent = getContinent();
        if (continent != null) {
            return getPath() + '_' + continent.getName();
        }
        return getPath();
    }

    public RegionItem getParent() {
        return parent;
    }

    public void setParent(RegionItem parent) {
        this.parent = parent;
    }

    public RegionItem getContinent() {
        if (regionType == RegionType.CONTINENT) {
            return this;
        }
        if (parent != null) {
            return parent.getContinent();
        }
        return null;
    }

    public boolean isHasMap() {
        return hasMap;
    }

    public boolean isMapDownloaded() {
        return isMapDownloaded;
    }

    public void setMapDownloaded(boolean mapDownloaded) {
        isMapDownloaded = mapDownloaded;
        if (changeListener != null) {
            changeListener.onMapDownloaded();
        }
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
        if (changeListener != null) {
            changeListener.onProgressChange();
        }
    }

    public boolean isInDownloadProcess() {
        return inDownloadProcess;
    }

    public void setDownloadProcess(boolean inDownloadProcess) {
        this.inDownloadProcess = inDownloadProcess;
        if (changeListener != null) {
            changeListener.onProcessTypeChange();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionItem that = (RegionItem) o;
        return getPathWithContinent().equals(that.getPathWithContinent());
    }

    @Override
    public int hashCode() {
        return getPathWithContinent().hashCode();
    }
}