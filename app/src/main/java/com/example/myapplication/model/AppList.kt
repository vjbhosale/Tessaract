package com.example.myapplication.model

import android.graphics.drawable.Drawable

class AppList(
  var  appName: String?="",
  var  loadIcon: Drawable,
   var packageName: String?="",
   var versionName: String?="",
   var versionCode: String?=""
) {
}