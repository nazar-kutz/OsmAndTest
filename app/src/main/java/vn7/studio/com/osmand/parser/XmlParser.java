package vn7.studio.com.osmand.parser;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

import vn7.studio.com.osmand.R;
import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.entity.RegionType;
import vn7.studio.com.osmand.service.RegionService;

public class XmlParser {
    public final static String TAG_REGION = "region";

    public final static String ATR_NAME = "name";
    public final static String ATR_TYPE = "type";
    public final static String ATR_MAP = "map";

    public final static String TYPE_CONTINENT = "continent";
    public final static String YES = "yes";
    public final static String NO = "no";

    public static ArrayList<RegionItem> parseRegions(Context context) {
        ArrayList<RegionItem> result;
        int depth = 0;

        XmlPullParser parser = context.getResources().getXml(R.xml.regions);


        //find first region tag depth
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(TAG_REGION)) {
                    depth = parser.getDepth();
                    break;
                }
                parser.next();
            }
        } catch (Exception e) {}

        result = parseRegions(depth, parser);

        return result;
    }

    private static ArrayList<RegionItem> parseRegions(int previousDepth, XmlPullParser parser) {
        ArrayList<RegionItem> regions = new ArrayList<>();
        RegionItem lastRegion = null;

        parsing_code :
        {

            try {

                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (parser.getEventType()) {

                        case XmlPullParser.START_TAG:
                            if (parser.getName().equals(TAG_REGION)) {
                                int currentDepth = parser.getDepth();
                                if (previousDepth < currentDepth) {
                                    ArrayList<RegionItem> children = parseRegions(currentDepth, parser);
                                    lastRegion.setChildren(children);
                                }
                                if (previousDepth > parser.getDepth()) {
                                    break parsing_code;
                                } else {
                                    RegionItem region = new RegionItem();

                                    //parse arguments

                                    parseArguments(parser, region);

                                    regions.add(region);
                                    lastRegion = region;
                                }
                            }
                            break;

                        case XmlPullParser.END_DOCUMENT:
                            break parsing_code;
                    }
                    parser.next();
                }

            } catch (Exception e) {}
        }

        RegionService.sortRegionsByName(regions);
        return regions;
    }

    private static void parseArguments(XmlPullParser parser, RegionItem region) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String currentAttribute = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);

            switch (currentAttribute) {
                case ATR_NAME:
                    region.setName(value);
                    break;
                case ATR_TYPE:
                    if (value.equals(TYPE_CONTINENT)) {
                        region.setType(RegionType.CONTINENT);
                    }
                    break;
                case ATR_MAP :
                    if (value.equals(NO)) {
                        region.setHasMap(false);
                    }
                    break;
            }
        }
    }
}