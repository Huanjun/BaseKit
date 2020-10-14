package com.mp.basekit.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.startup.Initializer
import com.chad.library.adapter.base.module.LoadMoreModuleConfig.defLoadMoreView
import com.mp.basekit.util.CrashUtils
import com.mp.basekit.util.FileUtils
import com.mp.basekit.util.LanguageUtils
import com.mp.basekit.util.MPUtils
import com.mp.basekit.view.load.CustomLoadMoreView
import java.io.File

class MPInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        initDir()

        MPUtils.init(context)
        defLoadMoreView = CustomLoadMoreView()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            CrashUtils.init(MPApplication.getMyCrashDir())
        }
    }

    private fun initDir() {
        //图片沙盒文件夹
        //File doc = sApp.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        val doc = MPApplication.SDCARD_ROOT
        FileUtils.createOrExistsDir(File(doc))
        val dir = File("$doc/crash/")
        try {
            if (!dir.exists()) {
                dir.mkdir() //如果该文件夹不存在，则新建
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val docCache = MPApplication.SDCARD_ROOT
        FileUtils.createOrExistsDir(File(docCache))
        val dirCache = File("$docCache/cache/")
        try {
            if (!dirCache.exists()) {
                dirCache.mkdir() //如果该文件夹不存在，则新建
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}
