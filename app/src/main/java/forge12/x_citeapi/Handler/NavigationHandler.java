package forge12.x_citeapi.Handler;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import forge12.x_citeapi.Views.ProductInformation;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.Views.Admin;
import forge12.x_citeapi.Views.ContractList;
import forge12.x_citeapi.Views.Dashboard;

public class NavigationHandler {
    public static void update(Context context){
        AppCompatActivity app = (AppCompatActivity) context;

        if(Helper.is_database_available(context)){
//            app.findViewById(R.id.navigation_contract_add).setVisibility(View.VISIBLE);
            app.findViewById(R.id.navigation_contract_list).setVisibility(View.VISIBLE);
        }else{
//            app.findViewById(R.id.navigation_contract_add).setVisibility(View.GONE);
            app.findViewById(R.id.navigation_contract_list).setVisibility(View.GONE);
        }
    }

    public static BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigation(final AppCompatActivity context){
        BottomNavigationView.OnNavigationItemSelectedListener view = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        intent = new Intent(context, Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;
                    /*case R.id.navigation_contract_add:
                        intent = new Intent(context, ContractPersonal.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;*/
                    case R.id.navigation_contract_list:
                        intent = new Intent(context, ContractList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;
                    /*case R.id.navigation_sync:
                        intent = new Intent(context, Sync.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;*/
                    case R.id.navigation_admin:
                        intent = new Intent(context, Admin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;
                    case R.id.navigation_product_information:
                        intent = new Intent(context, ProductInformation.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                        return true;
                }
                return false;
            }
        };

        return view;
    }
}
