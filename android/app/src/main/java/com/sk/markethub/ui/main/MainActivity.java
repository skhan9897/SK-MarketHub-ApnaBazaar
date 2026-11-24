package com.sk.markethub.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.ui.cart.CartFragment;
import com.sk.markethub.ui.catalog.CatalogFragment;
import com.sk.markethub.ui.orders.OrdersFragment;
import com.sk.markethub.ui.portals.PortalsFragment;

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    private int getLocalVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        replaceFragment(new CatalogFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_catalog) {
                replaceFragment(new CatalogFragment());
                return true;
            } else if (id == R.id.nav_cart) {
                replaceFragment(new CartFragment());
                return true;
            } else if (id == R.id.nav_orders) {
                replaceFragment(new OrdersFragment());
                return true;
            } else if (id == R.id.nav_portals) {
                replaceFragment(new PortalsFragment());
                return true;
            }
            return false;
        });

        checkForAppUpdates();
    }

    private void checkForAppUpdates() {
        ApiClient.getService().getLatestAppRelease().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    Number serverVersionCodeNum = (Number) data.get("latestVersionCode");
                    int serverVersionCode = serverVersionCodeNum != null ? serverVersionCodeNum.intValue() : 1;
                    String versionName = (String) data.get("latestVersionName");
                    String releaseNotes = (String) data.get("releaseNotes");
                    String downloadUrl = (String) data.get("downloadUrl");

                    int localVersionCode = getLocalVersionCode();

                    if (serverVersionCode > localVersionCode) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Update Available: v" + versionName)
                                .setMessage(releaseNotes != null ? releaseNotes : "A new version of SK-MarketHub is available!")
                                .setPositiveButton("Update Now", (dialog, which) -> {
                                    String targetUrl = (downloadUrl != null && !downloadUrl.isBlank()) ? downloadUrl : "https://sk-markethub-apnabazaar.onrender.com/download-app";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
                                    startActivity(intent);
                                })
                                .setNegativeButton("Later", null)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }
}
