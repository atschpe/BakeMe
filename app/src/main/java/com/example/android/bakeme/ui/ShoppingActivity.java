package com.example.android.bakeme.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.bakeme.R;

import com.example.android.bakeme.databinding.ActivityShoppingBinding;

public class ShoppingActivity extends AppCompatActivity {

    ActivityShoppingBinding shoppingBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingBinder = DataBindingUtil.setContentView(this, R.layout.activity_shopping);
    }
}
