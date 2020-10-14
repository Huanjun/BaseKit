package com.mp.basekit.util;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import com.mp.basekit.wallet.encrypt.Sha256Hash;

public class PasswordUtils {

    public static String baseShaPsw(String psw) {
        if (TextUtils.isEmpty(psw)) {
            return psw;
        }
        return Base64.encodeToString(Sha256Hash.hash(psw.getBytes()), Base64.NO_WRAP);
    }

    public static void setPswVisible(ImageView imageView, boolean isShowPassword, int imgRes, EditText... editTexts) {
        imageView.setImageResource(imgRes);
        if (isShowPassword) {
            for (EditText editText : editTexts) {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                int select = editText.getText().toString().length();
                editText.setSelection(select);
            }
        } else {
            for (EditText editText : editTexts) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                int select = editText.getText().toString().length();
                editText.setSelection(select);
            }
        }
    }
}
