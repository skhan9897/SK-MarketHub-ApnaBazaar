package com.sk.markethub.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.sk.markethub.R;
import com.sk.markethub.ui.cart.CartFragment;
import com.sk.markethub.ui.catalog.CatalogFragment;
import com.sk.markethub.ui.orders.OrdersFragment;
import com.sk.markethub.ui.portals.PortalsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

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
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .commit();
    }
}
