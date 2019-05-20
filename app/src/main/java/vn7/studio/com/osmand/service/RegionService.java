package vn7.studio.com.osmand.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.entity.RegionType;
import vn7.studio.com.osmand.entity.RegionsHolder;

public class RegionService {
    public static ArrayList<RegionItem> sortRegionsByName(ArrayList<RegionItem> list) {
        Collections.sort(list, new Comparator<RegionItem>(){
            @Override
            public int compare(RegionItem r1, RegionItem r2) {
                return r1.getName().compareTo(r2.getName());
            }
        });
        return list;
    }

    public static ArrayList<RegionItem> getContinentsAndInnerRegionsAsOneList(ArrayList<RegionItem> regions) {
        ArrayList<RegionItem> result = new ArrayList<>();
        for (RegionItem r : regions) {
            result.add(r);
            if (r.getRegionType()== RegionType.CONTINENT) {
                ArrayList<RegionItem> subRegions = r.getChildren();
                if (subRegions!=null) {
                    result.addAll(subRegions);
                }
            }
        }
        return result;
    }
}