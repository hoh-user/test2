package forge12.x_citeapi.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import forge12.x_citeapi.EventListener;
import forge12.x_citeapi.Handler.ContractNavigationHandler;
import forge12.x_citeapi.Handler.NavigationHandler;
import forge12.x_citeapi.Handler.SignatureHandler;
import forge12.x_citeapi.Handler.ToolbarHandler;
import forge12.x_citeapi.Model.ContractImageModel;
import forge12.x_citeapi.Model.ContractModel;
import forge12.x_citeapi.Model.TarifModel;
import forge12.x_citeapi.R;
import forge12.x_citeapi.Utils.AlertHelper;
import forge12.x_citeapi.Utils.CustomEventListener;
import forge12.x_citeapi.Utils.Helper;
import forge12.x_citeapi.interfaces.CallableAction;

public class ContractPersonalEdit extends AppCompatActivity {

    /**
     * Button to Save the Contract
     */
    private EditText mStromMovingInDate = null;
    private EditText mStromDistributorChangeDate = null;
    private EditText mGasMovingInDate = null;
    private EditText mGasDistributorChangeDate = null;
    private EditText mKautionMovingInDate = null;
    private EditText mKautionDistributorChangeDate = null;
    private EditText mBirthday = null;
    private EditText mBirthdayLeaser = null;
    private Bitmap mSignature = null;
    private String mVertragsart = "Strom"; // Strom, Gas, Kaution
    private EditText mZip = null;

    ContractModel CM = null;
    TarifModel TM = null;

