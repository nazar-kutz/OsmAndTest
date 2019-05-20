package vn7.studio.com.osmand;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import vn7.studio.com.osmand.entity.RegionItem;
import vn7.studio.com.osmand.entity.RegionsHolder;

public class RegionsActivity extends AppCompatActivity {
    public final static String FULL_REGION_PATH = "full_region_path";
    private RegionListFragment frRegions;
    private Toolbar tbCurrentRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions);

        tbCurrentRegion = findViewById(R.id.tbCurrentRegion);
        setSupportActionBar(tbCurrentRegion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            String regionPath = getIntent().getExtras().getString(FULL_REGION_PATH);
            RegionItem region = RegionsHolder.getRegionByPath(regionPath);

            tbCurrentRegion.setTitle(region.getName());

            FragmentManager fragmentManager = getSupportFragmentManager();
            frRegions = (RegionListFragment) fragmentManager.findFragmentById(R.id.frRegionList);

            frRegions.setRegions(region.getChildren());
            frRegions.refreshView();
        } catch (Exception e){}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
