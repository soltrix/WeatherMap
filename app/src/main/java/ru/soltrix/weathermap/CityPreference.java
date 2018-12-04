package ru.soltrix.weathermap;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {

    //Вспомогательный класс для хранения выбранного города
    private SharedPreferences prefslat;
    private SharedPreferences prefslon;

    CityPreference(Activity activity) {
        prefslat = activity.getPreferences(Activity.MODE_PRIVATE);
        prefslon = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // Возвращаем город по умолчанию, если SharedPreferences пустые
    protected String getLatitude() {
        return prefslat.getString("lat", "57.63");
    }

    protected String getLongitude() {
        return prefslon.getString("lon", "39.89");
    }

    protected void setLatitude(String latitude) {
        prefslat.edit().putString("lat", latitude).apply();
    }

    protected void setLongitude(String longitude) {
        prefslon.edit().putString("lon", longitude).apply();
    }
}