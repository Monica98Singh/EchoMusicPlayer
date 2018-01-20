package com.example.hp.echo1.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.example.hp.echo1.R


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    var myActivity : Activity? = null
    var shakeSwitch: Switch?= null

    object statified{
        var MY_PREFS_NAME = "ShakeFeature"


    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        activity.title = "Settings"
        shakeSwitch = view?.findViewById(R.id.switchShake)
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myActivity?.getSharedPreferences(statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isallowed = prefs?.getBoolean("features", false)
        if (isallowed as Boolean){

            shakeSwitch?.isChecked = true
        }else{
            shakeSwitch?.isChecked = false
        }

        shakeSwitch?.setOnCheckedChangeListener({compoundButton, b ->

            if (b){
                val editor = myActivity?.getSharedPreferences(statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            }else{
                val editor = myActivity?.getSharedPreferences(statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()

            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

}// Required empty public constructor