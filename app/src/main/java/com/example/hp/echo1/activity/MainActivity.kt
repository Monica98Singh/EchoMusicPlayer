package com.example.hp.echo1.activity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.hp.echo1.Fragments.MainScreenFragment
import com.example.hp.echo1.Fragments.SongPlayingFragment
import com.example.hp.echo1.R
import com.example.hp.echo1.adapters.NavigationDrawerAdapter

class MainActivity : AppCompatActivity() {

    var navDrawIconList : ArrayList<String> = arrayListOf()
    var trackNotificationBuilder : Notification?=null
    var images_for_navDrawer= intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites, R.drawable.navigation_settings,R.drawable.navigation_aboutus)
    object Statified{
        var drawerlayout: DrawerLayout?= null
        var notificationManager : NotificationManager?= null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    navDrawIconList.add("All Songs")
        navDrawIconList.add("Favorites")
        navDrawIconList.add("Settings")
        navDrawIconList.add("About Us")
        MainActivity.Statified.drawerlayout= findViewById(R.id.drawer_layout)

        val toggle= ActionBarDrawerToggle(this@MainActivity,MainActivity.Statified.drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.Statified.drawerlayout?.setDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = MainScreenFragment()

        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment, mainScreenFragment,"mainScreenFragment")
                .commit()

        var _navAdapter= NavigationDrawerAdapter(navDrawIconList, images_for_navDrawer, this)
        _navAdapter.notifyDataSetChanged()
        var navigation_recycler_view = findViewById<RecyclerView>(R.id.recycler_view)

        navigation_recycler_view.layoutManager= LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator= DefaultItemAnimator()
        navigation_recycler_view.adapter = _navAdapter
        navigation_recycler_view.setHasFixedSize(true)
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pintent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),
                intent, 0)
        trackNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pintent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()

        Statified.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



    }

    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(2017)


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()

        try {
            if (SongPlayingFragment.stat?.mediaPlayer?.isPlaying as Boolean){
                Statified.notificationManager?.notify(2017, trackNotificationBuilder)

            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(2017)


        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}
