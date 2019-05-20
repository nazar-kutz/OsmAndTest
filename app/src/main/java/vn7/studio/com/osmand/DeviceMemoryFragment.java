package vn7.studio.com.osmand;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import vn7.studio.com.osmand.util.MemoryHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceMemoryFragment extends Fragment implements MemoryChangeListener {
    private ProgressBar pbDeviceMemory;
    private TextView tvFreeDeviceMemory;

    public DeviceMemoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_memory, container, false);

        pbDeviceMemory = view.findViewById(R.id.pbMemoryState);
        tvFreeDeviceMemory = view.findViewById(R.id.tvFreeDeviceMemory);

        displayMemory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayMemory();
    }

    public void displayMemory() {
        double availableMemoryInGB =
                (double) MemoryHelper.getAvailableExternalMemorySize() / MemoryHelper.BYTE_PER_GB;
        String format = String.format(getString(R.string.free_memory_formatter), availableMemoryInGB);
        tvFreeDeviceMemory.setText(format);

        int leftPercent = MemoryHelper.getUsageExternalMemoryPercent();
        pbDeviceMemory.setProgress(leftPercent);
    }

    @Override
    public void onMemoryChange() {
        displayMemory();
    }
}
