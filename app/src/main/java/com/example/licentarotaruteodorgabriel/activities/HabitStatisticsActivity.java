package com.example.licentarotaruteodorgabriel.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.classes.achievement_classes.GeneralAchievement;
import com.example.licentarotaruteodorgabriel.classes.achievement_classes.StreakAchievement;
import com.example.licentarotaruteodorgabriel.classes.achievement_classes.StrengthAchievement;
import com.example.licentarotaruteodorgabriel.data.AchievementsReader;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateConverter;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;
import com.example.licentarotaruteodorgabriel.habit_charts.CompletionsChartsManager;
import com.example.licentarotaruteodorgabriel.habit_charts.HistoryChartsManager;
import com.example.licentarotaruteodorgabriel.habit_charts.ScoreChartsManager;
import com.example.licentarotaruteodorgabriel.habit_charts.StreaksChartsManager;
import com.example.licentarotaruteodorgabriel.habit_charts.StrengthChartsManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.scores.HabitScore;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.Calculations;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfMonthDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfWeekDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitAverageStreak;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitBestStreak;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitCurrentStreak;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitPrevStreaksVsMissesDiff;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HabitStatisticsActivity extends AppCompatActivity {

    private Habit receivedHabit;
    private TextView tvStatisticsHabitName;
    private TextView tvStatisticsHabitFrequency;
    private TextView tvStatisticsHabitDescription;
    private TextView tvStatisticsHabitStartDate;
    private TextView tvStatisticsHabitDaysPassed;
    private TextView tvStatisticsHabitCategory;
    private TextView tvStatisticsHabitDifficulty;

    //SCORE
    private PieChart pieChartHabitScore;

    // NUMERICAL STATS
    private ConstraintLayout constraintLayoutNumericalStats;
    private TextView tvTotalNumerical;
    private TextView tvAverageNumerical;
    private TextView tvHighestNumerical;
    private TextView tvLowestNumerical;

    // STREAKS
    private MaterialCardView cardViewStreaks;
    private TextView tvStatisticsHabitStreaksTag;
    //    private ImageView imageViewStreaksTag;
    private ConstraintLayout constraintLayoutStreaksStatistics;
    private HorizontalBarChart horizontalBarChartStreaks;
    private LineChart lineChartStreaksEvolution;

    // COMPLETIONS
    private MaterialCardView cardViewCompletions;
    private TextView tvStatisticsHabitCompletionsTag;
    private ConstraintLayout constraintLayoutCompletionsStatistics;
    private PieChart pieChartCompletions;
    private BarChart barChartMonthlyCompletions;
    private TextView tvStatisticsCurrMonthPercents;
    private TextView tvStatisticsCurrMonthCompletionPercent;
    private TextView tvStatisticsCurrMonthMissPercent;
    private TextView tvStatisticsAvgMonthsPercents;
    private TextView tvStatisticsAvgMonthsCompletionPercent;
    private TextView tvStatisticsAvgMonthsMissPercent;

    // STRENGTH
    private MaterialCardView cardViewStrength;
    private TextView tvHabitStrengthTag;
    private ConstraintLayout constraintLayoutStrengthStatistics;
    private PieChart pieChartHabitStrength;
    private LineChart lineChartStrengthEvolution;

    // HISTORY
    private MaterialCardView cardViewHistory;
    private TextView tvStatisticsHabitHistoryTag;
    private ConstraintLayout constraintLayoutHistory;
    private MaterialCalendarView materialCalendarViewHistory;
    private LocalDate habitHistoryDate;

    // ACHIEVEMENTS
    private MaterialCardView cardViewAchievements;
    private TextView tvStatisticsHabitAchievementsTag;
    private ConstraintLayout constraintLayoutAchievements;
    private FlexboxLayout flexboxLayoutGeneralAchievements;
    private FlexboxLayout flexboxLayoutStreaksAchievements;
    private FlexboxLayout flexboxLayoutStrengthAchievements;
    private ArrayList<StreakAchievement> streakAchievements;
    private ArrayList<StrengthAchievement> strengthAchievements;
    private ArrayList<GeneralAchievement> generalAchievements;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_statistics);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        receivedHabit = getIntent().getParcelableExtra("HABIT_FOR_STATISTICS");
        if (receivedHabit == null) {
            Toast.makeText(HabitStatisticsActivity.this,
                    "Oh uh! An error occured. Please try again later",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        tvStatisticsHabitName = findViewById(R.id.tvStatisticsHabitName);
        tvStatisticsHabitFrequency = findViewById(R.id.tvStatisticsHabitFrequency);
        tvStatisticsHabitDescription = findViewById(R.id.tvStatisticsHabitDescription);
        tvStatisticsHabitStartDate = findViewById(R.id.tvStatisticsHabitStartDate);
        tvStatisticsHabitDaysPassed = findViewById(R.id.tvStatisticsHabitDaysPassed);
        tvStatisticsHabitCategory = findViewById(R.id.tvStatisticsHabitCategory);
        tvStatisticsHabitDifficulty = findViewById(R.id.tvStatisticsHabitDifficulty);

        //SCORE
        pieChartHabitScore = findViewById(R.id.pieChartHabitScore);

        // NUMERICAL STATS
        constraintLayoutNumericalStats = findViewById(R.id.constraintLayoutNumericalStats);
        tvTotalNumerical = findViewById(R.id.tvTotalNumerical);
        tvAverageNumerical = findViewById(R.id.tvAverageNumerical);
        tvHighestNumerical = findViewById(R.id.tvHighestNumerical);
        tvLowestNumerical = findViewById(R.id.tvLowestNumerical);

        // STREAKS
        cardViewStreaks = findViewById(R.id.cardViewStreaks);
        tvStatisticsHabitStreaksTag = findViewById(R.id.tvStatisticsHabitStreaksTag);
        constraintLayoutStreaksStatistics = findViewById(R.id.constraintLayoutStreaksStatistics);
        horizontalBarChartStreaks = findViewById(R.id.horizontalBarChartStreaks);
        lineChartStreaksEvolution = findViewById(R.id.lineChartStreaksEvolution);

        // COMPLETIONS
        cardViewCompletions = findViewById(R.id.cardViewCompletions);
        tvStatisticsHabitCompletionsTag = findViewById(R.id.tvStatisticsHabitCompletionsTag);
        constraintLayoutCompletionsStatistics = findViewById(R.id.constraintLayoutCompletionsStatistics);
        pieChartCompletions = findViewById(R.id.pieChartCompletions);
        barChartMonthlyCompletions = findViewById(R.id.barChartMonthlyCompletions);
        tvStatisticsCurrMonthPercents = findViewById(R.id.tvStatisticsCurrMonthPercents);
        tvStatisticsCurrMonthCompletionPercent = findViewById(R.id.tvStatisticsCurrMonthCompletionPercent);
        tvStatisticsCurrMonthMissPercent = findViewById(R.id.tvStatisticsCurrMonthMissPercent);
        tvStatisticsAvgMonthsPercents = findViewById(R.id.tvStatisticsAvgMonthsPercents);
        tvStatisticsAvgMonthsCompletionPercent = findViewById(R.id.tvStatisticsAvgMonthsCompletionPercent);
        tvStatisticsAvgMonthsMissPercent = findViewById(R.id.tvStatisticsAvgMonthsMissPercent);

        // STRENGTH
        cardViewStrength = findViewById(R.id.cardViewStrength);
        tvHabitStrengthTag = findViewById(R.id.tvHabitStrengthTag);
        constraintLayoutStrengthStatistics = findViewById(R.id.constraintLayoutStrengthStatistics);
        pieChartHabitStrength = findViewById(R.id.pieChartHabitStrength);
        lineChartStrengthEvolution = findViewById(R.id.lineChartStrengthEvolution);

        // HISTORY
        cardViewHistory = findViewById(R.id.cardViewHistory);
        tvStatisticsHabitHistoryTag = findViewById(R.id.tvStatisticsHabitHistoryTag);
        constraintLayoutHistory = findViewById(R.id.constraintLayoutHistory);
        materialCalendarViewHistory = findViewById(R.id.materialCalendarViewHistory);
        habitHistoryDate = LocalDate.now();

        // ACHIEVEMENTS
        cardViewAchievements = findViewById(R.id.cardViewAchievements);
        tvStatisticsHabitAchievementsTag = findViewById(R.id.tvStatisticsHabitAchievementsTag);
        constraintLayoutAchievements = findViewById(R.id.constraintLayoutAchievements);
        flexboxLayoutGeneralAchievements = findViewById(R.id.flexboxLayoutGeneralAchievements);
        flexboxLayoutStreaksAchievements = findViewById(R.id.flexboxLayoutStreaksAchievements);
        flexboxLayoutStrengthAchievements = findViewById(R.id.flexboxLayoutStrengthAchievements);
        streakAchievements = AchievementsReader.getStreakAchievements(HabitStatisticsActivity.this);
        strengthAchievements = AchievementsReader.getStrengthAchievements(HabitStatisticsActivity.this);
        generalAchievements = AchievementsReader.getGeneralAchievements(HabitStatisticsActivity.this);

        configureComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        Integer currentStreak = HabitCurrentStreak.getCurrentStreak(receivedHabit);
        Double averageStreak = HabitAverageStreak.getAverageStreak(receivedHabit);
        Integer bestStreak = HabitBestStreak.getBestStreak(receivedHabit);
        double streaksVsMissesDifference = HabitPrevStreaksVsMissesDiff.get(receivedHabit);
        double habitScore = HabitScore.get(receivedHabit, streaksVsMissesDifference, currentStreak, averageStreak, bestStreak);
        HashMap<String, Double> realizations = receivedHabit.getRealizations();
        String habitNumericalUnit = receivedHabit.getNumericalUnit();
        double habitStrength = Calculations.getHabitStrength(streaksVsMissesDifference,
                currentStreak,
                averageStreak,
                bestStreak,
                receivedHabit.getDaysToFormHabit());

        ScoreChartsManager.configureScorePieChart(pieChartHabitScore,
                habitScore,
                getApplicationContext());

        if (receivedHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
            tvStatisticsHabitName.setText(receivedHabit.getName());
            constraintLayoutNumericalStats.setVisibility(View.GONE);
        } else {
            HashMap<String, Double> currentRealizations = getFilteredRealizationsByDate(realizations, LocalDate.now().toEpochDay());
            tvStatisticsHabitName.setText(getNumericalHabitFullName(receivedHabit));
            constraintLayoutNumericalStats.setVisibility(View.VISIBLE);
            tvTotalNumerical.setText(getSpannableStringForNumericalStats(currentRealizations, habitNumericalUnit, 0));
            tvAverageNumerical.setText(getSpannableStringForNumericalStats(currentRealizations, habitNumericalUnit, 1));
            tvHighestNumerical.setText(getSpannableStringForNumericalStats(currentRealizations, habitNumericalUnit, 2));
            tvLowestNumerical.setText(getSpannableStringForNumericalStats(currentRealizations, habitNumericalUnit, 3));
        }

        HabitFrequency habitFrequency = receivedHabit.getFrequency();
        if (habitFrequency == HabitFrequency.DAILY) {
            tvStatisticsHabitFrequency.setText("Frequency: Daily");
        } else if (habitFrequency == HabitFrequency.SPECIFICDAYSOFWEEK) {
            tvStatisticsHabitFrequency.setText(getDaysOfWeekString());
        } else if (habitFrequency == HabitFrequency.SPECIFICDAYSOFMONTH) {
            tvStatisticsHabitFrequency.setText(getDaysOfMonthString());
        } else { // Repeat
            tvStatisticsHabitFrequency.setText("Frequency: Once every "
                    .concat(receivedHabit.getNumberOfDaysForRepeat().toString())
                    .concat(" days"));
        }

        if (receivedHabit.getDescription() != null) {
            tvStatisticsHabitDescription.setVisibility(View.VISIBLE);
            tvStatisticsHabitDescription.setText("Description: ".concat(receivedHabit.getDescription()));
        }

        Long habitStartDate = receivedHabit.getStartDate();
        String habitStartDateString = LocalDateConverter.getStandardFormatStringFromDate(
                LocalDate.ofEpochDay(habitStartDate));

        if (habitStartDate <= LocalDate.now().toEpochDay()) {
            tvStatisticsHabitStartDate.setText("Started on: "
                    .concat(habitStartDateString));
            tvStatisticsHabitDaysPassed.setText("Days passed: ".concat(String.valueOf(
                    LocalDate.now().toEpochDay() - receivedHabit.getStartDate())));
        } else {
            tvStatisticsHabitStartDate.setText("Starts on: "
                    .concat(habitStartDateString));
            tvStatisticsHabitDaysPassed.setText("Days left: ".concat(String.valueOf(
                    receivedHabit.getStartDate() - LocalDate.now().toEpochDay())));
        }

        if (receivedHabit.getCategory() != null) {
            tvStatisticsHabitCategory.setVisibility(View.VISIBLE);
            tvStatisticsHabitCategory.setText("Category: ".concat(receivedHabit.getCategory()));
        }


        int[] daysToFormHabitArray;
        String[] habitDifficulties;
        daysToFormHabitArray = getResources().getIntArray(R.array.habitDaysToFormHabit);
        habitDifficulties = getResources().getStringArray(R.array.habitDifficulties);
        int indexOfNrDaysToFormHabit = Arrays.binarySearch(daysToFormHabitArray, receivedHabit.getDaysToFormHabit());
        tvStatisticsHabitDifficulty.setText("Difficulty: ".concat(habitDifficulties[indexOfNrDaysToFormHabit]));

        tvStatisticsHabitStreaksTag.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(cardViewStreaks);
            if (constraintLayoutStreaksStatistics.getVisibility() == View.GONE) {
                constraintLayoutStreaksStatistics.setVisibility(View.VISIBLE);

                StreaksChartsManager.configureHorizontalBarChart(receivedHabit,
                        horizontalBarChartStreaks,
                        getApplicationContext(),
                        currentStreak,
                        averageStreak,
                        bestStreak);

                StreaksChartsManager.configureStreaksEvolutionLineChart(receivedHabit,
                        lineChartStreaksEvolution,
                        getApplicationContext());
            } else {
                constraintLayoutStreaksStatistics.setVisibility(View.GONE);
            }
        });

        tvStatisticsHabitCompletionsTag.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(cardViewCompletions);
            if (constraintLayoutCompletionsStatistics.getVisibility() == View.GONE) {
                constraintLayoutCompletionsStatistics.setVisibility(View.VISIBLE);

                CompletionsChartsManager.configurePieChart(receivedHabit,
                        pieChartCompletions,
                        getApplicationContext());

                CompletionsChartsManager.configureBarChart(receivedHabit,
                        barChartMonthlyCompletions,
                        tvStatisticsCurrMonthCompletionPercent,
                        tvStatisticsCurrMonthMissPercent,
                        tvStatisticsAvgMonthsCompletionPercent,
                        tvStatisticsAvgMonthsMissPercent,
                        getApplicationContext());

            } else {
                constraintLayoutCompletionsStatistics.setVisibility(View.GONE);
            }
        });


        tvHabitStrengthTag.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(cardViewStrength);
            if (constraintLayoutStrengthStatistics.getVisibility() == View.GONE) {
                constraintLayoutStrengthStatistics.setVisibility(View.VISIBLE);

                StrengthChartsManager.configureScorePieChart(pieChartHabitStrength,
                        getApplicationContext(),
                        habitStrength);

                StrengthChartsManager.configureStrengthEvolutionLineChart(receivedHabit,
                        lineChartStrengthEvolution,
                        getApplicationContext());

            } else {
                constraintLayoutStrengthStatistics.setVisibility(View.GONE);
            }
        });


        tvStatisticsHabitHistoryTag.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(cardViewHistory);
            if (constraintLayoutHistory.getVisibility() == View.GONE) {
                constraintLayoutHistory.setVisibility(View.VISIBLE);

                HistoryChartsManager.configureCalendar(materialCalendarViewHistory,
                        receivedHabit,
                        HabitStatisticsActivity.this);

            } else {
                constraintLayoutHistory.setVisibility(View.GONE);
            }
        });


        tvStatisticsHabitAchievementsTag.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(cardViewAchievements);
            if (constraintLayoutAchievements.getVisibility() == View.GONE) {
                constraintLayoutAchievements.setVisibility(View.VISIBLE);

                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 20, 20, 0);
                Drawable trophyImg = ContextCompat.getDrawable(HabitStatisticsActivity.this, R.drawable.ic_trophy_svg);
                for (int i = 0; i < generalAchievements.size(); i++) {
                    TextView tv = new TextView(HabitStatisticsActivity.this);
                    tv.setLayoutParams(params);
                    tv.setText(generalAchievements.get(i).getName());
                    tv.setTextColor(Color.WHITE);
                    tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tv.setTypeface(ResourcesCompat.getFont(HabitStatisticsActivity.this, R.font.russo_one));
                    Drawable trophyImgCopy = trophyImg.getConstantState().newDrawable().mutate();

                    if (generalAchievements.get(i).getName().equals("On my best")) {
                        if (!bestStreak.equals(currentStreak) || bestStreak == 0) {
                            trophyImgCopy.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                        }
                    } else if (generalAchievements.get(i).getName().equals("Perfect")) {
                        if (!bestStreak.equals(currentStreak) ||
                                (double) currentStreak != averageStreak ||
                                (double) bestStreak != averageStreak ||
                                bestStreak == 0) {
                            trophyImgCopy.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                        }
                    } else if (generalAchievements.get(i).getName().equals("Strong will") || bestStreak == 0) {
                        if (hasMultipleDaysBetweenCompletions()) {
                            trophyImgCopy.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                        }
                    }

                    tv.setCompoundDrawablesWithIntrinsicBounds(null, trophyImgCopy, null, null);
                    int finalI = i;
                    tv.setOnClickListener(view1 -> {
                        new AlertDialog.Builder(HabitStatisticsActivity.this,
                                AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                                .setTitle(generalAchievements.get(finalI).getName())
                                .setMessage(generalAchievements.get(finalI).getDescription())
                                .create()
                                .show();
                    });
                    flexboxLayoutGeneralAchievements.addView(tv);
                }
                for (int i = 0; i < streakAchievements.size(); i++) {
                    TextView tv = getStreakAchievementTextView(currentStreak, params, trophyImg, i);
                    flexboxLayoutStreaksAchievements.addView(tv);
                }

                for (int i = 0; i < strengthAchievements.size(); i++) {
                    TextView tv = getStrengthAchievementTextView(habitStrength, params, trophyImg, i);
                    flexboxLayoutStrengthAchievements.addView(tv);
                }
            } else {
                constraintLayoutAchievements.setVisibility(View.GONE);
                flexboxLayoutGeneralAchievements.removeAllViews();
                flexboxLayoutStreaksAchievements.removeAllViews();
                flexboxLayoutStrengthAchievements.removeAllViews();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean hasMultipleDaysBetweenCompletions() {
        HashMap<String, Double> realizations = receivedHabit.getRealizations();

        if (realizations == null || realizations.size() == 0) {
            return true;
        }

        Long minDateFromRealizations = Collections.min(
                realizations.keySet()
                        .stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));

        if (minDateFromRealizations >= LocalDate.now().toEpochDay()) {
            return true;
        }

        Long dateInEpochDays = receivedHabit.getStartDate();
        if (receivedHabit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
            LocalDate habitStartDate = LocalDate.ofEpochDay(receivedHabit.getStartDate());
            if (!receivedHabit.getDaysOfWeek().contains(habitStartDate.getDayOfWeek().getValue())) {
                dateInEpochDays = DaysOfWeekDateManager.getNextDate(habitStartDate.toEpochDay(), receivedHabit.getDaysOfWeek());
            }
        } else if (receivedHabit.getFrequency() == HabitFrequency.SPECIFICDAYSOFMONTH) {
            LocalDate habitStartDate = LocalDate.ofEpochDay(receivedHabit.getStartDate());
            if (!receivedHabit.getDaysOfMonth().contains(habitStartDate.getDayOfMonth())) {
                dateInEpochDays = DaysOfMonthDateManager.getNextDate(habitStartDate, receivedHabit.getDaysOfMonth());
            }
        }

        int nrOfDaysBetweenCompletions = 0;
        while (dateInEpochDays <= LocalDate.now().toEpochDay()) {
            if (!HabitRealizationValidator.isValid(dateInEpochDays, receivedHabit)) {
                if (nrOfDaysBetweenCompletions == 1) {
                    return true;
                }
                ++nrOfDaysBetweenCompletions;
            } else {
                nrOfDaysBetweenCompletions = 0;
            }

            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, receivedHabit);
        }

        return false;
    }


    private TextView getStreakAchievementTextView(Integer currentStreak, FlexboxLayout.LayoutParams params, Drawable trophyImg, int i) {
        TextView tv = new TextView(HabitStatisticsActivity.this);
        tv.setLayoutParams(params);
        tv.setText(streakAchievements.get(i).getName());
        tv.setTextColor(Color.WHITE);
        tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
        tv.setTypeface(ResourcesCompat.getFont(HabitStatisticsActivity.this, R.font.russo_one));

        Drawable trophyImgCopy = trophyImg.getConstantState().newDrawable().mutate();
        int min = streakAchievements.get(i).getMinimumStreak();
        if (min > currentStreak) {
            trophyImgCopy.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(null, trophyImgCopy, null, null);
        return tv;
    }

    private TextView getStrengthAchievementTextView(Double currentStrength, FlexboxLayout.LayoutParams params, Drawable trophyImg, int i) {
        TextView tv = new TextView(HabitStatisticsActivity.this);
        tv.setLayoutParams(params);
        tv.setText(strengthAchievements.get(i).getName());
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTypeface(ResourcesCompat.getFont(HabitStatisticsActivity.this, R.font.russo_one));

        Drawable trophyImgCopy = trophyImg.getConstantState().newDrawable().mutate();
        double min = strengthAchievements.get(i).getMinimumStrength();
        if (min > currentStrength) {
            trophyImgCopy.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(null, trophyImgCopy, null, null);
        return tv;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public HashMap<String, Double> getFilteredRealizationsByDate(HashMap<String, Double> realizations, Long maxDate) {
        HashMap<String, Double> filteredRealizations = new HashMap<>();
        for (Map.Entry<String, Double> realization : realizations.entrySet()) {
            if (Long.parseLong(realization.getKey()) <= maxDate) {
                filteredRealizations.put(realization.getKey(), realization.getValue());
            }
        }
        return filteredRealizations;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private SpannableString getSpannableStringForNumericalStats(HashMap<String, Double> realizations, String unit, int type) {
        String title;
        String value;

        switch (type) {
            case 0:
                title = "Total ".concat(unit);
                double total = realizations.values().stream()
                        .mapToDouble(d -> d)
                        .sum();
                if (total % 1 == 0) {
                    value = "\n".concat(String.format("%.0f", total));
                } else {
                    value = "\n".concat(String.format("%.1f", total));
                }
                break;
            case 1:
                title = "Average";
                if (realizations == null || realizations.size() == 0) {
                    value = "\n0";
                } else {
                    double average = realizations.values().stream()
                            .mapToDouble(d -> d)
                            .average().getAsDouble();
                    if (average % 1 == 0) {
                        value = "\n".concat(String.format("%.0f", average));
                    } else {
                        value = "\n".concat(String.format("%.1f", average));
                    }
                }
                break;
            case 2:
                title = "Highest";
                if (realizations == null || realizations.size() == 0) {
                    value = "\n0";
                } else {
                    double max = realizations.values().stream()
                            .mapToDouble(d -> d)
                            .max().getAsDouble();
                    if (max % 1 == 0) {
                        value = "\n".concat(String.format("%.0f", max));
                    } else {
                        value = "\n".concat(String.format("%.1f", max));
                    }
                }
                break;
            case 3:
                title = "Lowest";
                if (realizations == null || realizations.size() == 0) {
                    value = "\n0";
                } else {
                    double min = realizations.values().stream()
                            .mapToDouble(d -> d)
                            .min().getAsDouble();
                    if (min % 1 == 0) {
                        value = "\n".concat(String.format("%.0f", min));
                    } else {
                        value = "\n".concat(String.format("%.1f", min));
                    }
                }
                break;
            default:
                title = "Woops";
                value = "Error";
                break;
        }
        String fullText = title.concat(value);
        SpannableString inputsSum = new SpannableString(title.concat(value));
        inputsSum.setSpan(new RelativeSizeSpan(1.5f),
                title.length(),
                fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return inputsSum;
    }


    @NonNull
    private StringBuilder getDaysOfMonthString() {
        StringBuilder sbFrequency = new StringBuilder().append("Frequency: Certain days of month (");
        ArrayList<Integer> daysOfMonth = receivedHabit.getDaysOfMonth();
        Collections.sort(daysOfMonth);
        for (int i = 0; i < daysOfMonth.size(); i++) {
            sbFrequency.append(daysOfMonth.get(i));
            if (i != daysOfMonth.size() - 1) {
                sbFrequency.append(", ");
            } else {
                sbFrequency.append(")");
            }
        }
        return sbFrequency;
    }


    @NonNull
    private StringBuilder getDaysOfWeekString() {
        StringBuilder sbFrequency = new StringBuilder().append("Frequency: Certain days of week (");
        ArrayList<Integer> daysOfWeek = receivedHabit.getDaysOfWeek();
        for (int i = 0; i < daysOfWeek.size(); i++) {
            sbFrequency.append(getDayOfWeekAsWordFromNumber(daysOfWeek.get(i)));
            if (i != daysOfWeek.size() - 1) {
                sbFrequency.append(", ");
            } else {
                sbFrequency.append(")");
            }
        }

        return sbFrequency;
    }

    private String getDayOfWeekAsWordFromNumber(Integer dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "N/A";
        }
    }


    private StringBuilder getNumericalHabitFullName(Habit currentHabit) {
        StringBuilder sbHabitName = new StringBuilder();

        sbHabitName.append(currentHabit.getName())
                .append(" ");
        if (currentHabit.getNumericalComparisonType() == HabitNumericalComparisonType.EXACTLY) {
            sbHabitName.append("exactly");
        } else if (currentHabit.getNumericalComparisonType() == HabitNumericalComparisonType.ATLEAST) {
            sbHabitName.append("at least");
        } else {
            sbHabitName.append("less than");
        }

        sbHabitName.append(" ");
        if (isDoubleAnInteger(currentHabit.getNumericalGoal())) {
            sbHabitName.append(String.format("%.0f", currentHabit.getNumericalGoal()));
        } else {
            sbHabitName.append(currentHabit.getNumericalGoal());
        }
        sbHabitName.append(" ")
                .append(currentHabit.getNumericalUnit());

        return sbHabitName;
    }

    private boolean isDoubleAnInteger(Double numberAsDouble) {
        return numberAsDouble % 1 == 0;
    }
}