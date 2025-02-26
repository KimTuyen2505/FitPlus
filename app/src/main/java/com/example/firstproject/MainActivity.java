package com.example.firstproject;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        DashboardFragment.DashboardListener,
        PersonalInfoFragment.PersonalInfoListener,
        HealthTrackingFragment.HealthTrackingListener,
        RemindersFragment.RemindersListener,
        MedicalHistoryFragment.MedicalHistoryListener,
        HealthSuggestionsFragment.HealthSuggestionsListener,
        AnalysisAlertsFragment.AnalysisAlertsListener,
        MenstrualCycleFragment.MenstrualCycleListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý sức khỏe");

        // Set up bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";
                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    selectedFragment = new DashboardFragment();
                    title = "Tổng quan";
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new PersonalInfoFragment();
                    title = "Thông tin cá nhân";
                } else if (itemId == R.id.nav_health) {
                    selectedFragment = new HealthTrackingFragment();
                    title = "Theo dõi sức khỏe";
                } else if (itemId == R.id.nav_reminders) {
                    selectedFragment = new RemindersFragment();
                    title = "Nhắc nhở";
                } else if (itemId == R.id.nav_more) {
                    // Hiển thị menu popup với các tùy chọn khác
                    showMoreOptions();
                    return true;
                }

                if (selectedFragment != null) {
                    getSupportActionBar().setTitle(title);
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportActionBar().setTitle("Tổng quan");
            loadFragment(new DashboardFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void showMoreOptions() {
        // Hiển thị menu popup với các tùy chọn khác
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.nav_more));
        popupMenu.getMenuInflater().inflate(R.menu.more_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";
                int itemId = item.getItemId();

                if (itemId == R.id.action_medical_history) {
                    selectedFragment = new MedicalHistoryFragment();
                    title = "Lịch sử y tế";
                } else if (itemId == R.id.action_health_suggestions) {
                    selectedFragment = new HealthSuggestionsFragment();
                    title = "Lời khuyên sức khỏe";
                } else if (itemId == R.id.action_analysis_alerts) {
                    selectedFragment = new AnalysisAlertsFragment();
                    title = "Phân tích & Cảnh báo";
                } else if (itemId == R.id.action_menstrual_cycle) {
                    selectedFragment = new MenstrualCycleFragment();
                    title = "Chu kỳ kinh nguyệt";
                }

                if (selectedFragment != null) {
                    getSupportActionBar().setTitle(title);
                    loadFragment(selectedFragment);
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_medical_history) {
            loadFragment(new MedicalHistoryFragment());
            getSupportActionBar().setTitle("Lịch sử y tế");
            return true;
        } else if (id == R.id.action_health_suggestions) {
            loadFragment(new HealthSuggestionsFragment());
            getSupportActionBar().setTitle("Lời khuyên sức khỏe");
            return true;
        } else if (id == R.id.action_analysis_alerts) {
            loadFragment(new AnalysisAlertsFragment());
            getSupportActionBar().setTitle("Phân tích & Cảnh báo");
            return true;
        } else if (id == R.id.action_menstrual_cycle) {
            loadFragment(new MenstrualCycleFragment());
            getSupportActionBar().setTitle("Chu kỳ kinh nguyệt");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // DashboardFragment.DashboardListener methods
    @Override
    public void onAddMeasurementClicked() {
        bottomNavigationView.setSelectedItemId(R.id.nav_health);
        Toast.makeText(this, "Đang chuyển đến Theo dõi sức khỏe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewRemindersClicked() {
        bottomNavigationView.setSelectedItemId(R.id.nav_reminders);
        Toast.makeText(this, "Đang chuyển đến Nhắc nhở", Toast.LENGTH_SHORT).show();
    }

    // PersonalInfoFragment.PersonalInfoListener methods
    @Override
    public void onPersonalInfoSaved() {
        Toast.makeText(this, "Thông tin cá nhân đã được lưu thành công", Toast.LENGTH_SHORT).show();
    }

    // HealthTrackingFragment.HealthTrackingListener methods
    @Override
    public void onMeasurementAdded() {
        Toast.makeText(this, "Đã thêm chỉ số mới thành công", Toast.LENGTH_SHORT).show();
    }

    // RemindersFragment.RemindersListener methods
    @Override
    public void onReminderAdded() {
        Toast.makeText(this, "Đã thêm nhắc nhở mới thành công", Toast.LENGTH_SHORT).show();
    }

    // MedicalHistoryFragment.MedicalHistoryListener methods
    @Override
    public void onMedicalRecordAdded() {
        Toast.makeText(this, "Đã thêm hồ sơ y tế thành công", Toast.LENGTH_SHORT).show();
    }

    // HealthSuggestionsFragment.HealthSuggestionsListener methods
    @Override
    public void onSuggestionSelected(String suggestionId) {
        Toast.makeText(this, "Đã chọn lời khuyên: " + suggestionId, Toast.LENGTH_SHORT).show();
    }

    // AnalysisAlertsFragment.AnalysisAlertsListener methods
    @Override
    public void onAlertDismissed(String alertId) {
        Toast.makeText(this, "Đã bỏ qua cảnh báo: " + alertId, Toast.LENGTH_SHORT).show();
    }

    // MenstrualCycleFragment.MenstrualCycleListener methods
    @Override
    public void onPeriodLogged(Date date) {
        Toast.makeText(this, "Đã ghi nhận chu kỳ thành công", Toast.LENGTH_SHORT).show();
    }
}

