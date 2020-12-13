package com.example.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.adapter.AppAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.listener.OnItemClickListener
import com.example.myapplication.model.AppList
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException

class MainActivity : AppCompatActivity(), OnItemClickListener {
    override fun onClick(pos: Int, view: View, obj: Any) {
        val data=obj as AppList
        openApp(data)
    }
// explicit intent to open app
    private fun openApp(data: AppList) {
        try {
            val intent=packageManager.getLaunchIntentForPackage(data?.packageName!!)
            startActivity(intent)
        }catch (ex:NullPointerException)
        {
            Toast.makeText(this@MainActivity,"error occured",Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var appAdapter: AppAdapter
    lateinit var  receiver: BroadcastReceiver
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel=ViewModelProvider(this).get(MainViewModel::class.java)

        DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        ).apply {
            this.setLifecycleOwner(this@MainActivity)
            this.viewmodel = mainViewModel
        }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")

        receiver=object :BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                val action=p1?.action.toString()
                if (action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED))
                {
                    sendNotification("App uninstalled!!",p0!!)
                }else if (action.equals(Intent.ACTION_PACKAGE_ADDED))
                {
                    sendNotification("App installed!!",p0!!)
                }

                mainViewModel.AllData.value=getInstalledApps()

            }

        }
        registerReceiver(receiver,filter)
        mainViewModel.AllData.value=getInstalledApps()

        appRV.layoutManager= StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)

        appAdapter= AppAdapter(this@MainActivity,this,null)
        appRV.adapter=appAdapter



        mainViewModel.searchContent.observe(this, Observer {
            MyFilter(it)

        })

        mainViewModel.AllData.observe(this, Observer {

            appAdapter.addData(it)
            MyFilter(mainViewModel.searchContent.value!!)

        })

    }

    private fun sendNotification(msgS: String,context: Context) {

        val intent=Intent(context,MainActivity::class.java)
        val pendingIntent=PendingIntent.getActivity(context,11,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId).setPriority(
            NotificationCompat.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_VIBRATE)
            .setSmallIcon(getNotificationSmallIcon())
            .setContentTitle(msgS)
            // .setContentText(message.data["title"])

            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(msgS)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)


        }

        notificationManager.notify(11   , notificationBuilder.build())


    }


    // VM filter
    private fun MyFilter(filter: String) {

        if (filter.trim().length==0)
        {
            appAdapter.addData(mainViewModel.AllData.value!!)
        }else{
            val filterData=mainViewModel.AllData.value!!.filter {

            it.appName?.toLowerCase()?.contains(filter.toLowerCase())!!
            }

            appAdapter.addData(filterData)
        }

    }

    private fun getInstalledApps(): ArrayList<AppList> {
        var MyList=ArrayList<AppList>()
        val packs=packageManager.getInstalledPackages(0)

      packs.filter {
            !isSystemPackage(it)

        }.forEach {

            MyList.add(AppList(it.applicationInfo.loadLabel(packageManager).toString(),
                it.applicationInfo.loadIcon(packageManager),
                it.applicationInfo.packageName,it.versionName,it.versionCode.toString()
                ))

        }

        return  MyList

    }

    private fun isSystemPackage(it: PackageInfo?): Boolean {

         if (it?.applicationInfo?.flags!! and  ApplicationInfo.FLAG_SYSTEM != 0)
         {
            return true
         }

        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun getNotificationSmallIcon(): Int {
        val whiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (whiteIcon) R.drawable.ic_launchers_transparent else R.drawable.ic_launchers_transparent
    }

}
