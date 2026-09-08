package com.example.randomwallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream

/**
 * 点击桌面图标即触发：
 * 1. 首次运行：弹出系统文件夹选择器，用户选好后记住这个文件夹（长期授权）。
 * 2. 之后每次运行：直接从该文件夹里随机挑一张图片设为壁纸，无需任何界面操作。
 */
class MainActivity : AppCompatActivity() {

    private val prefs by lazy { getSharedPreferences("wallpaper_prefs", MODE_PRIVATE) }

    companion object {
        private const val KEY_FOLDER_URI = "folder_uri"
        private const val REQUEST_CODE_PICK_FOLDER = 1001
        private val IMAGE_MIME_PREFIX = "image/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedUriString = prefs.getString(KEY_FOLDER_URI, null)
        if (savedUriString == null) {
            // 还没选过文件夹，先让用户选
            pickFolder()
        } else {
            val treeUri = Uri.parse(savedUriString)
            applyRandomWallpaperFrom(treeUri)
        }
    }

    private fun pickFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FOLDER) {
            if (resultCode == Activity.RESULT_OK && data?.data != null) {
                val treeUri = data.data!!

                // 拿到长期持久化的读权限，否则下次启动 app 就没法再访问这个文件夹了
                contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                prefs.edit().putString(KEY_FOLDER_URI, treeUri.toString()).apply()
                applyRandomWallpaperFrom(treeUri)
            } else {
                toastAndFinish("未选择文件夹，已取消")
            }
        }
    }

    private fun applyRandomWallpaperFrom(treeUri: Uri) {
        val folder = DocumentFile.fromTreeUri(this, treeUri)
        if (folder == null || !folder.exists()) {
            // 文件夹可能被删除/移动了，清掉记录，让用户重新选
            prefs.edit().remove(KEY_FOLDER_URI).apply()
            toastAndFinish("文件夹已失效，请重新运行选择")
            return
        }

        val images = folder.listFiles().filter { file ->
            file.isFile && (file.type?.startsWith(IMAGE_MIME_PREFIX) == true)
        }

        if (images.isEmpty()) {
            toastAndFinish("文件夹里没有找到图片")
            return
        }

        val picked = images.random()

        try {
            contentResolver.openInputStream(picked.uri)?.use { input: InputStream ->
                val wallpaperManager = WallpaperManager.getInstance(this)
                wallpaperManager.setStream(input)
            }
            toastAndFinish("壁纸已更换：${picked.name}")
        } catch (e: Exception) {
            toastAndFinish("设置壁纸失败：${e.message}")
        }
    }

    private fun toastAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}
