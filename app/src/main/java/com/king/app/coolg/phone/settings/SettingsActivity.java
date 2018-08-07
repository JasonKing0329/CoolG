package com.king.app.coolg.phone.settings;


import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.model.FingerPrintController;
import com.king.app.coolg.model.http.BaseUrl;
import com.king.app.coolg.model.repository.PropertyRepository;
import com.king.app.coolg.model.setting.PreferenceKey;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.utils.AppUtil;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
@Route("Setting")
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }  else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }

            /**
             * 站点配置变化要及时通知baseUrl变更，重新创建retrofit相关
             */
            if (preference.getKey().equals(PreferenceKey.PREF_HTTP_SERVER)) {
                BaseUrl.getInstance().setBaseUrl(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.back);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_safety);
        if (!new FingerPrintController(this).isSupported()) {
            findPreference(PreferenceKey.PREF_SAFETY_FP).setEnabled(false);
        }

        // Add 'general' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_general);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'http' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.setting_http_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_http);

        // Add 'gdb' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.setting_gdb_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_gdb);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.

        //general
        bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_HOME_VIEW));

        //http
        bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_HTTP_SERVER));
        EditTextPreference edt = (EditTextPreference) findPreference(PreferenceKey.PREF_MAX_DOWNLOAD);
        bindPreferenceSummaryToValue(edt);
        limitEditRange(edt.getEditText(), 1, PreferenceValue.HTTP_MAX_DOWNLOAD_UPLIMIT);


        PrefClickListener listener = new PrefClickListener(this, getFragmentManager());
        Preference updatePref = findPreference(PreferenceKey.PREF_CHECK_UPDATE);
        updatePref.setSummary("v" + AppUtil.getAppVersionName());
        updatePref.setOnPreferenceClickListener(listener);
        updatePref = findPreference(PreferenceKey.PREF_CHECK_UPDATE_GDB);
        updatePref.setSummary("v" + new PropertyRepository().getVersion());
        updatePref.setOnPreferenceClickListener(listener);
        updatePref = findPreference(PreferenceKey.PREF_CHECK_BACKUP);
        updatePref.setOnPreferenceClickListener(listener);

        //gdb
        bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_GDB_LATEST_NUM));

    }

    private static class PrefClickListener implements Preference.OnPreferenceClickListener {

        private Context mContext;
        private FragmentManager fragmentManager;

        public PrefClickListener(Context context, FragmentManager fragmentManager) {
            mContext = context;
            this.fragmentManager = fragmentManager;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(PreferenceKey.PREF_CHECK_UPDATE)) {
//                UpdateManager manager = new UpdateManager(mContext);
//                manager.setFragmentManager(fragmentManager);
//                manager.showMessageWarning();
//                manager.startCheck();
            }
            else if (preference.getKey().equals(PreferenceKey.PREF_CHECK_UPDATE_GDB)) {
//                ActivityManager.startManageActivity((Activity) mContext);
//                ((Activity) mContext).finish();
            }
            else if (preference.getKey().equals(PreferenceKey.PREF_CHECK_BACKUP)) {
//                new UploadPresenter(new IUploadView() {
//                    @Override
//                    public void onUploadSuccess() {
//                        ((SettingsActivity) mContext).showToastLong(mContext.getString(R.string.upload_success), BaseView.TOAST_SUCCESS);
//                    }
//
//                    @Override
//                    public void onUploadFail() {
//                        ((SettingsActivity) mContext).showToastLong(mContext.getString(R.string.upload_fail), BaseView.TOAST_ERROR);
//                    }
//                }).uploadAppData();
            }
            return false;
        }
    }

    private void showToastLong(String string, int toastSuccess) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    public static void limitEditRange(final EditText editText, final int min, final int max) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    int num = Integer.parseInt(s.toString());
                    if (num > max) {
                        editText.setText("" + max);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || SafetyPreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || HttpPreferenceFragment.class.getName().equals(fragmentName)
                || GdbPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SafetyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_safety);
            setHasOptionsMenu(true);

            if (!new FingerPrintController(getActivity()).isSupported()) {
                findPreference(PreferenceKey.PREF_SAFETY_FP).setEnabled(false);
            }

        }

    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_HOME_VIEW));
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HttpPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_http);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_HTTP_SERVER));
            bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_MAX_DOWNLOAD));

            PrefClickListener listener = new PrefClickListener(getActivity(), getFragmentManager());
            Preference preference = findPreference(PreferenceKey.PREF_CHECK_UPDATE);
            preference.setSummary("v" + AppUtil.getAppVersionName());
            preference.setOnPreferenceClickListener(listener);
            preference = findPreference(PreferenceKey.PREF_CHECK_UPDATE_GDB);
            preference.setSummary("v" +  new PropertyRepository().getVersion());
            preference.setOnPreferenceClickListener(listener);
            preference = findPreference(PreferenceKey.PREF_CHECK_BACKUP);
            preference.setOnPreferenceClickListener(listener);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GdbPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_gdb);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(PreferenceKey.PREF_GDB_LATEST_NUM));
        }

    }

}
