package com.yanlong.im.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordTextWather implements TextWatcher {
    EditText editText;

    public PasswordTextWather(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String editable = editText.getText().toString();
        String regEx = "[^a-zA-Z0-9.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(editable);
        String str = m.replaceAll("").trim();
        if (!editable.equals(str)) {
            editText.setText(str);
            editText.setSelection(str.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
