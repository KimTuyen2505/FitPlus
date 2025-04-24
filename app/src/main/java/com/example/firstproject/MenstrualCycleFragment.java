package com.example.firstproject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstproject.dao.MenstrualCycleDAO;
import com.example.firstproject.dao.PregnancyDataDAO;
import com.example.firstproject.dao.PregnancyDiaryDAO;
import com.example.firstproject.dao.UltrasoundImageDAO;
import com.example.firstproject.models.MenstrualCycle;
import com.example.firstproject.models.PregnancyData;
import com.example.firstproject.models.PregnancyDiary;
import com.example.firstproject.models.UltrasoundImage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import android.app.Activity;

public class MenstrualCycleFragment extends Fragment {

    private MenstrualCycleListener listener;
    private CalendarView calendarMenstrualCycle;
    private Button btnLogPeriod;
    private TextView textCycleInfo, textCycleAlerts;
    private TextView tabCycleTracking, tabPrediction;
    private MenstrualCycleDAO menstrualCycleDAO;
    private PregnancyDataDAO pregnancyDataDAO;
    private PregnancyDiaryDAO pregnancyDiaryDAO;
    private UltrasoundImageDAO ultrasoundImageDAO;
    private SimpleDateFormat dateFormat;
    private MenstrualCycle currentCycle;
    private PregnancyData currentPregnancy;

    // Flow and pain level indicators
    private ImageView[] flowLevelIndicators = new ImageView[5];
    private ImageView[] painLevelIndicators = new ImageView[5];

    // Current selected levels
    private int currentFlowLevel = 3;
    private int currentPainLevel = 3;

    // Add these new instance variables at the top of the class with other instance variables
    private TextView textBabyName, textDueDate, textBabySize, textBabyWeight, textPregnancyDays, textPregnancyWeeks;
    private View pregnancyProfileView;
    private Button btnDetails, btnImages, btnNames, btnDiary, btnConfirmPregnancy;
    private boolean isPregnancyDataSet = false;
    private boolean isPregnancyConfirmed = false;

    // Add these new fields for pregnancy tracking
    private Button btnCheckupSchedule;
    private Date pregnancyStartDate;
    private String babyName = "Bé Yêu";
    private View mainView;

    private static final int REQUEST_IMAGE_PICK = 100;

