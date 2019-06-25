package com.example.contactame.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.example.contactame.R;
import com.example.contactame.adapter.ViewPagerAdapter;
import com.example.contactame.fragments.CitasCanceladasFragment;
import com.example.contactame.fragments.CitasTerminadasFragment;
import com.example.contactame.fragments.CitasProcesoFragment;
import com.example.contactame.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CitasActivity extends AppCompatActivity {


    private SectionsPagerAdapter viewPagerAdapter;
    private TextView tvDia;
    private TextView tvMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        initView();
        initUI();
        initToolbar();
    }


    private void initUI() {

        initToolbar();

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        ViewPager viewPager = findViewById(R.id.viewPager);

        int index = setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        } else {
            ViewCompat.setLayoutDirection(tabLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
        }

        if (Utils.isRTL()) {
            tabLayout.getTabAt(index - 1).select();
        }

        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFillViewport(true);

        try {

            TabLayout.Tab tab1 = tabLayout.getTabAt(0);
            if (tab1 != null) {
                tab1.setIcon(R.drawable.ic_timer_white_24dp);
                if (tab1.getIcon() != null) {
                    tab1.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }
            }

            TabLayout.Tab tab2 = tabLayout.getTabAt(1);
            if (tab2 != null) {
                tab2.setIcon(R.drawable.baseline_date_range_black_24);
                tab2.select();
                if (tab2.getIcon() != null) {
                    tab2.getIcon().setColorFilter(getResources().getColor(R.color.md_grey_600), PorterDuff.Mode.SRC_IN);
                }
            }

            TabLayout.Tab tab3 = tabLayout.getTabAt(2);
            if (tab3 != null) {
                tab3.setIcon(R.drawable.ic_timer_off_white_24dp);
                if (tab3.getIcon() != null) {
                    tab3.getIcon().setColorFilter(getResources().getColor(R.color.md_grey_600), PorterDuff.Mode.SRC_IN);
                }
            }

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getIcon() != null) {
                        tab.getIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab.getIcon() != null) {
                        tab.getIcon().setColorFilter(getResources().getColor(R.color.md_grey_600), PorterDuff.Mode.SRC_IN);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getIcon() != null) {
                        tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    }
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (Utils.isRTL()) {
            viewPagerAdapter.addFragment(CitasTerminadasFragment.newInstance(), "Terminadas");
            viewPagerAdapter.addFragment(CitasProcesoFragment.newInstance(), "Pendientes");
            viewPagerAdapter.addFragment(CitasCanceladasFragment.newInstance(), "Canceladas");
        } else {
            viewPagerAdapter.addFragment(CitasTerminadasFragment.newInstance(), "Terminadas");
            viewPagerAdapter.addFragment(CitasProcesoFragment.newInstance(), "Pendientes");
            viewPagerAdapter.addFragment(CitasCanceladasFragment.newInstance(), "Canceladas");
        }
        viewPager.setAdapter(viewPagerAdapter);

        return viewPagerAdapter.getCount();
    }

    private void initView() {
        tvDia = findViewById(R.id.tv_dia);
        tvMes = findViewById(R.id.tv_mes);

        tvDia.setText(Utils.getDayOfWeek());
        tvMes.setText(Utils.getMonthAndYear());
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public String getTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.md_grey_300), PorterDuff.Mode.SRC_ATOP);
        }

        toolbar.setTitle(Utils.getDayOfWeek());

        try {
            toolbar.setTitleTextColor(getResources().getColor(R.color.md_white_1000));
        } catch (Exception e) {
            Log.e("TEAMPS", "Can't set color.");
        }

        try {
            setSupportActionBar(toolbar);
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set support action bar.");
        }

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e("TEAMPS", "Error in set display home as up enabled.");
        }

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        if (Utils.isRTL()) {
            collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.END);
        } else {
            collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.START);
        }
    }

}
