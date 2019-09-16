package forge12.x_citeapi.Handler;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.R;

public class ContractNavigationHandler {
    private static int[] _views = {
            R.id.snippet_personal_container,
            R.id.snippet_bank_container,
            R.id.snippet_gas_container,
            R.id.snippet_strom_container,
            R.id.snippet_product_info_container,
            R.id.snippet_signature_container,
            R.id.snippet_invoice_container,
            R.id.snippet_business_container,
            R.id.snippet_leaser_container,
            R.id.snippet_lessor_container,
            R.id.snippet_kaution_container,
    };

    public static void hideAll(final AppCompatActivity context) {
        for (int i : _views) {
            context.findViewById(i).setVisibility(View.GONE);
        }
        ContractNavigationHandler.hide(context);
    }

    public static void showView(final AppCompatActivity context, int view) {
        for (int i : _views) {
            if (i == view) {
                context.findViewById(i).setVisibility(View.VISIBLE);
            }
        }
    }

    public static void loadProductTypeNavigation(NavigationView view, String vertragsart, @Nullable ContractModel CM) {
        if (vertragsart.equals("Strom")) {
            /*view.getMenu().findItem(R.id.navigation_contract_product_gas).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_leaser).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_lessor).setVisible(false);*/
            view.getMenu().findItem(R.id.navigation_contract_product_kaution).setVisible(false);
        } else if (vertragsart.equals("Gas")) {
            /*view.getMenu().findItem(R.id.navigation_contract_product_strom).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_leaser).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_lessor).setVisible(false);*/
            view.getMenu().findItem(R.id.navigation_contract_product_kaution).setVisible(false);
        } else {
            /*view.getMenu().findItem(R.id.navigation_contract_product_information).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_gas).setVisible(false);
            view.getMenu().findItem(R.id.navigation_contract_product_strom).setVisible(false);*/
        }

        if (CM != null) {
            if (CM.get("status").equals("1")) {
                view.getMenu().findItem(R.id.navigation_contract_close).setVisible(true);
                view.getMenu().findItem(R.id.navigation_contract_save).setVisible(false);
                view.getMenu().findItem(R.id.navigation_contract_save_draft).setVisible(false);
            } else {
                view.getMenu().findItem(R.id.navigation_contract_close).setVisible(false);
                view.getMenu().findItem(R.id.navigation_contract_save).setVisible(true);
                view.getMenu().findItem(R.id.navigation_contract_save_draft).setVisible(true);
            }
        }
    }

    public static NavigationView.OnNavigationItemSelectedListener getNavigation(final AppCompatActivity context) {
        NavigationView.OnNavigationItemSelectedListener view = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() != R.id.navigation_contract_close && menuItem.getItemId() != R.id.navigation_contract_save && menuItem.getItemId() != R.id.navigation_contract_save_draft) {
                    hideAll(context);
                }
                switch (menuItem.getItemId()) {
                    case R.id.navigation_contract_personal:
                        showView(context, R.id.snippet_personal_container);
                        showView(context, R.id.snippet_leaser_container);
                        showView(context, R.id.snippet_business_container);
                        return true;
                    case R.id.navigation_contract_bank:
                        showView(context, R.id.snippet_bank_container);
                        showView(context, R.id.snippet_invoice_container);
                        showView(context, R.id.snippet_signature_container);
                        return true;
                    case R.id.navigation_contract_product_kaution:
                        showView(context, R.id.snippet_lessor_container);
                        showView(context, R.id.snippet_kaution_container);
                        break;
                    case R.id.navigation_contract_close:
                        context.finish();
                        break;
                    case R.id.navigation_contract_save:
                        EventListener.do_action("contract_save", 1);
                        break;
                    case R.id.navigation_contract_save_draft:
                        EventListener.do_action("contract_save", 0);
                        break;
                    /*case R.id.navigation_contract_product_strom:
                        toggleView(context, R.id.snippet_strom_container);
                        return true;
                    case R.id.navigation_contract_product_gas:
                        toggleView(context, R.id.snippet_gas_container);
                        return true;
                    case R.id.navigation_contract_product_information:
                        toggleView(context, R.id.snippet_product_info_container);
                        return true;
                    case R.id.navigation_contract_signature:
                        toggleView(context, R.id.snippet_signature_container);
                        return true;
                    case R.id.navigation_contract_invoice:
                        toggleView(context, R.id.snippet_invoice_container);
                        break;
                    case R.id.navigation_contract_business_information:
                        toggleView(context, R.id.snippet_business_container);
                        break;
                    case R.id.navigation_contract_product_leaser:
                        toggleView(context, R.id.snippet_leaser_container);
                        break;
                    case R.id.navigation_contract_product_lessor:
                        toggleView(context, R.id.snippet_lessor_container);
                        break;*/
                }
                return false;
            }
        };

        return view;
    }

    public static void toggle(final AppCompatActivity context) {
        NavigationView nv = context.findViewById(R.id.navigation_contract);
        if (nv == null) {
            return;
        }

        if (nv.getVisibility() == View.VISIBLE) {
            nv.setVisibility(View.GONE);
        } else {
            nv.setVisibility(View.VISIBLE);
        }
    }

    public static void show(final AppCompatActivity context) {
        NavigationView nv = context.findViewById(R.id.navigation_contract);
        if (nv == null) {
            return;
        }

        nv.setVisibility(View.VISIBLE);
    }

    public static void hide(final AppCompatActivity context) {
        NavigationView nv = context.findViewById(R.id.navigation_contract);
        if (nv == null) {
            return;
        }

        nv.setVisibility(View.GONE);
    }
}
