package com.androidcollider.easyfin;

import com.androidcollider.easyfin.database.DataSource;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTransactionDate;
    private DatePickerDialog setDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Spinner spinAddTransCategory, spinAddTransExpense;

    DataSource dataSource;

    Dialog dialogNoExpense;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);

        setToolbar(R.string.new_transaction);


        tvTransactionDate = (TextView) findViewById(R.id.tvTransactionDate);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        setDateTimeField();

        Button btnTransactionAdd = (Button) findViewById(R.id.btnTransactionAdd);
        btnTransactionAdd.setOnClickListener(this);

        dataSource = new DataSource(this);

        setSpinner();

    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);}


    private void setSpinner() {
        spinAddTransCategory = (Spinner) findViewById(R.id.spinAddTransCategory);
        spinAddTransExpense = (Spinner) findViewById(R.id.spinAddTransExpense);

        List<String> accounts = dataSource.getAllAccounts();

        if (accounts.size() == 0) {
            showDialogNoExpense();}


        ArrayAdapter<?> adapterTransCat = ArrayAdapter.createFromResource(this, R.array.cat_transaction_array, R.layout.spinner_item);
        adapterTransCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddTransCategory.setAdapter(adapterTransCat);

        ArrayAdapter<?> adapterTransExp = new ArrayAdapter<>(this, R.layout.spinner_item, accounts);
        adapterTransExp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddTransExpense.setAdapter(adapterTransExp);
    }


    private void setDateTimeField() {
        tvTransactionDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvTransactionDate.setText(dateFormatter.format(newCalendar.getTime()));

        setDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvTransactionDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionDate: setDatePickerDialog.show(); break;
            case R.id.btnTransactionAdd: addTransaction(); break;
        }
    }

    private void addTransaction() {
        EditText editTextTransSum = (EditText) findViewById(R.id.editTextTransSum);
        RadioButton radioButtonCost = (RadioButton) findViewById(R.id.radioButtonCost);
        ContentValues cv = new ContentValues();

        String date = tvTransactionDate.getText().toString();
        int id_account = spinAddTransExpense.getSelectedItemPosition() + 1;
        int amount = Integer.parseInt(editTextTransSum.getText().toString());
        if (radioButtonCost.isChecked()) {
            amount *= -1;}
        String category = spinAddTransCategory.getSelectedItem().toString();

        cv.put("date", date);
        cv.put("id_account", id_account);
        cv.put("amount", amount);
        cv.put("category", category);


        dataSource.insertLocal("Transactions", cv);

        Toast.makeText(this, date + " " + id_account + " " + amount + " " + category, Toast.LENGTH_LONG).show();

        closeActivity();
    }

    private void closeActivity() {
        this.finish();
    }

    private void openAddExpenseActivity() {
        Intent intent = new Intent(this, AddExpense.class);
        startActivity(intent);
    }

    private void showDialogNoExpense() {
        dialogNoExpense = new Dialog(this);
        dialogNoExpense.setCanceledOnTouchOutside(false);
        dialogNoExpense.setContentView(R.layout.dialog_no_expense);
        dialogNoExpense.setTitle(getString(R.string.no_expense));

        Button btnDialogNoExpenseReturnToMain = (Button) dialogNoExpense.findViewById(R.id.btnDialogNoExpenseReturnToMain);
        Button btnDialogNoExpenseNewExpense = (Button) dialogNoExpense.findViewById(R.id.btnDialogNoExpenseNewExpense);

        btnDialogNoExpenseReturnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });

        btnDialogNoExpenseNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddNewExpense();
            }
        });

        dialogNoExpense.show();
    }

    public void returnToMain() {
        dialogNoExpense.dismiss();
        closeActivity();
    }

    public void goToAddNewExpense() {
        dialogNoExpense.dismiss();
        closeActivity();
        openAddExpenseActivity();
    }

}