    /**
     * Called when the Product Information should be updated automatically by changing the ZIP
     */
    private View.OnFocusChangeListener mOnProductRefreshFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean focused) {
            if (!focused) {
                EventListener.do_action("contract_update_product_information", mZip.getText(), mVertragsart, ContractPersonalEdit.this);
            }
        }
    };

    /**
     * Called when the Product Information should be updated manually
     */
    private View.OnClickListener mOnProductRefresh = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EventListener.do_action("contract_update_product_information", mZip.getText(), mVertragsart, ContractPersonalEdit.this);
        }
    };

    /**
     * Override the onBackPressed to add a confirmation popup
     * to the interaction.
     */
    @Override
    public void onBackPressed() {
        AlertHelper.showOnBackPressedConfirmation(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContractPersonalEdit.super.onBackPressed();
            }
        });
    }

    /**
     * Show Datepicker Dialog for Moving In
     *
     * @param v
     */
    public void showDatePickerDialog(final View v) {
        final DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setOnDateSetListener(new DatePickerListener() {
            @Override
            public void onDataSet(DatePicker view, int year, int month, int day) {

                // Gas
                if (v.getId() == R.id.gas_moving_in_date) {
                    mGasMovingInDate.setText(day + "." + month + "." + year);
                }
                if (v.getId() == R.id.gas_distributor_change_date) {
                    mGasDistributorChangeDate.setText(day + "." + month + "." + year);
                }

                // Strom
                if (v.getId() == R.id.strom_moving_in_date) {
                    mStromMovingInDate.setText(day + "." + month + "." + year);
                }
                if (v.getId() == R.id.strom_distributor_change_date) {
                    mStromDistributorChangeDate.setText(day + "." + month + "." + year);
                }

                // Kaution
                if (v.getId() == R.id.kaution_moving_in_date) {
                    mKautionMovingInDate.setText(day + "." + month + "." + year);
                }
                if (v.getId() == R.id.kaution_distributor_change_date) {
                    mKautionDistributorChangeDate.setText(day + "." + month + "." + year);
                }

                // Default
                if (v.getId() == R.id.birthday) {
                    mBirthday.setText(day + "." + month + "." + year);
                }

                if (v.getId() == R.id.mieter_1_gebdat) {
                    mBirthdayLeaser.setText(day + "." + month + "." + year);
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Change visibility of fields to ensure that the view is optimized
     * for the user
     *
     * @return CompoundButton.OnCheckedChangeListener
     */
    protected CompoundButton.OnCheckedChangeListener toggleVisibility() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TableLayout container;

                switch (buttonView.getId()) {
                    case R.id.gas_moving_in:
                    case R.id.strom_moving_in:
                    case R.id.gas_distributor_change:
                    case R.id.strom_distributor_change:
                        if (!isChecked) {
                            break;
                        }

                        CheckBox gas_moving_in = findViewById(R.id.gas_moving_in);
                        CheckBox gas_distributor_change = findViewById(R.id.gas_distributor_change);

                        CheckBox strom_moving_in = findViewById(R.id.strom_moving_in);
                        CheckBox strom_distributor_change = findViewById(R.id.strom_distributor_change);

                        TableRow gas_moving_in_container = findViewById(R.id.gas_moving_in_date_container);
                        TableRow gas_distributor_container = findViewById(R.id.gas_distributor_change_date_container);

                        TableRow strom_moving_in_container = findViewById(R.id.strom_moving_in_date_container);
                        TableRow strom_distributor_container = findViewById(R.id.strom_distributor_change_date_container);

                        try {
                            if (gas_moving_in.getId() == buttonView.getId()) {
                                gas_distributor_change.setChecked(false);
                                gas_moving_in_container.setVisibility(View.VISIBLE);
                                gas_distributor_container.setVisibility(View.GONE);
                            }

                            if (gas_distributor_change.getId() == buttonView.getId()) {
                                gas_moving_in.setChecked(false);
                                gas_moving_in_container.setVisibility(View.GONE);
                                gas_distributor_container.setVisibility(View.VISIBLE);
                            }

                            if (strom_moving_in.getId() == buttonView.getId()) {
                                strom_distributor_change.setChecked(false);
                                strom_moving_in_container.setVisibility(View.VISIBLE);
                                strom_distributor_container.setVisibility(View.GONE);
                            }

                            if (strom_distributor_change.getId() == buttonView.getId()) {
                                strom_moving_in.setChecked(false);
                                strom_moving_in_container.setVisibility(View.GONE);
                                strom_distributor_container.setVisibility(View.VISIBLE);
                            }
                        } catch (NullPointerException e) {
                            Log.v("NULLPOINTER", e.getMessage());
                        }
                        break;
                }
            }
        };
    }

    public CallableAction addPersonalData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("salutation", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.salutation));
            Contract.add("anrede_titel", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.anrede_titel));
            Contract.add("lastname", Helper.getText(ContractPersonalEdit.this, R.id.lastname));
            Contract.add("firstname", Helper.getText(ContractPersonalEdit.this, R.id.firstname));
            Contract.add("street", Helper.getText(ContractPersonalEdit.this, R.id.street));
            Contract.add("housenumber", Helper.getText(ContractPersonalEdit.this, R.id.housenumber));
            Contract.add("zip", Helper.getText(ContractPersonalEdit.this, R.id.zip));
            Contract.add("city", Helper.getText(ContractPersonalEdit.this, R.id.city));
            Contract.add("email", Helper.getText(ContractPersonalEdit.this, R.id.email));
            Contract.add("birthday", Helper.getText(ContractPersonalEdit.this, R.id.birthday, "1970-01-01"));
            Contract.add("phone", Helper.getText(ContractPersonalEdit.this, R.id.phone));
            Contract.add("mobil", Helper.getText(ContractPersonalEdit.this, R.id.mobil));
        }
    };

    public CallableAction addInvoiceRecipientData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("invoice_recipient", Helper.getText(ContractPersonalEdit.this, R.id.invoice_recipient));
            Contract.add("invoice_recipient_street", Helper.getText(ContractPersonalEdit.this, R.id.invoice_recipient_street));
            Contract.add("invoice_recipient_housenumber", Helper.getText(ContractPersonalEdit.this, R.id.invoice_recipient_housenumber));
            Contract.add("invoice_recipient_zip", Helper.getText(ContractPersonalEdit.this, R.id.invoice_recipient_zip));
            Contract.add("invoice_recipient_city", Helper.getText(ContractPersonalEdit.this, R.id.invoice_recipient_city));
        }
    };

    public CallableAction addBusinessInformationData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("company", Helper.getText(ContractPersonalEdit.this, R.id.company));
            Contract.add("company_register_number", Helper.getText(ContractPersonalEdit.this, R.id.company_register_number));
            Contract.add("company_register_court", Helper.getText(ContractPersonalEdit.this, R.id.company_register_court));
        }
    };

    public CallableAction addBankData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("account_iban", Helper.getText(ContractPersonalEdit.this, R.id.account_iban));
            Contract.add("account_holder", Helper.getText(ContractPersonalEdit.this, R.id.account_holder));
            Contract.add("account_sepa", Helper.getBool(ContractPersonalEdit.this, R.id.account_sepa));
        }
    };

    public CallableAction addLeaserData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("mieter_1_anrede", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.mieter_1_anrede));
            Contract.add("mieter_1_titel", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.mieter_1_titel));
            Contract.add("mieter_1_vorname", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_vorname));
            Contract.add("mieter_1_nachname", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_nachname));
            Contract.add("mieter_1_strasse", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_strasse));
            Contract.add("mieter_1_hnr", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_hnr));
            Contract.add("mieter_1_plz", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_plz));
            Contract.add("mieter_1_ort", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_ort));
            Contract.add("mieter_1_email", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_email));
            Contract.add("mieter_1_tel", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_tel));
            Contract.add("mieter_1_gebdat", Helper.getText(ContractPersonalEdit.this, R.id.mieter_1_gebdat, "1970-01-01"));
        }
    };

    public CallableAction addLessorData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("vermieter_anrede", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.vermieter_anrede));
            Contract.add("vermieter_vorname", Helper.getText(ContractPersonalEdit.this, R.id.vermieter_vorname));
            Contract.add("vermieter_nachname", Helper.getText(ContractPersonalEdit.this, R.id.vermieter_nachname));
            Contract.add("vermieter_strasse", Helper.getText(ContractPersonalEdit.this, R.id.vermieter_strasse));
            Contract.add("vermieter_hnr", Helper.getText(ContractPersonalEdit.this, R.id.vermieter_hnr));
            Contract.add("vermieter_plz", Helper.getText(ContractPersonalEdit.this, R.id.vermieter_plz));
            Contract.add("vermiter_ort", Helper.getText(ContractPersonalEdit.this, R.id.vermiter_ort));
        }
    };

    public CallableAction addSignatureData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            Contract.add("ordering", Helper.getBool(ContractPersonalEdit.this, R.id.ordering));
            Contract.add("marketing", Helper.getBool(ContractPersonalEdit.this, R.id.marketing));
        }
    };

    public CallableAction addKautionData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            if (!Contract.get("vertragsart").equals("Kaution")) {
                return;
            }

            Contract.add("whg_miete", Helper.getText(ContractPersonalEdit.this, R.id.whg_miete));
            Contract.add("whg_kaution", Helper.getText(ContractPersonalEdit.this, R.id.whg_kaution));
            Contract.add("whg_temp", Helper.getBool(ContractPersonalEdit.this, R.id.whg_temp));
            Contract.add("whg_kuendigung", Helper.getBool(ContractPersonalEdit.this, R.id.whg_kuendigung));
            Contract.add("whg_mietverzug", Helper.getBool(ContractPersonalEdit.this, R.id.whg_mietverzug));
            Contract.add("whg_schaeden", Helper.getBool(ContractPersonalEdit.this, R.id.whg_schaeden));
            Contract.add("whg_kaution_no", Helper.getBool(ContractPersonalEdit.this, R.id.whg_kaution_no));
            Contract.add("whg_kaution_bes_no", Helper.getBool(ContractPersonalEdit.this, R.id.whg_kaution_bes_no));
            Contract.add("whg_versand", Helper.getSpinnerText(ContractPersonalEdit.this, R.id.whg_versand));

            Contract.add("stromlieferung_strasse", Helper.getText(ContractPersonalEdit.this, R.id.stromlieferung_strasse));
            Contract.add("stromlieferung_hausnummer", Helper.getText(ContractPersonalEdit.this, R.id.stromlieferung_hausnummer));
            Contract.add("stromlieferung_ort", Helper.getText(ContractPersonalEdit.this, R.id.stromlieferung_ort));
            Contract.add("stromlieferung_plz", Helper.getText(ContractPersonalEdit.this, R.id.stromlieferung_plz));

            Contract.add("moving_in", Helper.getBool(ContractPersonalEdit.this, R.id.kaution_moving_in));
            Contract.add("moving_in_date", Helper.getText(ContractPersonalEdit.this, R.id.kaution_moving_in_date, "1970-01-01"));
            Contract.add("distributor_change", Helper.getBool(ContractPersonalEdit.this, R.id.kaution_distributor_change));
            Contract.add("distributor_change_date", Helper.getText(ContractPersonalEdit.this, R.id.kaution_distributor_change_date, "1970-01-01"));
        }
    };

    public CallableAction addStromData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            if (!Contract.get("vertragsart").equals("Strom")) {
                return;
            }

            Contract.add("moving_in", Helper.getBool(ContractPersonalEdit.this, R.id.strom_moving_in));
            Contract.add("moving_in_date", Helper.getText(ContractPersonalEdit.this, R.id.strom_moving_in_date, "1970-01-01"));
            Contract.add("distributor_change", Helper.getBool(ContractPersonalEdit.this, R.id.strom_distributor_change));
            Contract.add("distributor_name", Helper.getText(ContractPersonalEdit.this, R.id.strom_distributor_name));
            Contract.add("distributor_change_date", Helper.getText(ContractPersonalEdit.this, R.id.strom_distributor_change_date, "1970-01-01"));
            Contract.add("distributor_number", Helper.getText(ContractPersonalEdit.this, R.id.strom_distributor_number));
            Contract.add("electric_meter_number", Helper.getText(ContractPersonalEdit.this, R.id.strom_electric_meter_number));
            Contract.add("electric_meter_reading", Helper.getText(ContractPersonalEdit.this, R.id.strom_electric_meter_reading));
            Contract.add("power_consumption", Helper.getText(ContractPersonalEdit.this, R.id.strom_power_consumption));
        }
    };

    public CallableAction addGasData = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            AppCompatActivity context;
            ContractModel Contract;

            if (args[0] instanceof ContractModel) {
                Contract = (ContractModel) args[0];
            } else {
                return;
            }

            if (args[1] instanceof AppCompatActivity) {
                context = (AppCompatActivity) args[1];
            } else {
                return;
            }

            if (!Contract.get("vertragsart").equals("Gas")) {
                return;
            }

            Contract.add("moving_in", Helper.getBool(ContractPersonalEdit.this, R.id.gas_moving_in));
            Contract.add("moving_in_date", Helper.getText(ContractPersonalEdit.this, R.id.gas_moving_in_date, "1970-01-01"));
            Contract.add("distributor_change", Helper.getBool(ContractPersonalEdit.this, R.id.gas_distributor_change));
            Contract.add("distributor_name", Helper.getText(ContractPersonalEdit.this, R.id.gas_distributor_name));
            Contract.add("distributor_change_date", Helper.getText(ContractPersonalEdit.this, R.id.gas_distributor_change_date, "1970-01-01"));
            Contract.add("distributor_number", Helper.getText(ContractPersonalEdit.this, R.id.gas_distributor_number));
            Contract.add("electric_meter_number", Helper.getText(ContractPersonalEdit.this, R.id.gas_electric_meter_number));
            Contract.add("electric_meter_reading", Helper.getText(ContractPersonalEdit.this, R.id.gas_electric_meter_reading));
            Contract.add("power_consumption", Helper.getText(ContractPersonalEdit.this, R.id.gas_power_consumption));
        }
    };

    /**
     * Called whenever the contract should be saved to the database
     */
    private CallableAction mSaveContract = new CallableAction() {
        @Override
        public void onCall(Object... args) {
            int sync_ready = 0;

            if (args[0] instanceof Integer) {
                sync_ready = (int) args[0];
            }

            Intent EditIntent = getIntent();
            int ID = Integer.parseInt(EditIntent.getStringExtra("ID"));

            ContractModel Contract = ContractModel.load(ContractPersonalEdit.this, ID, null);

            Contract.add("ID", ID);
            Contract.add("sync_ready", sync_ready);
            Contract.add("vertragsart", mVertragsart);

            EventListener.do_action("before_contract_saved", Contract, ContractPersonalEdit.this);

            // Saving the data
            boolean res = Contract.save(ContractPersonalEdit.this, new CustomEventListener() {
                @Override
                public void sendMessage(String message) {
                    Toast.makeText(ContractPersonalEdit.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void sendMessage(String message, int option) {
                    sendMessage(message);
                }

                @Override
                public void sendMessage(String message, int option, int code) {
                    sendMessage(message);
                    if (code == 200) {
                        Intent intent = new Intent(ContractPersonalEdit.this, ContractList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }
            });

            EventListener.do_action("after_contract_saved", Contract.get("ID"), ContractPersonalEdit.this, mSignature);
        }
    };

    protected void onCreateStrom() {
        TableRow MovingInDateContainer = findViewById(R.id.strom_moving_in_date_container);
        TableRow DistributorDateContainer = findViewById(R.id.strom_distributor_change_date_container);

        MovingInDateContainer.setVisibility(View.GONE);
        DistributorDateContainer.setVisibility(View.GONE);

        CheckBox MovingIn = findViewById(R.id.strom_moving_in);
        CheckBox DistributorChange = findViewById(R.id.strom_distributor_change);

        MovingIn.setOnCheckedChangeListener(toggleVisibility());
        DistributorChange.setOnCheckedChangeListener(toggleVisibility());

        mStromMovingInDate = findViewById(R.id.strom_moving_in_date);
        mStromDistributorChangeDate = findViewById(R.id.strom_distributor_change_date);

        if (CM.get("moving_in").equals("1")) {
            MovingInDateContainer.setVisibility(View.VISIBLE);
        }

        if (CM.get("distributor_change").equals("1")) {
            DistributorDateContainer.setVisibility(View.VISIBLE);
        }

        setCheckbox(R.id.strom_moving_in, CM.get("moving_in"));
        setEditText(R.id.strom_moving_in_date, CM.get("moving_in_date"));
        setCheckbox(R.id.strom_distributor_change, CM.get("distributor_change"));
        setEditText(R.id.strom_distributor_change_date, CM.get("distributor_change_date"));
        setEditText(R.id.strom_distributor_name, CM.get("distributor_name"));
        setEditText(R.id.strom_distributor_number, CM.get("distributor_number"));
        setEditText(R.id.strom_electric_meter_number, CM.get("electric_meter_number"));
        setEditText(R.id.strom_electric_meter_reading, CM.get("electric_meter_reading"));
        setEditText(R.id.strom_power_consumption, CM.get("power_consumption"));
    }

    protected void onCreateGas() {
        TableRow MovingInDateContainer = findViewById(R.id.gas_moving_in_date_container);
        TableRow DistributorDateContainer = findViewById(R.id.gas_distributor_change_date_container);

        MovingInDateContainer.setVisibility(View.GONE);
        DistributorDateContainer.setVisibility(View.GONE);

        CheckBox MovingIn = findViewById(R.id.gas_moving_in);
        CheckBox DistributorChange = findViewById(R.id.gas_distributor_change);

        MovingIn.setOnCheckedChangeListener(toggleVisibility());
        DistributorChange.setOnCheckedChangeListener(toggleVisibility());

        mGasMovingInDate = findViewById(R.id.gas_moving_in_date);
        mGasDistributorChangeDate = findViewById(R.id.gas_distributor_change_date);

        if (CM.get("moving_in").equals("1")) {
            MovingInDateContainer.setVisibility(View.VISIBLE);
        }

        if (CM.get("distributor_change").equals("1")) {
            DistributorDateContainer.setVisibility(View.VISIBLE);
        }

        setCheckbox(R.id.gas_moving_in, CM.get("moving_in"));
        setEditText(R.id.gas_moving_in_date, CM.get("moving_in_date"));
        setCheckbox(R.id.gas_distributor_change, CM.get("distributor_change"));
        setEditText(R.id.gas_distributor_change_date, CM.get("distributor_change_date"));
        setEditText(R.id.gas_distributor_name, CM.get("distributor_name"));
        setEditText(R.id.gas_distributor_number, CM.get("distributor_number"));
        setEditText(R.id.gas_electric_meter_number, CM.get("electric_meter_number"));
        setEditText(R.id.gas_electric_meter_reading, CM.get("electric_meter_reading"));
        setEditText(R.id.gas_power_consumption, CM.get("power_consumption"));
    }

    protected boolean setCheckbox(int view, String value) {
        CheckBox ckb = findViewById(view);
        if (ckb == null) {
            return false;
        }

        if (value.equals("1")) {
            ckb.setChecked(true);
        }
        return true;
    }

    protected boolean setEditText(int view, String text) {
        EditText edt = findViewById(view);
        if (edt == null) {
            return false;
        }

        edt.setText(text);
        return true;
    }

    protected void onCreatePersonal() {
        mBirthday = findViewById(R.id.birthday);

        /*
         * Add Salutation Spinner
         */
        Spinner mSalutation = findViewById(R.id.salutation);
        ArrayAdapter<CharSequence> salutation_items = ArrayAdapter
                .createFromResource(this, R.array.salutation_array,
                        android.R.layout.simple_spinner_item);

        salutation_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSalutation.setAdapter(salutation_items);

        if (!CM.get("salutation").isEmpty()) {
            int spinnerPosition = salutation_items.getPosition(CM.get("salutation"));
            mSalutation.setSelection(spinnerPosition);
        }

        /*
         * Add Title spinner
         */
        Spinner mTitle = findViewById(R.id.anrede_titel);
        ArrayAdapter<CharSequence> title_items = ArrayAdapter
                .createFromResource(this, R.array.title_array,
                        android.R.layout.simple_spinner_item);

        title_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mTitle.setAdapter(title_items);

        if (!CM.get("anrede_titel").isEmpty()) {
            int spinnerPosition = title_items.getPosition(CM.get("anrede_titel"));
            mTitle.setSelection(spinnerPosition);
        }

        /**
         * Add ZIP & Refresh
         */
        mZip = findViewById(R.id.zip);
        ImageButton mBtnProductRefresh = findViewById(R.id.btnProductRefresh);
        mBtnProductRefresh.setOnClickListener(mOnProductRefresh);
        mZip.setOnFocusChangeListener(mOnProductRefreshFocusChange);

        setEditText(R.id.lastname, CM.get("lastname"));
        setEditText(R.id.firstname, CM.get("firstname"));
        setEditText(R.id.street, CM.get("street"));
        setEditText(R.id.housenumber, CM.get("housenumber"));
        setEditText(R.id.zip, CM.get("zip"));
        setEditText(R.id.city, CM.get("city"));
        setEditText(R.id.email, CM.get("email"));
        setEditText(R.id.birthday, CM.get("birthday"));
        setEditText(R.id.phone, CM.get("phone"));
        setEditText(R.id.mobil, CM.get("mobil"));

        EventListener.do_action("contract_update_product_information", mZip.getText(), mVertragsart, ContractPersonalEdit.this);
    }

    protected void onCreateInvoiceRecipient() {
        setEditText(R.id.invoice_recipient, CM.get("invoice_recipient"));
        setEditText(R.id.invoice_recipient_street, CM.get("invoice_recipient_street"));
        setEditText(R.id.invoice_recipient_housenumber, CM.get("invoice_recipient_housenumber"));
        setEditText(R.id.invoice_recipient_zip, CM.get("invoice_recipient_zip"));
        setEditText(R.id.invoice_recipient_city, CM.get("invoice_recipient_city"));
    }

    protected void onCreateBusinessInformation() {
        setEditText(R.id.company, CM.get("company"));
        setEditText(R.id.company_register_number, CM.get("company_register_number"));
        setEditText(R.id.company_register_court, CM.get("company_register_court"));
    }

    protected void onCreateBank() {
        setEditText(R.id.account_iban, CM.get("account_iban"));
        setEditText(R.id.account_holder, CM.get("account_holder"));
        setCheckbox(R.id.account_sepa, CM.get("account_sepa"));
    }

    protected void onCreateLeaser() {
        mBirthdayLeaser = findViewById(R.id.mieter_1_gebdat);

        /*
         * Mieter anrede
         */
        Spinner mMieter1Anrede = findViewById(R.id.mieter_1_anrede);

        ArrayAdapter<CharSequence> salutation_items = ArrayAdapter
                .createFromResource(this, R.array.salutation_array,
                        android.R.layout.simple_spinner_item);

        salutation_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMieter1Anrede.setAdapter(salutation_items);

        if (!CM.get("mieter_1_anrede").isEmpty()) {
            int spinnerPosition = salutation_items.getPosition(CM.get("mieter_1_anrede"));
            mMieter1Anrede.setSelection(spinnerPosition);
        }

        /*
         * Mieter titel
         */
        Spinner mMieter1Titel = findViewById(R.id.mieter_1_titel);

        ArrayAdapter<CharSequence> title_items = ArrayAdapter
                .createFromResource(this, R.array.title_array,
                        android.R.layout.simple_spinner_item);

        title_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mMieter1Titel.setAdapter(title_items);

        if (!CM.get("mieter_1_titel").isEmpty()) {
            int spinnerPosition = title_items.getPosition(CM.get("mieter_1_titel"));
            mMieter1Titel.setSelection(spinnerPosition);
        }

        setEditText(R.id.mieter_1_vorname, CM.get("mieter_1_vorname"));
        setEditText(R.id.mieter_1_nachname, CM.get("mieter_1_nachname"));
        setEditText(R.id.mieter_1_strasse, CM.get("mieter_1_strasse"));
        setEditText(R.id.mieter_1_hnr, CM.get("mieter_1_hnr"));
        setEditText(R.id.mieter_1_plz, CM.get("mieter_1_plz"));
        setEditText(R.id.mieter_1_ort, CM.get("mieter_1_ort"));
        setEditText(R.id.mieter_1_email, CM.get("mieter_1_email"));
        setEditText(R.id.mieter_1_tel, CM.get("mieter_1_tel"));
        setEditText(R.id.mieter_1_gebdat, CM.get("mieter_1_gebdat"));
    }

    protected void onCreateLessor() {
        /*
         * Vermieter anrede
         */
        Spinner mVermieterAnrede = findViewById(R.id.vermieter_anrede);

        ArrayAdapter<CharSequence> salutation_items = ArrayAdapter
                .createFromResource(this, R.array.salutation_array,
                        android.R.layout.simple_spinner_item);

        mVermieterAnrede.setAdapter(salutation_items);

        if (!CM.get("vermieter_anrede").isEmpty()) {
            int spinnerPosition = salutation_items.getPosition(CM.get("vermieter_anrede"));
            mVermieterAnrede.setSelection(spinnerPosition);
        }

        setEditText(R.id.vermieter_vorname, CM.get("vermieter_vorname"));
        setEditText(R.id.vermieter_nachname, CM.get("vermieter_nachname"));
        setEditText(R.id.vermieter_strasse, CM.get("vermieter_strasse"));
        setEditText(R.id.vermieter_hnr, CM.get("vermieter_hnr"));
        setEditText(R.id.vermieter_plz, CM.get("vermieter_plz"));
        setEditText(R.id.vermiter_ort, CM.get("vermiter_ort"));
    }


    protected void onCreateSignature() {
        /**
         * signature
         */
        final ImageView imageSignature = findViewById(R.id.imageSignature);
        ContractImageModel CIM = ContractImageModel.load(ContractPersonalEdit.this, Integer.parseInt(CM.get("ID")), "signature", null);

        if (CIM.isValid()) {
            mSignature = Helper.convertBase64ToBitmap(CIM.get("image"));
            imageSignature.setImageBitmap(mSignature);
        }

        imageSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignatureDialog.show(ContractPersonalEdit.this, new SignatureDialog.OnSignatureEventListener() {
                    @Override
                    public void onSave(Bitmap bmp) {
                        imageSignature.setImageBitmap(bmp);
                        mSignature = bmp;
                    }
                }, mSignature);
            }
        });

        setCheckbox(R.id.ordering, CM.get("ordering"));
        setCheckbox(R.id.marketing, CM.get("marketing"));
    }

    protected void onCreateKaution() {
        mKautionMovingInDate = findViewById(R.id.kaution_moving_in_date);
        mKautionDistributorChangeDate = findViewById(R.id.kaution_distributor_change_date);
        /*
         * Add Kaution Versand spinner
         */
        Spinner mVersand = findViewById(R.id.whg_versand);
        ArrayAdapter<CharSequence> versand_items = ArrayAdapter
                .createFromResource(this, R.array.whg_versand_array,
                        android.R.layout.simple_spinner_item);

        versand_items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mVersand.setAdapter(versand_items);

        if (!CM.get("whg_versand").isEmpty()) {
            int spinnerPosition = versand_items.getPosition(CM.get("whg_versand"));
            mVersand.setSelection(spinnerPosition);
        }

        setEditText(R.id.whg_miete, CM.get("whg_miete"));
        setEditText(R.id.whg_kaution, CM.get("whg_kaution"));
        setCheckbox(R.id.whg_temp, CM.get("whg_temp"));
        setCheckbox(R.id.whg_kuendigung, CM.get("whg_kuendigung"));
        setCheckbox(R.id.whg_mietverzug, CM.get("whg_mietverzug"));
        setCheckbox(R.id.whg_schaeden, CM.get("whg_schaeden"));
        setCheckbox(R.id.whg_kaution_no, CM.get("whg_kaution_no"));
        setCheckbox(R.id.whg_kaution_bes_no, CM.get("whg_kaution_bes_no"));

        setEditText(R.id.stromlieferung_strasse, CM.get("stromlieferung_strasse"));
        setEditText(R.id.stromlieferung_hausnummer, CM.get("stromlieferung_hausnummer"));
        setEditText(R.id.stromlieferung_ort, CM.get("stromlieferung_ort"));
        setEditText(R.id.stromlieferung_plz, CM.get("stromlieferung_plz"));

        setCheckbox(R.id.kaution_moving_in, CM.get("moving_in"));
        setEditText(R.id.kaution_moving_in_date, CM.get("moving_in_date"));
        setCheckbox(R.id.kaution_distributor_change, CM.get("distributor_change"));
        setEditText(R.id.kaution_distributor_change_date, CM.get("distributor_change_date"));
    }

    protected void onCreateProductInformation() {
        // Produktpreise
        ImageButton mBtnProductRefresh = findViewById(R.id.btnProductRefresh);
        /*mTarifschluessel = findViewById(R.id.tarifschluessel);
        mTarifschluessel.setVisibility(View.GONE);*/
        mBtnProductRefresh.setOnClickListener(mOnProductRefresh);
    }


    @Override
    protected void onPause() {

        EventListener.remove_action("after_contract_updated", SignatureHandler.update_signature_image);
        EventListener.remove_action("contract_update_product_information", ProductInformation.update_product_information);

        EventListener.remove_action("contract_save", mSaveContract);
        EventListener.remove_action("before_contract_saved", addStromData);
        EventListener.remove_action("before_contract_saved", addGasData);
        EventListener.remove_action("before_contract_saved", addPersonalData);
        EventListener.remove_action("before_contract_saved", addBankData);
        EventListener.remove_action("before_contract_saved", addBusinessInformationData);
        EventListener.remove_action("before_contract_saved", addInvoiceRecipientData);
        EventListener.remove_action("before_contract_saved", addSignatureData);

        EventListener.remove_action("before_contract_saved", addLeaserData);
        EventListener.remove_action("before_contract_saved", addLessorData);
        EventListener.remove_action("before_contract_saved", addKautionData);

        super.onPause();
    }

    protected String getAGBbyType(String Vertragsart) {
        switch (Vertragsart) {
            case "Gas":
                return getString(R.string.ordering_clause_natural_gas);
            case "Kaution":
                return getString(R.string.ordering_clause_kaution);
            case "Strom":
            default:
                return getString(R.string.ordering_clause);
        }
    }

    protected String getSEPAbyType(String Vertragsart) {
        switch (Vertragsart) {
            case "Kaution":
                return getString(R.string.sepa_clause_kaution);
            case "Gas":
            case "Strom":
            default:
                return getString(R.string.sepa_clause);
        }
    }

    protected String getBlockPersonalByType(String Vertragsart){
        switch(Vertragsart){
            case "Kaution":
                return getString(R.string.personal_customer_data_kaution);
            case "Gas":
            case "Strom":
            default:
                return getString(R.string.personal_customer_data);
        }
    }

    protected void toggleViews(String type, ContractModel CM) {
        HashMap<String, View> Views = new HashMap<String, View>();
        Views.put("header_contract", findViewById(R.id.header_contract));
        Views.put("snippet_personal_container", findViewById(R.id.snippet_personal_container));
        Views.put("snippet_invoice_container", findViewById(R.id.snippet_invoice_container));
        Views.put("snippet_business_container", findViewById(R.id.snippet_business_container));
        Views.put("snippet_bank_container", findViewById(R.id.snippet_bank_container));
        Views.put("snippet_strom_container", findViewById(R.id.snippet_strom_container));
        Views.put("snippet_gas_container", findViewById(R.id.snippet_gas_container));
        Views.put("snippet_product_info_container", findViewById(R.id.snippet_product_info_container));
        Views.put("snippet_leaser_container", findViewById(R.id.snippet_leaser_container));
        Views.put("snippet_lessor_container", findViewById(R.id.snippet_lessor_container));
        Views.put("snippet_kaution_container", findViewById(R.id.snippet_kaution_container));
        Views.put("snippet_signature_container", findViewById(R.id.snippet_signature_container));
        Views.put("scrollview", findViewById(R.id.scrollview));
        Views.put("save_container", findViewById(R.id.save_container));
        Views.put("row_save", findViewById(R.id.row_save));
        Views.put("row_close", findViewById(R.id.row_close));

        if (type.equals("Kaution")) {
            Views.get("header_contract").setVisibility(View.VISIBLE);
            Views.get("snippet_personal_container").setVisibility(View.VISIBLE);
            Views.get("snippet_invoice_container").setVisibility(View.GONE);
            Views.get("snippet_business_container").setVisibility(View.VISIBLE);
            Views.get("snippet_bank_container").setVisibility(View.GONE);
            Views.get("snippet_strom_container").setVisibility(View.GONE);
            Views.get("snippet_gas_container").setVisibility(View.GONE);
            Views.get("snippet_product_info_container").setVisibility(View.GONE);
            Views.get("snippet_leaser_container").setVisibility(View.VISIBLE);
            Views.get("snippet_lessor_container").setVisibility(View.GONE);
            Views.get("snippet_kaution_container").setVisibility(View.GONE);
            Views.get("snippet_signature_container").setVisibility(View.GONE);
            Views.get("save_container").setVisibility(View.GONE);
        } else {
            Views.get("header_contract").setVisibility(View.GONE);
            Views.get("snippet_personal_container").setVisibility(View.VISIBLE);
            Views.get("snippet_invoice_container").setVisibility(View.VISIBLE);
            Views.get("snippet_business_container").setVisibility(View.VISIBLE);
            Views.get("snippet_bank_container").setVisibility(View.VISIBLE);
            Views.get("snippet_product_info_container").setVisibility(View.VISIBLE);
            Views.get("snippet_leaser_container").setVisibility(View.GONE);
            Views.get("snippet_lessor_container").setVisibility(View.GONE);
            Views.get("snippet_kaution_container").setVisibility(View.GONE);
            Views.get("snippet_signature_container").setVisibility(View.VISIBLE);
            Views.get("save_container").setVisibility(View.VISIBLE);

            if (mVertragsart.equals("Strom")) {
                Views.get("snippet_strom_container").setVisibility(View.VISIBLE);
                Views.get("snippet_gas_container").setVisibility(View.GONE);
            } else {
                Views.get("snippet_gas_container").setVisibility(View.VISIBLE);
                Views.get("snippet_strom_container").setVisibility(View.GONE);
            }

            Views.get("row_save").setVisibility(View.GONE);
            Views.get("row_close").setVisibility(View.GONE);

            if (CM.get("status") != null && CM.get("status").length() != 0 && !CM.get("status").equals("0")) {
                Views.get("row_close").setVisibility(View.VISIBLE);
            } else {
                Views.get("row_save").setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add Actions
        EventListener.add_action("after_contract_saved", SignatureHandler.update_signature_image, 10, 3);
        EventListener.add_action("contract_update_product_information", ProductInformation.update_product_information, 10, 3);
        EventListener.add_action("contract_save", mSaveContract, 10, 1);
        EventListener.add_action("before_contract_saved", addStromData, 10, 2);
        EventListener.add_action("before_contract_saved", addGasData, 10, 2);
        EventListener.add_action("before_contract_saved", addPersonalData, 10, 2);
        EventListener.add_action("before_contract_saved", addInvoiceRecipientData, 10, 2);
        EventListener.add_action("before_contract_saved", addBusinessInformationData, 10, 2);
        EventListener.add_action("before_contract_saved", addBankData, 10, 2);
        EventListener.add_action("before_contract_saved", addLeaserData, 10, 2);
        EventListener.add_action("before_contract_saved", addLessorData, 10, 2);
        EventListener.add_action("before_contract_saved", addSignatureData, 10, 2);
        EventListener.add_action("before_contract_saved", addKautionData, 10, 2);

        setContentView(R.layout.activity_contract_add_personal);

        /*
         * Load Edit ID
         */
        Intent EditIntent = getIntent();
        int ID = Integer.parseInt(EditIntent.getStringExtra("ID"));

        CM = ContractModel.load(ContractPersonalEdit.this, ID, null);
        if (CM == null) {
            Log.v("NULLEXCEPTION", "Contract Model not found with ID " + ID);
            return;
        }

        TM = TarifModel.loadTarif(ContractPersonalEdit.this, CM.get("product_id"), CM.get("zip"), CM.get("vertragsart"), null);

        mVertragsart = CM.get("vertragsart");

        TextView title = findViewById(R.id.contract_title);
        if (mVertragsart.equals("Strom")) {
            title.setText("Stromvertrag");
        } else if (mVertragsart.equals("Gas")) {
            title.setText("Gasvertrag");
        } else {
            title.setText("Kautionsvertrag");
        }

        /**
         * Toggle view for Contracts
         */
        toggleViews(mVertragsart, CM);

        /*
         Set AGB
         */
        String agb = getAGBbyType(mVertragsart);
        CheckBox ordering = findViewById(R.id.ordering);
        ordering.setText(agb);

        /**
         * Set SEPA
         */
        String sepaText = getSEPAbyType(mVertragsart);
        CheckBox sepa = findViewById(R.id.account_sepa);
        sepa.setText(sepaText);

        /**
         * Set text Block 1
         */
        String blockPersonal = getBlockPersonalByType(mVertragsart);
        TextView TextViewPersonal = findViewById(R.id.blockPersonal);
        TextViewPersonal.setText(blockPersonal);

        /**
         * Remove Marketing for Kaution
         */
        if (mVertragsart.equals("Kaution")) {
            TableRow row_marketing = findViewById(R.id.row_marketing);

            if (row_marketing != null) {
                row_marketing.setVisibility(View.GONE);
            }
        }

        /*
          Navigation listener
         */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(NavigationHandler.getBottomNavigation(ContractPersonalEdit.this));

        NavigationView navigation_contract = findViewById(R.id.navigation_contract);
        navigation_contract.setNavigationItemSelectedListener(ContractNavigationHandler.getNavigation(ContractPersonalEdit.this));
        ContractNavigationHandler.loadProductTypeNavigation(navigation_contract, mVertragsart, CM);

        ImageButton NavigationToggle = findViewById(R.id.button_option);
        NavigationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContractNavigationHandler.toggle(ContractPersonalEdit.this);
            }
        });

        /*
         * Add toolbar
         */
        ToolbarHandler.getInstance().setContext(ContractPersonalEdit.this);
        ToolbarHandler.getInstance().setSubtitle("Vertrag bearbeiten");

        /*
         * Toggle Dates
         */
        this.onCreatePersonal();
        this.onCreateStrom();
        this.onCreateGas();
        this.onCreateLeaser();
        this.onCreateLessor();
        this.onCreateSignature();
        this.onCreateKaution();
        this.onCreateInvoiceRecipient();
        this.onCreateBusinessInformation();
        this.onCreateBank();
        this.onCreateProductInformation();

        Button SaveAndSync = findViewById(R.id.buttonSaveAndSync);
        SaveAndSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventListener.do_action("contract_save", 1);
            }
        });

        Button Save = findViewById(R.id.buttonSave);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventListener.do_action("contract_save", 0);
            }
        });

        Button Close = findViewById(R.id.buttonClose);
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * Update Bottom Navigation
         */
        NavigationHandler.update(ContractPersonalEdit.this);
    }
}
