package com.example.firstproject;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstproject.appointment.AppointmentBookingActivity;
import com.example.firstproject.doctor.DoctorConsultationActivity;
import com.example.firstproject.services.NotificationHelper;
import com.example.firstproject.services.NotificationReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;
import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity implements
        DashboardFragment.DashboardListener,
        PersonalInfoFragment.PersonalInfoListener,
        HealthTrackingFragment.HealthTrackingListener,
        RemindersFragment.RemindersListener,
        MedicalHistoryFragment.MedicalHistoryListener,
        AnalysisAlertsFragment.AnalysisAlertsListener,
        MenstrualCycleFragment.MenstrualCycleListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize notification channel for Android 8.0+
        NotificationHelper.createNotificationChannel(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý sức khỏe");

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

        if (savedInstanceState == null) {
            getSupportActionBar().setTitle("Tổng quan");
            loadFragment(new DashboardFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void showMoreOptions() {
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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

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

    @Override
    public void onHealthTrackingClicked() {
        bottomNavigationView.setSelectedItemId(R.id.nav_health);
        Toast.makeText(this, "Đang chuyển đến Theo dõi sức khỏe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHealthSuggestionsClicked() {
        // Load the HealthSuggestionsFragment
        Fragment fragment = new HealthSuggestionsFragment();
        getSupportActionBar().setTitle("Lời khuyên sức khỏe");
        loadFragment(fragment);
    }

    @Override
    public void onPersonalInfoSaved() {
        Toast.makeText(this, "Thông tin cá nhân đã được lưu thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeasurementAdded() {
        Toast.makeText(this, "Đã thêm chỉ số mới thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReminderAdded() {
        Toast.makeText(this, "Đã thêm nhắc nhở mới thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMedicalRecordAdded() {
        Toast.makeText(this, "Đã thêm hồ sơ y tế thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlertDismissed(String alertId) {
        Toast.makeText(this, "Đã bỏ qua cảnh báo: " + alertId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeriodLogged(Date date) {
        Toast.makeText(this, "Đã ghi nhận chu kỳ thành công", Toast.LENGTH_SHORT).show();
    }

    // New methods for menstrual cycle and doctor consultation
    public void onMenstrualCycleClicked() {
        Fragment fragment = new MenstrualCycleFragment();
        getSupportActionBar().setTitle("Chu kỳ kinh nguyệt");
        loadFragment(fragment);
    }

    public void onConsultDoctorClicked() {
        try {
            // Launch the doctor consultation activity
            Intent intent = new Intent(this, DoctorConsultationActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể mở trang tư vấn bác sĩ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onBookAppointmentClicked() {
        try {
            // Launch the appointment booking activity
            Intent intent = new Intent(this, AppointmentBookingActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể mở trang đặt lịch khám: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
