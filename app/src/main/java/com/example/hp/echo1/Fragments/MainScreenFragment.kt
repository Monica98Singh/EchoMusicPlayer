package com.example.hp.echo1.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hp.echo1.R
import com.example.hp.echo1.Songs
import com.example.hp.echo1.adapters.MainScreenAdapter
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class MainScreenFragment : Fragment() {
    var getSongList: ArrayList<Songs>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var PlayPauseButton: ImageButton?=null
    var songTitle: TextView?= null
    var visibleLayout: RelativeLayout?= null
    var noSongs: RelativeLayout?= null
    var recyclerview: RecyclerView?=null
    var myActivity : Activity? = null
    var _mainscreenAdapter: MainScreenAdapter?= null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity.title = "All Songs"
        visibleLayout= view?.findViewById<RelativeLayout>(R.id.visible_layout)
        noSongs= view?.findViewById<RelativeLayout>(R.id.no_songs)
        nowPlayingBottomBar= view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
        songTitle=view?.findViewById<TextView>(R.id.songtitle_mainscreen)
        PlayPauseButton= view?.findViewById<ImageButton>(R.id.play_pause_button)
        recyclerview=view?.findViewById<RecyclerView>(R.id.contentMain)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getSongList = getSongsFromPhone()
        val prefs = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending","true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        if (getSongList == null){
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE

        }else{
            if (action_sort_ascending!!.equals("true", true)){
                Collections.sort(getSongList, Songs.STATIFIED.nameComparator)
                _mainscreenAdapter?.notifyDataSetChanged()
            }else if (action_sort_recent!!.equals("true", true)){
                Collections.sort(getSongList,Songs.STATIFIED.dateComparator)
                _mainscreenAdapter?.notifyDataSetChanged()
            }
        }


        _mainscreenAdapter= MainScreenAdapter(getSongList as ArrayList<Songs>, myActivity as Context)
        val mLayoutManager= LinearLayoutManager(myActivity)
        recyclerview?.layoutManager = mLayoutManager
        recyclerview?.itemAnimator= DefaultItemAnimator()
        recyclerview?.adapter= _mainscreenAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
    return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending){
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongList != null){
                Collections.sort(getSongList, Songs.STATIFIED.nameComparator)
            }
            _mainscreenAdapter?.notifyDataSetChanged()
            return false
        }else if (switcher==R.id.action_sort_recent){
            val editor = myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()

            editor?.putString("action_sort_recent", "true")
            editor?.putString("action_sort_ascending", "false")

            editor?.apply()

            if (getSongList != null){
                Collections.sort(getSongList, Songs.STATIFIED.dateComparator)
            }
            _mainscreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    fun getSongsFromPhone(): ArrayList<Songs>{
        var arrayList = ArrayList<Songs>()

        var contentResolver = myActivity?.contentResolver
        var songURI= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songURI, null, null,null ,null)
        if(songCursor!= null && songCursor.moveToFirst()){
            val songId= songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songArtist= songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songTitle= songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songData= songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex= songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()){
               var currentId= songCursor.getLong(songId)
                var currentTitle= songCursor.getString(songTitle)
                var currentArtist= songCursor.getString(songArtist)
                var currentData= songCursor.getString(songData)
                var currentDate= songCursor.getLong(dateIndex)

                arrayList.add(Songs(currentId,currentTitle,currentArtist,currentData,currentDate))
            }
        }

        return arrayList
    }

}// Required empty public constructor
