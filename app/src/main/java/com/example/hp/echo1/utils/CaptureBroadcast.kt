package com.example.hp.echo1.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.hp.echo1.Fragments.SongPlayingFragment
import com.example.hp.echo1.R
import com.example.hp.echo1.activity.MainActivity

/**
 * Created by maniraj on 30/12/17.
 */
class CaptureBroadcast : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statified.notificationManager?.cancel(2017)


            }catch (e:Exception){
                e.printStackTrace()
            }
           try {
               if (SongPlayingFragment.stat.mediaPlayer?.isPlaying as Boolean){
                   SongPlayingFragment.stat.mediaPlayer?.pause()
                   SongPlayingFragment.stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
               }
           }catch (e:Exception){
               e.printStackTrace()
           }
        }else{
            val tm : TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm.callState){
                TelephonyManager.CALL_STATE_RINGING->{
                    try {
                        MainActivity.Statified.notificationManager?.cancel(2017)


                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.stat.mediaPlayer?.isPlaying as Boolean){
                            SongPlayingFragment.stat.mediaPlayer?.pause()
                            SongPlayingFragment.stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                else->{

                }

            }
        }
    }

}