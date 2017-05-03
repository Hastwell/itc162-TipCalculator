package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TipCalculatorActivity extends Activity
{

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;
    private TextView tipTextView;
    private TextView totalTextView;
    private SeekBar tipPercentSeekBar;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tip_calculator);

        // get references to the widgets
        this.billAmountEditText = (EditText) this.findViewById(R.id.billAmountEditText);
        this.percentTextView =    (TextView) this.findViewById(R.id.percentTextView);
        this.tipTextView =        (TextView) this.findViewById(R.id.tipTextView);
        this.totalTextView =      (TextView) this.findViewById(R.id.totalTextView);
        this.tipPercentSeekBar =  (SeekBar) this.findViewById(R.id.tipPercentSeekBar);

        // set the listeners
        this.billAmountEditText.setOnEditorActionListener(this.genericTextboxSubmitEvent);
        this.billAmountEditText.addTextChangedListener(this.genericTextboxInteractEvent);
        this.tipPercentSeekBar.setOnSeekBarChangeListener(this.genericSeekbarInteractEvent);



        // get SharedPreferences object
        this.savedValues = this.getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", this.billAmountString);
        editor.putFloat("tipPercent", this.tipPercent);
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();
    }

    public void calculateAndDisplay() {

        // get the bill amount
        this.billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (this.billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(this.billAmountString);
        }

        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        this.tipTextView.setText(currency.format(tipAmount));
        this.totalTextView.setText(currency.format(totalAmount));

        // update the tip percentage views
        NumberFormat percent = NumberFormat.getPercentInstance();
        this.tipPercentSeekBar.setProgress((int) (tipPercent * 100));
        this.percentTextView.setText(percent.format(tipPercent));

    }

    TextWatcher genericTextboxInteractEvent = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            TipCalculatorActivity.this.calculateAndDisplay();
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };

    OnEditorActionListener genericTextboxSubmitEvent = new OnEditorActionListener()
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
            {
                TipCalculatorActivity.this.calculateAndDisplay();
            }
            return false;
        }
    };

    SeekBar.OnSeekBarChangeListener genericSeekbarInteractEvent = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            // Because the Tip Percentage seek bar can be updated by code, ensure
            // that this input is indeed from the user before calling controller code.
            if(fromUser)
            {
                TipCalculatorActivity.this.tipPercent = TipCalculatorActivity.this.tipPercentSeekBar.getProgress() / 100.0f;
                TipCalculatorActivity.this.calculateAndDisplay();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };
}