    public interface MenstrualCycleListener {
        void onPeriodLogged(Date date);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MenstrualCycleListener) {
            listener = (MenstrualCycleListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MenstrualCycleListener");
        }
        menstrualCycleDAO = new MenstrualCycleDAO(context);
        pregnancyDataDAO = new PregnancyDataDAO(context);
        pregnancyDiaryDAO = new PregnancyDiaryDAO(context);
        ultrasoundImageDAO = new UltrasoundImageDAO(context);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public void onResume() {
        super.onResume();
        menstrualCycleDAO.open();
        pregnancyDataDAO.open();
        pregnancyDiaryDAO.open();
        ultrasoundImageDAO.open();
        loadCycleData();
        loadPregnancyData();
    }

    @Override
    public void onPause() {
        super.onPause();
        menstrualCycleDAO.close();
        pregnancyDataDAO.close();
        pregnancyDiaryDAO.close();
        ultrasoundImageDAO.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_menstrual_cycle, container, false);

        calendarMenstrualCycle = mainView.findViewById(R.id.calendar_menstrual_cycle);
        btnLogPeriod = mainView.findViewById(R.id.btn_log_period);
        textCycleInfo = mainView.findViewById(R.id.text_cycle_info);
        textCycleAlerts = mainView.findViewById(R.id.text_cycle_alerts);
        tabCycleTracking = mainView.findViewById(R.id.tab_cycle_tracking);
        tabPrediction = mainView.findViewById(R.id.tab_prediction);

        // Initialize flow level indicators
        flowLevelIndicators[0] = mainView.findViewById(R.id.flow_level_1);
        flowLevelIndicators[1] = mainView.findViewById(R.id.flow_level_2);
        flowLevelIndicators[2] = mainView.findViewById(R.id.flow_level_3);
        flowLevelIndicators[3] = mainView.findViewById(R.id.flow_level_4);
        flowLevelIndicators[4] = mainView.findViewById(R.id.flow_level_5);

        // Initialize pain level indicators
        painLevelIndicators[0] = mainView.findViewById(R.id.pain_level_1);
        painLevelIndicators[1] = mainView.findViewById(R.id.pain_level_2);
        painLevelIndicators[2] = mainView.findViewById(R.id.pain_level_3);
        painLevelIndicators[3] = mainView.findViewById(R.id.pain_level_4);
        painLevelIndicators[4] = mainView.findViewById(R.id.pain_level_5);

        // Set up click listeners for flow level indicators
        for (int i = 0; i < flowLevelIndicators.length; i++) {
            final int level = i + 1;
            flowLevelIndicators[i].setOnClickListener(v -> {
                updateFlowLevel(level);
                animateIconClick(v);
            });
        }

        // Set up click listeners for pain level indicators
        for (int i = 0; i < painLevelIndicators.length; i++) {
            final int level = i + 1;
            painLevelIndicators[i].setOnClickListener(v -> {
                updatePainLevel(level);
                animateIconClick(v);
            });
        }

        calendarMenstrualCycle.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            showDateInfoPopup(selectedDate.getTime());
        });

        btnLogPeriod.setOnClickListener(v -> {
            animateButtonClick(btnLogPeriod);
            showLogPeriodDialog();
        });

        // Set up tab click listeners
        tabCycleTracking.setOnClickListener(v -> {
            setActiveTab(tabCycleTracking);
            setInactiveTab(tabPrediction);
            setupCycleTrackingView();
        });

        tabPrediction.setOnClickListener(v -> {
            setActiveTab(tabPrediction);
            setInactiveTab(tabCycleTracking);
            setupPregnancyPrepView();
        });

        // Initialize with cycle tracking view
        setupCycleTrackingView();
        mainView.findViewById(R.id.pregnancy_prep_container).setVisibility(View.GONE);

        return mainView;
    }

    private void loadPregnancyData() {
        currentPregnancy = pregnancyDataDAO.getLatestPregnancyData();
        if (currentPregnancy != null) {
            isPregnancyConfirmed = currentPregnancy.isConfirmed();
            pregnancyStartDate = currentPregnancy.getStartDate();
            babyName = currentPregnancy.getBabyName();
            isPregnancyDataSet = false; // Force refresh of UI data
        }
    }

    // Replace the setupPregnancyPrepView method with this updated version
    private void setupPregnancyPrepView() {
        // Hide period tracking elements
        mainView.findViewById(R.id.calendar_container).setVisibility(View.GONE);
        mainView.findViewById(R.id.legend_container).setVisibility(View.GONE);
        mainView.findViewById(R.id.period_logging_card).setVisibility(View.GONE);
        mainView.findViewById(R.id.cycle_info_card).setVisibility(View.GONE);

        // Show pregnancy prep elements
        mainView.findViewById(R.id.pregnancy_prep_container).setVisibility(View.VISIBLE);

        // Initialize pregnancy profile views if not already done
        if (pregnancyProfileView == null) {
            pregnancyProfileView = mainView.findViewById(R.id.pregnancy_profile);
            textBabyName = mainView.findViewById(R.id.text_baby_name);
            textDueDate = mainView.findViewById(R.id.text_due_date);
            textBabySize = mainView.findViewById(R.id.text_baby_size);
            textBabyWeight = mainView.findViewById(R.id.text_baby_weight);
            textPregnancyDays = mainView.findViewById(R.id.text_pregnancy_days);
            textPregnancyWeeks = mainView.findViewById(R.id.text_pregnancy_weeks);

            btnDetails = mainView.findViewById(R.id.btn_details);
            btnImages = mainView.findViewById(R.id.btn_images);
            btnNames = mainView.findViewById(R.id.btn_names);
            btnDiary = mainView.findViewById(R.id.btn_diary);
            btnConfirmPregnancy = mainView.findViewById(R.id.btn_confirm_pregnancy);

            // Initialize additional buttons
            btnCheckupSchedule = mainView.findViewById(R.id.btn_checkup_schedule);

            // Set up button click listeners
            btnDetails.setOnClickListener(v -> showPregnancyDetails());
            btnImages.setOnClickListener(v -> showPregnancyImages());
            btnNames.setOnClickListener(v -> showBabyNames());
            btnDiary.setOnClickListener(v -> showPregnancyDiary());
            btnConfirmPregnancy.setOnClickListener(v -> confirmPregnancy());

            // Set up additional button click listeners
            if (btnCheckupSchedule != null) btnCheckupSchedule.setOnClickListener(v -> showCheckupSchedule());

            // Set up bottom navigation buttons
            mainView.findViewById(R.id.btn_nav_prep).setOnClickListener(v -> Toast.makeText(getContext(), "Chuẩn bị", Toast.LENGTH_SHORT).show());
            mainView.findViewById(R.id.btn_nav_sounds).setOnClickListener(v -> Toast.makeText(getContext(), "Âm đáng yêu", Toast.LENGTH_SHORT).show());
            mainView.findViewById(R.id.btn_nav_videos).setOnClickListener(v -> Toast.makeText(getContext(), "Video bé ích", Toast.LENGTH_SHORT).show());
            mainView.findViewById(R.id.btn_nav_settings).setOnClickListener(v -> Toast.makeText(getContext(), "Cài đặt", Toast.LENGTH_SHORT).show());
        }

        // Update UI based on pregnancy confirmation status
        updatePregnancyUI();
    }

    // Add this method to update the UI based on pregnancy confirmation status
    private void updatePregnancyUI() {
        if (isPregnancyConfirmed) {
            // Show pregnancy tracking UI
            if (pregnancyProfileView != null) {
                pregnancyProfileView.setVisibility(View.VISIBLE);
            }
            if (btnConfirmPregnancy != null) {
                btnConfirmPregnancy.setVisibility(View.GONE);
            }

            View featuresGrid = mainView.findViewById(R.id.pregnancy_features_grid);
            View additionalFeatures = mainView.findViewById(R.id.pregnancy_additional_features);

            if (featuresGrid != null) {
                featuresGrid.setVisibility(View.VISIBLE);
            }
            if (additionalFeatures != null) {
                additionalFeatures.setVisibility(View.VISIBLE);
            }

            // Set pregnancy data if not already set
            if (!isPregnancyDataSet) {
                setPregnancyData();
            }
        } else {
            // Show pregnancy confirmation UI
            if (pregnancyProfileView != null) {
                pregnancyProfileView.setVisibility(View.GONE);
            }
            if (btnConfirmPregnancy != null) {
                btnConfirmPregnancy.setVisibility(View.VISIBLE);
            }

            View featuresGrid = mainView.findViewById(R.id.pregnancy_features_grid);
            View additionalFeatures = mainView.findViewById(R.id.pregnancy_additional_features);

            if (featuresGrid != null) {
                featuresGrid.setVisibility(View.GONE);
            }
            if (additionalFeatures != null) {
                additionalFeatures.setVisibility(View.GONE);
            }
        }
    }

    // Add this method to confirm pregnancy
    private void confirmPregnancy() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_pregnancy, null);
        builder.setView(dialogView);

        final EditText editLastPeriod = dialogView.findViewById(R.id.edit_last_period);
        final EditText editBabyName = dialogView.findViewById(R.id.edit_baby_name);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Set up date picker for last period
        editLastPeriod.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        editLastPeriod.setText(dateFormat.format(calendar.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Set default date to today
        editLastPeriod.setText(dateFormat.format(new Date()));

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            try {
                // Parse last period date
                Date lastPeriodDate = dateFormat.parse(editLastPeriod.getText().toString());
                pregnancyStartDate = lastPeriodDate;

                // Save baby name if provided
                String name = editBabyName.getText().toString().trim();
                if (!name.isEmpty()) {
                    babyName = name;
                }

                // Create and save pregnancy data
                PregnancyData pregnancyData = new PregnancyData(pregnancyStartDate, babyName);
                pregnancyData.setUserId(1); // Default user ID
                pregnancyData.setConfirmed(true);

                // Save to database
                pregnancyDataDAO.open();
                long id = pregnancyDataDAO.insertPregnancyData(pregnancyData);
                pregnancyData.setId(id);
                currentPregnancy = pregnancyData;

                // Set pregnancy as confirmed
                isPregnancyConfirmed = true;
                updatePregnancyUI();

                Toast.makeText(getContext(), "Đã xác nhận thai kỳ!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void setupCycleTrackingView() {
        // Show period tracking elements
        mainView.findViewById(R.id.calendar_container).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.legend_container).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.period_logging_card).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.cycle_info_card).setVisibility(View.VISIBLE);

        // Hide pregnancy prep elements
        mainView.findViewById(R.id.pregnancy_prep_container).setVisibility(View.GONE);
    }

    private void animateIconClick(View view) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_animation);
        view.startAnimation(scaleAnimation);
    }

    private void animateButtonClick(Button button) {
        int colorFrom = ContextCompat.getColor(getContext(), R.color.pink_primary);
        int colorTo = ContextCompat.getColor(getContext(), R.color.pink_dark);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(animator -> button.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();

        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() ->
                button.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        ).start();
    }

    private void setActiveTab(TextView tab) {
        tab.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_light));
        tab.setTextColor(ContextCompat.getColor(getContext(), R.color.pink_primary));
    }

    private void setInactiveTab(TextView tab) {
        tab.setBackgroundColor(Color.WHITE);
        tab.setTextColor(ContextCompat.getColor(getContext(), R.color.text_tertiary));
    }

    private void updateFlowLevel(int level) {
        currentFlowLevel = level;

        // Update UI to reflect selected level
        for (int i = 0; i < flowLevelIndicators.length; i++) {
            if (i < level) {
                flowLevelIndicators[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.pink_primary));
            } else {
                flowLevelIndicators[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.light_gray));
            }
        }

        // Show tips based on flow level
        showFlowLevelTips(level);
    }

    private void updatePainLevel(int level) {
        currentPainLevel = level;

        // Update UI to reflect selected level
        for (int i = 0; i < painLevelIndicators.length; i++) {
            if (i < level) {
                painLevelIndicators[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.pink_primary));
            } else {
                painLevelIndicators[i].setColorFilter(ContextCompat.getColor(getContext(), R.color.light_gray));
            }
        }

        // Show tips based on pain level
        showPainLevelTips(level);
    }

    private void showFlowLevelTips(int level) {
        String tip = "";
        if (level <= 2) {
            tip = getString(R.string.flow_level_light_tip);
        } else if (level == 3) {
            tip = getString(R.string.flow_level_normal_tip);
        } else if (level == 4) {
            tip = getString(R.string.flow_level_heavy_tip);
        } else {
            tip = getString(R.string.flow_level_very_heavy_tip);
        }

        showTipDialog(getString(R.string.flow_level_tip_title), tip);
    }

    private void showPainLevelTips(int level) {
        String tip = "";
        if (level <= 2) {
            tip = getString(R.string.pain_level_mild_tip);
        } else if (level == 3) {
            tip = getString(R.string.pain_level_moderate_tip);
        } else if (level == 4) {
            tip = getString(R.string.pain_level_severe_tip);
        } else {
            tip = getString(R.string.pain_level_very_severe_tip);
        }

        showTipDialog(getString(R.string.pain_level_tip_title), tip);
    }

    private void showTipDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.tip_understood, null)
                .setIcon(R.drawable.ic_period_tracker);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDateInfoPopup(Date date) {
        // Check if this date is in a special period (menstruation, ovulation, etc.)
        // and show appropriate information

        if (currentCycle != null && currentCycle.getStartDate() != null) {
            Calendar cycleStart = Calendar.getInstance();
            cycleStart.setTime(currentCycle.getStartDate());

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTime(date);

            // Check if selected date is during menstruation (typically first 5 days)
            if (isSameDay(selectedDate, cycleStart) ||
                    (daysBetween(cycleStart, selectedDate) > 0 && daysBetween(cycleStart, selectedDate) < 5)) {
                Toast.makeText(getContext(), "Ngày trong kỳ kinh nguyệt", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if selected date is during ovulation (typically around day 14)
            if (daysBetween(cycleStart, selectedDate) >= 12 && daysBetween(cycleStart, selectedDate) <= 16) {
                Toast.makeText(getContext(), "Ngày rụng trứng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if selected date is in fertile window (typically days 10-17)
            if (daysBetween(cycleStart, selectedDate) >= 10 && daysBetween(cycleStart, selectedDate) <= 17) {
                Toast.makeText(getContext(), "Ngày có khả năng thụ thai cao", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // If no special period, just show the date
        Toast.makeText(getContext(), "Ngày " + dateFormat.format(date), Toast.LENGTH_SHORT).show();
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private int daysBetween(Calendar startDate, Calendar endDate) {
        long endInstant = endDate.getTimeInMillis();
        long startInstant = startDate.getTimeInMillis();
        return (int) ((endInstant - startInstant) / (1000 * 60 * 60 * 24));
    }

    private void loadCycleData() {
        currentCycle = menstrualCycleDAO.getLatestMenstrualCycle();
        if (currentCycle != null) {
            updateCycleInfo();
        } else {
            textCycleInfo.setText("Chưa có dữ liệu chu kỳ");
            textCycleAlerts.setText("Hãy ghi lại chu kỳ đầu tiên của bạn");
        }
    }

    private void updateCycleInfo() {
        if (currentCycle == null) return;

        String startDate = dateFormat.format(currentCycle.getStartDate());
        String endDate = currentCycle.getEndDate() != null ?
                dateFormat.format(currentCycle.getEndDate()) : "Đang diễn ra";

        int cycleLength = currentCycle.calculatePeriodLength();

        StringBuilder info = new StringBuilder();
        info.append("Chu kỳ gần nhất:\n");
        info.append("Bắt đầu: ").append(startDate).append("\n");
        info.append("Kết thúc: ").append(endDate).append("\n");
        if (cycleLength > 0) {
            info.append("Độ dài chu kỳ: ").append(cycleLength).append(" ngày\n");
        }
        if (currentCycle.getSymptoms() != null && !currentCycle.getSymptoms().isEmpty()) {
            info.append("Triệu chứng: ").append(currentCycle.getSymptoms());
        }

        textCycleInfo.setText(info.toString());

        // Calculate and show alerts
        updateCycleAlerts();
    }

    private void updateCycleAlerts() {
        if (currentCycle == null || currentCycle.getStartDate() == null) return;

        Calendar today = Calendar.getInstance();
        Calendar nextPeriod = Calendar.getInstance();
        nextPeriod.setTime(currentCycle.getStartDate());
        nextPeriod.add(Calendar.DAY_OF_MONTH, 28); // Assuming 28-day cycle

        long daysDiff = (nextPeriod.getTimeInMillis() - today.getTimeInMillis()) /
                (24 * 60 * 60 * 1000);

        if (daysDiff <= 3 && daysDiff >= 0) {
            textCycleAlerts.setText("Chu kỳ tiếp theo dự kiến sẽ bắt đầu trong " +
                    daysDiff + " ngày nữa");
        } else if (daysDiff < 0) {
            textCycleAlerts.setText("Chu kỳ tiếp theo đã quá hạn " +
                    Math.abs(daysDiff) + " ngày");
        } else {
            textCycleAlerts.setText("Chu kỳ tiếp theo dự kiến sau " + daysDiff + " ngày");
        }
    }

    private void showLogPeriodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_log_period, null);
        builder.setView(dialogView);

        final CalendarView calendarView = dialogView.findViewById(R.id.calendar_select_date);
        final EditText editSymptoms = dialogView.findViewById(R.id.edit_symptoms);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Style the dialog
        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).setBackgroundResource(R.drawable.rounded_dialog_background);
        }

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Date[] selectedDate = {new Date()};
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate[0] = calendar.getTime();
        });

        btnSave.setOnClickListener(v -> {
            String symptoms = editSymptoms.getText().toString();

            // If there's an ongoing cycle, end it
            if (currentCycle != null && currentCycle.getEndDate() == null) {
                currentCycle.setEndDate(new Date());
                menstrualCycleDAO.updateMenstrualCycle(currentCycle);
            }

            // Start new cycle
            MenstrualCycle newCycle = new MenstrualCycle(selectedDate[0], null, symptoms);
            // Add flow and pain levels to symptoms
            String symptomDetails = symptoms;
            if (!symptomDetails.isEmpty()) {
                symptomDetails += "\n";
            }
            symptomDetails += "Lượng kinh: " + currentFlowLevel + "/5\n";
            symptomDetails += "Đau bụng: " + currentPainLevel + "/5";
            newCycle.setSymptoms(symptomDetails);

            long id = menstrualCycleDAO.insertMenstrualCycle(newCycle);
            newCycle.setId(id);
            currentCycle = newCycle;

            loadCycleData();

            if (listener != null) {
                listener.onPeriodLogged(selectedDate[0]);
            }

            // Show success animation
            Toast.makeText(getContext(), "Đã ghi nhận chu kỳ thành công!", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    // Replace the setDummyPregnancyData method with this updated version
    private void setPregnancyData() {
        if (currentPregnancy != null) {
            // Use data from database
            textBabyName.setText(currentPregnancy.getBabyName());
            textDueDate.setText("Dự sinh: " + dateFormat.format(currentPregnancy.getDueDate()));

            int[] duration = currentPregnancy.getDurationWeeksAndDays();
            textPregnancyDays.setText(currentPregnancy.getDurationDays() + " Ngày");
            textPregnancyWeeks.setText(duration[0] + " tuần " + duration[1] + " ngày");

            textBabySize.setText(currentPregnancy.getBabySize());
            textBabyWeight.setText(currentPregnancy.getBabyWeight());
        } else {
            // Set baby name
            textBabyName.setText(babyName);

            // Calculate due date (280 days from pregnancy start date)
            Calendar dueDate = Calendar.getInstance();
            if (pregnancyStartDate != null) {
                dueDate.setTime(pregnancyStartDate);
                dueDate.add(Calendar.DAY_OF_YEAR, 280);
            } else {
                // Default to 9 months from now if no start date
                dueDate.add(Calendar.MONTH, 9);
            }
            String dueDateStr = dateFormat.format(dueDate.getTime());
            textDueDate.setText("Dự sinh: " + dueDateStr);

            // Calculate pregnancy duration
            int pregnancyDays = 0;
            int pregnancyWeeks = 0;
            int pregnancyDaysRemainder = 0;

            if (pregnancyStartDate != null) {
                long diffInMillis = System.currentTimeMillis() - pregnancyStartDate.getTime();
                pregnancyDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
                pregnancyWeeks = pregnancyDays / 7;
                pregnancyDaysRemainder = pregnancyDays % 7;
            } else {
                // Default values if no start date
                pregnancyDays = 23;
                pregnancyWeeks = 3;
                pregnancyDaysRemainder = 2;
            }

            // Set baby size and weight based on pregnancy week
            String babySize = getBabySizeForWeek(pregnancyWeeks);
            String babyWeight = getBabyWeightForWeek(pregnancyWeeks);

            textBabySize.setText(babySize);
            textBabyWeight.setText(babyWeight);

            // Set pregnancy duration
            textPregnancyDays.setText(pregnancyDays + " Ngày");
            textPregnancyWeeks.setText(pregnancyWeeks + " tuần " + pregnancyDaysRemainder + " ngày");
        }

        isPregnancyDataSet = true;
    }

    // Add these helper methods to get baby size and weight by week
    private String getBabySizeForWeek(int week) {
        // Default size if week is out of range
        if (week < 5 || week > 40) return "N/A";

        // Array of baby sizes by week (starting from week 5)
        String[] babySizes = {
                "0.1 cm", // Week 5
                "0.3 cm", // Week 6
                "1 cm",   // Week 7
                "1.6 cm", // Week 8
                "2.3 cm", // Week 9
                "3.1 cm", // Week 10
                "4.1 cm", // Week 11
                "5.4 cm", // Week 12
                "7.4 cm", // Week 13
                "8.7 cm", // Week 14
                "10.1 cm", // Week 15
                "11.6 cm", // Week 16
                "13 cm",   // Week 17
                "14.2 cm", // Week 18
                "15.3 cm", // Week 19
                "16.4 cm", // Week 20
                "26.7 cm", // Week 21
                "27.8 cm", // Week 22
                "28.9 cm", // Week 23
                "30 cm",   // Week 24
                "34.6 cm", // Week 25
                "35.6 cm", // Week 26
                "36.6 cm", // Week 27
                "37.6 cm", // Week 28
                "38.6 cm", // Week 29
                "39.9 cm", // Week 30
                "41.1 cm", // Week 31
                "42.4 cm", // Week 32
                "43.7 cm", // Week 33
                "45 cm",   // Week 34
                "46.2 cm", // Week 35
                "47.4 cm", // Week 36
                "48.6 cm", // Week 37
                "49.8 cm", // Week 38
                "50.7 cm", // Week 39
                "51.2 cm"  // Week 40
        };

        if (week < 5) return "N/A";
        if (week > 40) return "51.2 cm";

        return babySizes[week - 5];
    }

    private String getBabyWeightForWeek(int week) {
        // Default weight if week is out of range
        if (week < 5 || week > 40) return "N/A";

        // Array of baby weights by week (starting from week 5)
        String[] babyWeights = {
                "< 1 g",  // Week 5
                "< 1 g",  // Week 6
                "1 g",    // Week 7
                "1 g",    // Week 8
                "2 g",    // Week 9
                "5 g",    // Week 10
                "10 g",   // Week 11
                "14 g",   // Week 12
                "23 g",   // Week 13
                "43 g",   // Week 14
                "70 g",   // Week 15
                "100 g",  // Week 16
                "140 g",  // Week 17
                "190 g",  // Week 18
                "240 g",  // Week 19
                "300 g",  // Week 20
                "360 g",  // Week 21
                "430 g",  // Week 22
                "501 g",  // Week 23
                "600 g",  // Week 24
                "660 g",  // Week 25
                "760 g",  // Week 26
                "875 g",  // Week 27
                "1000 g", // Week 28
                "1150 g", // Week 29
                "1300 g", // Week 30
                "1500 g", // Week 31
                "1700 g", // Week 32
                "1900 g", // Week 33
                "2150 g", // Week 34
                "2400 g", // Week 35
                "2600 g", // Week 36
                "2900 g", // Week 37
                "3100 g", // Week 38
                "3300 g", // Week 39
                "3400 g"  // Week 40
        };

        if (week < 5) return "N/A";
        if (week > 40) return "3400 g";

        return babyWeights[week - 5];
    }

    // Update the showPregnancyDetails method to show detailed information by week
    private void showPregnancyDetails() {
        // Calculate current pregnancy week
        int currentWeek = 19; // Default
        if (currentPregnancy != null) {
            int[] duration = currentPregnancy.getDurationWeeksAndDays();
            currentWeek = duration[0];
        } else if (pregnancyStartDate != null) {
            long diffInMillis = System.currentTimeMillis() - pregnancyStartDate.getTime();
            int pregnancyDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            currentWeek = pregnancyDays / 7;
        }

        // Get week-specific information
        String weekInfo = getPregnancyWeekInfo(currentWeek);
        String motherInfo = getMotherBodyChangesInfo(currentWeek);

        // Create and show dialog with detailed information
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pregnancy_details, null);
        builder.setView(dialogView);

        TextView textWeek = dialogView.findViewById(R.id.text_week);
        TextView textBabyDevelopment = dialogView.findViewById(R.id.text_baby_development);
        TextView textMotherChanges = dialogView.findViewById(R.id.text_mother_changes);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        textWeek.setText("Tuần thứ " + currentWeek);
        textBabyDevelopment.setText(weekInfo);
        textMotherChanges.setText(motherInfo);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    // Add these helper methods to get pregnancy information by week
    private String getPregnancyWeekInfo(int week) {
        // Default info if week is out of range
        if (week < 5 || week > 40) {
            return "Không có thông tin cho tuần này.";
        }

        // Information about baby development by week
        String[] weeklyInfo = {
                "Tuần 5: Thai nhi có kích thước như hạt vừng. Các cơ quan nội tạng bắt đầu hình thành.", // Week 5
                "Tuần 6: Tim thai bắt đầu đập. Các mầm tay chân đang hình thành.", // Week 6
                "Tuần 7: Não bộ phát triển nhanh. Mắt và tai bắt đầu hình thành.", // Week 7
                "Tuần 8: Tất cả các cơ quan chính đã hình thành. Thai nhi bắt đầu cử động.", // Week 8
                "Tuần 9: Các ngón tay và ngón chân đang phát triển. Thai nhi có thể cử động nhưng mẹ chưa cảm nhận được.", // Week 9
                "Tuần 10: Các cơ quan sinh dục bắt đầu phát triển. Xương bắt đầu cứng cáp hơn.", // Week 10
                "Tuần 11: Mặt thai nhi đã hình thành rõ ràng hơn. Các cơ quan tiếp tục phát triển.", // Week 11
                "Tuần 12: Thai nhi có thể đưa ngón tay vào miệng. Móng tay và móng chân bắt đầu mọc.", // Week 12
                "Tuần 13: Thai nhi có thể nhăn mặt và nhíu mày. Dây thanh quản đang phát triển.", // Week 13
                "Tuần 14: Thai nhi bắt đầu tập nuốt và hít nước ối. Có thể xác định giới tính qua siêu âm.", // Week 14
                "Tuần 15: Thai nhi có thể cảm nhận ánh sáng và phản ứng với âm thanh bên ngoài.", // Week 15
                "Tuần 16: Thai nhi có thể cử động đầu và cổ. Mắt có thể cảm nhận ánh sáng.", // Week 16
                "Tuần 17: Cơ thể thai nhi bắt đầu tích mỡ. Hệ miễn dịch đang phát triển.", // Week 17
                "Tuần 18: Thai nhi có thể nghe được âm thanh từ bên ngoài. Các giác quan đang phát triển.", // Week 18
                "Tuần 19: Lớp bảo vệ myelin bắt đầu bao quanh các dây thần kinh. Tai đã phát triển và bé có thể nghe được tiếng nói của mẹ.", // Week 19
                "Tuần 20: Siêu âm hình thái học được thực hiện. Lông mày và mi mắt đã hình thành.", // Week 20
                "Tuần 21: Thai nhi đã phát triển phản xạ nắm. Da bắt đầu dày lên.", // Week 21
                "Tuần 22: Các nếp nhăn trên da bắt đầu mờ đi khi thai nhi tích mỡ.", // Week 22
                "Tuần 23: Thai nhi có thể nghe được giọng nói và nhịp tim của mẹ.", // Week 23
                "Tuần 24: Phổi bắt đầu sản xuất chất surfactant, chuẩn bị cho việc hô hấp.", // Week 24
                "Tuần 25: Tất cả các giác quan đã phát triển. Thai nhi có thể phản ứng với ánh sáng và âm thanh.", // Week 25
                "Tuần 26: Mắt thai nhi bắt đầu mở và nhắm. Não bộ phát triển nhanh chóng.", // Week 26
                "Tuần 27: Thai nhi có thể nhận biết giọng nói của mẹ. Não bộ phát triển mạnh.", // Week 27
                "Tuần 28: Thai nhi có thể mở và nhắm mắt. Có thể mơ trong giấc ngủ REM.", // Week 28
                "Tuần 29: Xương đầu vẫn mềm để dễ dàng đi qua kênh sinh.", // Week 29
                "Tuần 30: Thai nhi có thể phân biệt ánh sáng và bóng tối. Móng tay đã mọc đến đầu ngón tay.", // Week 30
                "Tuần 31: Hệ thần kinh trung ương tiếp tục phát triển. Thai nhi tăng cân nhanh.", // Week 31
                "Tuần 32: Thai nhi tập thở bằng cách hít và thở ra nước ối.", // Week 32
                "Tuần 33: Hầu hết các cơ quan đã phát triển hoàn chỉnh, trừ phổi.", // Week 33
                "Tuần 34: Hệ thống miễn dịch đang phát triển. Thai nhi tích trữ sắt.", // Week 34
                "Tuần 35: Phổi gần như phát triển hoàn chỉnh. Thai nhi tiếp tục tăng cân.", // Week 35
                "Tuần 36: Thai nhi đã sẵn sàng cho việc sinh nở. Lớp mỡ dưới da đã dày.", // Week 36
                "Tuần 37: Thai nhi được coi là đủ tháng. Các cơ quan đã hoạt động đầy đủ.", // Week 37
                "Tuần 38: Thai nhi tiếp tục tăng cân và phát triển não bộ.", // Week 38
                "Tuần 39: Thai nhi đã sẵn sàng cho cuộc sống bên ngoài tử cung.", // Week 39
                "Tuần 40: Thai nhi đã phát triển hoàn chỉnh và sẵn sàng chào đời." // Week 40
        };

        return weeklyInfo[week - 5];
    }

    private String getMotherBodyChangesInfo(int week) {
        // Default info if week is out of range
        if (week < 5 || week > 40) {
            return "Không có thông tin cho tuần này.";
        }

        // Information about mother's body changes by week
        String[] motherChanges = {
                "Tuần 5: Buồn nôn, mệt mỏi và đau ngực có thể bắt đầu. Hormone hCG tăng cao.", // Week 5
                "Tuần 6: Buồn nôn buổi sáng có thể trở nên rõ rệt. Bạn có thể cần đi tiểu thường xuyên hơn.", // Week 6
                "Tuần 7: Tử cung bắt đầu to lên. Mệt mỏi và buồn nôn tiếp tục.", // Week 7
                "Tuần 8: Ngực tiếp tục to lên và đau. Có thể bị đầy hơi và táo bón.", // Week 8
                "Tuần 9: Tâm trạng thay đổi thất thường. Vòng eo bắt đầu to ra.", // Week 9
                "Tuần 10: Buồn nôn có thể bắt đầu giảm. Tóc và móng có thể mọc nhanh hơn.", // Week 10
                "Tuần 11: Tử cung đã to bằng quả cam. Lượng máu trong cơ thể tăng lên.", // Week 11
                "Tuần 12: Bụng bắt đầu nhô ra. Buồn nôn có thể giảm dần.", // Week 12
                "Tuần 13: Năng lượng bắt đầu trở lại. Bụng bắt đầu thấy rõ hơn.", // Week 13
                "Tuần 14: Có thể bắt đầu thấy đường đen dưới rốn. Da có thể sáng hơn.", // Week 14
                "Tuần 15: Mũi có thể bị nghẹt do tăng lưu lượng máu. Bụng rõ hơn.", // Week 15
                "Tuần 16: Có thể bắt đầu cảm nhận được cử động của thai nhi. Tóc và móng mọc nhanh.", // Week 16
                "Tuần 17: Tử cung đã nằm giữa rốn và xương mu. Cân nặng tăng đều.", // Week 17
                "Tuần 18: Có thể bị chuột rút ở chân. Bụng đã rõ ràng.", // Week 18
                "Tuần 19: Tử cung đã to bằng quả dưa hấu nhỏ. Có thể bị đau lưng.", // Week 19
                "Tuần 20: Rốn có thể bắt đầu lồi ra. Cử động thai nhi rõ ràng hơn.", // Week 20
                "Tuần 21: Có thể bị sưng mắt cá chân và bàn chân. Da bụng căng ra.", // Week 21
                "Tuần 22: Có thể bị đau dây chằng ở bụng. Tăng cân đều đặn.", // Week 22
                "Tuần 23: Có thể bị đau lưng và khó ngủ. Tử cung đã lên đến rốn.", // Week 23
                "Tuần 24: Có thể bị ợ nóng và khó tiêu. Bụng tiếp tục to ra.", // Week 24
                "Tuần 25: Có thể bị đau đầu và chóng mặt. Tăng cân nhanh hơn.", // Week 25
                "Tuần 26: Có thể bị phù chân và tay. Tử cung đã lên trên rốn.", // Week 26
                "Tuần 27: Có thể bị vết rạn da ở bụng và ngực. Khó ngủ tăng lên.", // Week 27
                "Tuần 28: Có thể bị chuột rút ở chân và đau lưng. Khó thở khi nằm ngửa.", // Week 28
                "Tuần 29: Tử cung đè lên dạ dày gây ợ nóng. Mệt mỏi tăng lên.", // Week 29
                "Tuần 30: Có thể bị khó ngủ và đau lưng. Bụng to rõ rệt.", // Week 30
                "Tuần 31: Có thể bị khó thở khi thai nhi đè lên cơ hoành. Sữa non có thể bắt đầu tiết ra.", // Week 31
                "Tuần 32: Có thể bị phù chân và tay. Khớp xương có thể đau nhức.", // Week 32
                "Tuần 33: Có thể bị co thắt tử cung giả. Khó ngủ tăng lên.", // Week 33
                "Tuần 34: Có thể bị đau lưng và khó thở. Bụng rất to.", // Week 34
                "Tuần 35: Có thể bị tiểu nhiều lần. Cơ thể chuẩn bị cho sinh nở.", // Week 35
                "Tuần 36: Thai nhi bắt đầu hạ xuống, giảm áp lực lên cơ hoành. Đi lại khó khăn.", // Week 36
                "Tuần 37: Có thể bị tiêu chảy nhẹ. Cổ tử cung bắt đầu mở.", // Week 37
                "Tuần 38: Có thể bị co thắt Braxton Hicks thường xuyên. Khó ngủ.", // Week 38
                "Tuần 39: Cổ tử cung tiếp tục mở và mỏng đi. Có thể thấy nút nhầy.", // Week 39
                "Tuần 40: Cơ thể hoàn toàn sẵn sàng cho sinh nở. Chờ đợi những cơn co thắt thật sự." // Week 40
        };

        return motherChanges[week - 5];
    }

    // Update the showPregnancyImages method to handle ultrasound image uploads
    private void showPregnancyImages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pregnancy_images, null);
        builder.setView(dialogView);

        Button btnUpload = dialogView.findViewById(R.id.btn_upload_image);
        Button btnClose = dialogView.findViewById(R.id.btn_close);
        RecyclerView recyclerImages = dialogView.findViewById(R.id.recycler_images);

        // Set up RecyclerView for ultrasound images
        recyclerImages.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get ultrasound images from database
        List<UltrasoundImage> ultrasoundImages = new ArrayList<>();
        if (currentPregnancy != null) {
            ultrasoundImageDAO.open();
            ultrasoundImages = ultrasoundImageDAO.getAllUltrasoundImagesByPregnancyId(currentPregnancy.getId());
        }

        // Create adapter for ultrasound images
        UltrasoundImageAdapter adapter = new UltrasoundImageAdapter(ultrasoundImages);
        recyclerImages.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnUpload.setOnClickListener(v -> {
            // Open image picker
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    // Add this inner class for the ultrasound image adapter
    private class UltrasoundImageAdapter extends RecyclerView.Adapter<UltrasoundImageAdapter.ImageViewHolder> {
        private List<UltrasoundImage> images;

        public UltrasoundImageAdapter(List<UltrasoundImage> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            UltrasoundImage image = images.get(position);

            // Format date
            String dateStr = dateFormat.format(image.getImageDate());

            // Set text
            holder.textTitle.setText("Siêu âm tuần " + image.getPregnancyWeek());
            holder.textSubtitle.setText(dateStr);

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                // Show image details
                Toast.makeText(getContext(), "Đang mở hình ảnh siêu âm", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            TextView textTitle;
            TextView textSubtitle;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(android.R.id.text1);
                textSubtitle = itemView.findViewById(android.R.id.text2);
            }
        }
    }

    // Update the showBabyNames method to provide name suggestions
    private void showBabyNames() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_baby_names, null);
        builder.setView(dialogView);

        EditText editSearch = dialogView.findViewById(R.id.edit_search);
        RadioGroup radioGender = dialogView.findViewById(R.id.radio_gender);
        RecyclerView recyclerNames = dialogView.findViewById(R.id.recycler_names);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        // Set up RecyclerView for baby names
        recyclerNames.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> boyNames = Arrays.asList("An Khang", "Bảo Hân", "Chí Kiên", "Duy Khánh", "Gia Huy");
        List<String> girlNames = Arrays.asList("An Nhiên", "Bảo Ngọc", "Cẩm Tú", "Diệu Linh", "Gia Hân");

        // Default to boy names
        BabyNameAdapter adapter = new BabyNameAdapter(boyNames);
        recyclerNames.setAdapter(adapter);

        // Handle gender selection
        radioGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_boy) {
                adapter.updateNames(boyNames);
            } else {
                adapter.updateNames(girlNames);
            }
        });

        // Handle search
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase().trim();
                List<String> filteredNames;

                if (radioGender.getCheckedRadioButtonId() == R.id.radio_boy) {
                    filteredNames = boyNames.stream()
                            .filter(name -> name.toLowerCase().contains(query))
                            .collect(Collectors.toList());
                } else {
                    filteredNames = girlNames.stream()
                            .filter(name -> name.toLowerCase().contains(query))
                            .collect(Collectors.toList());
                }

                adapter.updateNames(filteredNames);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String selectedName = adapter.getSelectedName();
            if (selectedName != null && !selectedName.isEmpty()) {
                babyName = selectedName;

                // Update UI
                if (textBabyName != null) {
                    textBabyName.setText(babyName);
                }

                // Update database if pregnancy exists
                if (currentPregnancy != null) {
                    currentPregnancy.setBabyName(babyName);
                    pregnancyDataDAO.open();
                    pregnancyDataDAO.updatePregnancyData(currentPregnancy);
                }

                Toast.makeText(getContext(), "Đã lưu tên bé: " + babyName, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn một tên", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    // Add this inner class for the baby name adapter
    private class BabyNameAdapter extends RecyclerView.Adapter<BabyNameAdapter.NameViewHolder> {
        private List<String> names;
        private int selectedPosition = -1;

        public BabyNameAdapter(List<String> names) {
            this.names = names;
        }

        @NonNull
        @Override
        public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new NameViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
            holder.textName.setText(names.get(position));

            // Highlight selected item
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_light));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Update UI
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
            });
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        public void updateNames(List<String> newNames) {
            this.names = newNames;
            selectedPosition = -1;
            notifyDataSetChanged();
        }

        public String getSelectedName() {
            if (selectedPosition >= 0 && selectedPosition < names.size()) {
                return names.get(selectedPosition);
            }
            return null;
        }

        class NameViewHolder extends RecyclerView.ViewHolder {
            TextView textName;

            public NameViewHolder(@NonNull View itemView) {
                super(itemView);
                textName = itemView.findViewById(android.R.id.text1);
            }
        }
    }

    // Update the showPregnancyDiary method to handle diary entries
    private void showPregnancyDiary() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pregnancy_diary, null);
        builder.setView(dialogView);

        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editContent = dialogView.findViewById(R.id.edit_content);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String content = editContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate current pregnancy week
            int currentWeek = 3; // Default
            if (currentPregnancy != null) {
                int[] duration = currentPregnancy.getDurationWeeksAndDays();
                currentWeek = duration[0];
            } else if (pregnancyStartDate != null) {
                long diffInMillis = System.currentTimeMillis() - pregnancyStartDate.getTime();
                int pregnancyDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
                currentWeek = pregnancyDays / 7;
            }

            // Create diary entry
            if (currentPregnancy != null) {
                PregnancyDiary diary = new PregnancyDiary(currentPregnancy.getId(), title, content, currentWeek);

                // Save to database
                pregnancyDiaryDAO.open();
                long id = pregnancyDiaryDAO.insertDiaryEntry(diary);

                if (id > 0) {
                    Toast.makeText(getContext(), "Đã lưu nhật ký thai kỳ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lưu nhật ký", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Vui lòng xác nhận thai kỳ trước khi lưu nhật ký", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void showCheckupSchedule() {
        // Calculate current pregnancy week
        int currentWeek = 3; // Default
        if (currentPregnancy != null) {
            int[] duration = currentPregnancy.getDurationWeeksAndDays();
            currentWeek = duration[0];
        } else if (pregnancyStartDate != null) {
            long diffInMillis = System.currentTimeMillis() - pregnancyStartDate.getTime();
            int pregnancyDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            currentWeek = pregnancyDays / 7;
        }

        StringBuilder scheduleInfo = new StringBuilder();
        scheduleInfo.append("Lịch khám thai và xét nghiệm:\n\n");

        if (currentWeek < 12) {
            scheduleInfo.append("• Tuần 11-13: Siêu âm kỳ đầu và xét nghiệm Double Test\n");
        }

        if (currentWeek < 20) {
            scheduleInfo.append("• Tuần 18-22: Siêu âm hình thái học và xét nghiệm Triple Test\n");
        }

        if (currentWeek < 28) {
            scheduleInfo.append("• Tuần 24-28: Xét nghiệm đường huyết\n");
        }

        if (currentWeek < 36) {
            scheduleInfo.append("• Tuần 30-32: Siêu âm đánh giá sự phát triển của thai nhi\n");
        }

        scheduleInfo.append("\nLịch khám định kỳ:\n");
        scheduleInfo.append("• Đến tuần 28: Khám 4 tuần/lần\n");
        scheduleInfo.append("• Tuần 28-36: Khám 2 tuần/lần\n");
        scheduleInfo.append("• Sau tuần 36: Khám 1 tuần/lần\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Lịch khám thai")
                .setMessage(scheduleInfo.toString())
                .setPositiveButton("Đóng", null)
                .setIcon(R.drawable.ic_calendar);
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            // Save ultrasound image to database if pregnancy exists
            if (currentPregnancy != null && imageUri != null) {
                // Calculate current pregnancy week
                int currentWeek = 3; // Default
                int[] duration = currentPregnancy.getDurationWeeksAndDays();
                currentWeek = duration[0];

                // Create ultrasound image
                UltrasoundImage image = new UltrasoundImage(
                        currentPregnancy.getId(),
                        imageUri.toString(),
                        currentWeek,
                        "Siêu âm tuần " + currentWeek
                );

                // Save to database
                ultrasoundImageDAO.open();
                long id = ultrasoundImageDAO.insertUltrasoundImage(image);

                if (id > 0) {
                    Toast.makeText(getContext(), "Đã lưu hình ảnh siêu âm", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lưu hình ảnh", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Vui lòng xác nhận thai kỳ trước khi lưu hình ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
