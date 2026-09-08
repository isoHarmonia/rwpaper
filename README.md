---壁纸存太多了不想浪费怎么办？---


#随机壁纸.apk

点击桌面图标 → 从指定文件夹中随机挑一张图片设为壁纸 → 自动退出，全程无界面(Andriod16目前还会弹出空白界面)。

#如何使用

1.下载压缩包，解压并安装其中的.apk文件。
2.首次点击桌面图标：会弹出系统的文件夹选择器，选中你放图片的那个文件夹即可
   （比如相册里的某个相簿、或者你自己建的图片文件夹）。选完这一次以后就不会
   再弹出来了，app 会记住这个文件夹。
3.之后每次点桌面图标，都会立刻从这个文件夹里随机挑一张图设为壁纸，
   然后弹一个小提示，自动关闭。

#想换一个文件夹怎么办？

代码里目前没做 重新选择 的入口。
系统设置 → 应用管理 → 随机壁纸 → 存储 → 清除数据。
清除后再点一次图标，就会重新弹出文件夹选择器。

#权限说明

用系统的「文件夹选择器」，只授权访问你选中的
那一个文件夹，更安全，也符合 Android 10+ 的分区存储要。

-项目结构

```
RandomWallpaper/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/randomwallpaper/MainActivity.kt   ← 核心逻辑
│       └── res/values/{strings.xml, styles.xml}
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
