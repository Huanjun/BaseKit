package com.mp.basekit.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

    public static void clip(Context context, String content) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert clip != null;
        ClipData clipData = ClipData.newPlainText("casino", content);
        clip.setPrimaryClip(clipData); // 复制
    }

}
