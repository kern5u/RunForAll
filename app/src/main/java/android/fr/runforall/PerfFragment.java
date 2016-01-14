package android.fr.runforall;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;


public class PerfFragment extends FragmentActivity {
    // Fragment TabHost as mTabHost
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_perf);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Dist"),
                DistanceFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tps"),
                TempsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Vit"),
                VitMoyFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator("Sup"),
                DeleteEntryFragment.class, null);
    }
}