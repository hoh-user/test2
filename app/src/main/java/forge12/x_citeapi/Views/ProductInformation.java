package forge12.x_citeapi.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.TarifModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.interfaces.CallableAction;

public class ProductInformation extends AppCompatActivity {

    private EditText mZip = null;

    private String mType = "Strom";

    /**
     * update the product information
     */
    public static CallableAction update_product_information = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            if (!(args[0] instanceof SpannableStringBuilder)) {
                return;
            }

            String zip = args[0].toString();

            if (!(args[1] instanceof String)) {
                return;
            }

            String type = args[1].toString();

            if (!(args[2] instanceof AppCompatActivity)) {
                return;
            }

            AppCompatActivity context = (AppCompatActivity) args[2];

            TarifModel TM = TarifModel.load(context, zip, type, null);

            if (null == TM) {
                return;
            }

            TextView kwh = context.findViewById(R.id.kwhpreis);
            kwh.setText(Helper.number_format(TM.get("kwhpreis")) + " ct/kWh");

            TextView group = context.findViewById(R.id.name_gruppe);
            group.setText(TM.get("name_gruppe"));

            TextView Grundgebuehr = context.findViewById(R.id.grundgebuehr);
            Grundgebuehr.setText(Helper.number_format(TM.get("grundgebuehr")) + " €");

            TextView Grundgebuehr2 = context.findViewById(R.id.grundgebuehr2);
            Grundgebuehr2.setText(Helper.number_format(TM.get("grundgebuehr2")) + " €");

            TextView GrundgebuehrSingle = context.findViewById(R.id.grundgebuehr_single);
            GrundgebuehrSingle.setText(Helper.number_format(TM.get("grundgebuehr")) + " €");

            TextView Laufzeit = context.findViewById(R.id.Laufzeit);
            Laufzeit.setText(TM.get("Laufzeit"));

            TextView Verlaengerung = context.findViewById(R.id.Verlaengerung);
            Verlaengerung.setText(TM.get("Verlaengerung"));

            TextView Kuendigungsfrist = context.findViewById(R.id.Kuendigungsfrist);
            Kuendigungsfrist.setText(TM.get("Kuendigungsfrist"));

            EditText Tarifschluessel = context.findViewById(R.id.tarifschluessel);
            Tarifschluessel.setText(TM.get("tarifschluessel"));

            // TR
            TableRow tr_grundgebuehr_single = context.findViewById(R.id.grundgebuehr_tr_single);
            TableRow tr_grundgebuehr_12 = context.findViewById(R.id.grundgebuehr_tr_12);
            TableRow tr_grundgebuehr_13 = context.findViewById(R.id.grundgebuehr_tr_13);

            if (TM.get("grundgebuehr2").equals(0) || TM.get("grundgebuehr2").equals("0") || TM.get("grundgebuehr2").equals("0,00") || TM.get("grundgebuehr2").equals("0.00")) {
                tr_grundgebuehr_12.setVisibility(View.GONE);
                tr_grundgebuehr_13.setVisibility(View.GONE);
                tr_grundgebuehr_single.setVisibility(View.VISIBLE);
            } else {
                tr_grundgebuehr_single.setVisibility(View.GONE);
                tr_grundgebuehr_12.setVisibility(View.VISIBLE);
                tr_grundgebuehr_13.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * OnClickListener which is used to update the product information whenever
     * the button is pressed
     */
    private View.OnClickListener mOnRequest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EventListener.do_action("contract_update_product_information", mZip.getText(), mType, ProductInformation.this);
        }
    };

    /**
     * OnClickListener which is used to take the zip information to a new contract
     */
    private View.OnClickListener mOnCreate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProductInformation.this, ContractPersonal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("zip", "" + mZip.getText());
            intent.putExtra("type", "" + mType);
            startActivity(intent);
        }
    };

    public View.OnClickListener mTypeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton button = findViewById(R.id.typeGas);
            mType = "Strom";

            if (v.getId() == R.id.typeGas) {
                button = findViewById(R.id.typePower);
                mType = "Gas";
            }

            button.setChecked(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        /**
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(ProductInformation.this);
        ToolbarHandler.getInstance().setSubtitle("Produkt-Infos");

        // Actions
        EventListener.add_action("contract_update_product_information", ProductInformation.update_product_information, 10, 3);

        // Interactions
        Button ButtonRequest = findViewById(R.id.buttonRequest);
        ButtonRequest.setOnClickListener(mOnRequest);

        ImageButton ButtonUpdate = findViewById(R.id.btnProductRefresh);
        ButtonUpdate.setOnClickListener(mOnRequest);

        Button ButtonCreate = findViewById(R.id.button_create_product_information);
        ButtonCreate.setOnClickListener(mOnCreate);

        RadioButton ButtonTypeGas = findViewById(R.id.typeGas);
        RadioButton ButtonTypePower = findViewById(R.id.typePower);

        ButtonTypeGas.setOnClickListener(mTypeOnClickListener);
        ButtonTypePower.setOnClickListener(mTypeOnClickListener);

        // Initialization
        mZip = findViewById(R.id.zip);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_product_information);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(ProductInformation.this));

        NavigationHandler.update(ProductInformation.this);
    }
}
