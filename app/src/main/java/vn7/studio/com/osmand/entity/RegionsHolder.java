package vn7.studio.com.osmand.entity;

import java.util.ArrayList;

import vn7.studio.com.osmand.app.OsmAndDownloadMapsApplication;
import vn7.studio.com.osmand.parser.XmlParser;

public class RegionsHolder {
    private ArrayList<RegionItem> regions;

    private RegionsHolder(){
        regions = XmlParser.parseRegions(OsmAndDownloadMapsApplication.getAppContext());
        for (RegionItem baseRegions : regions) {
            baseRegions.setBaseMap(true);
        }
    }

    private static class InstanceHolder {
        private final static RegionsHolder INSTANCE = new RegionsHolder();
    }

    public static ArrayList<RegionItem> getRegions() {
        return InstanceHolder.INSTANCE.regions;
    }

    public static RegionItem getRegionByPath(String path) {
        return getRegionByPath(getRegions(), path);
    }

    public static RegionItem getRegionByPath(ArrayList<RegionItem> regions, String subPath){
        for (RegionItem current : regions) {
            {
                String name = current.getName();
                if (current.getRegionType() == RegionType.CONTINENT) {
                    if (subPath.endsWith(name)) {
                        String suffix = '_' + name;
                        int endIndex = subPath.length() - suffix.length();
                        return getRegionByPath(current.getChildren(), subPath.substring(0, endIndex));
                    }
                    break;
                } else {
                    if (subPath.startsWith(current.getName())) {
                        if (subPath.equalsIgnoreCase(name)) {
                            return current;
                        } else {
                            String prefix = name + '_';
                            int startIndex = prefix.length();
                            return getRegionByPath(current.getChildren(), subPath.substring(startIndex));
                        }
                    }
                }
            }
        }
        return null;
    }
}