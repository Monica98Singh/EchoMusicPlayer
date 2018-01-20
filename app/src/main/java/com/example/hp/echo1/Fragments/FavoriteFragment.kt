package com.example.hp.echo1.Fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hp.echo1.R
import com.example.hp.echo1.Songs
import com.example.hp.echo1.adapters.FavoriteAdapter
import com.example.hp.echo1.database.EchoDatabase


/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {
    var myActivity : Activity?= null


    var noFavorite: TextView?= null
    var NowPlayingButtonBar: RelativeLayout?=null
    var playPauseButton: ImageButton?=null
    var songTitle : TextView?=null
    var recyclerView: RecyclerView?=null
    var trackPosition : Int = 0
    var favoriteContent : EchoDatabase? = null
    var refreshList: ArrayList<Songs>?= null
    var getListFromDatabase : ArrayList<Songs>? = null

    object Statified {
        var mediaPlayer : MediaPlayer?= null
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view=inflater!!.inflate(R.layout.fragment_favorite, container, false)
        activity.title= "Favorites"
        noFavorite= view?.findViewById(R.id.noFav)
        playPauseButton= view?.findViewById(R.id.play_pause_button)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        NowPlayingButtonBar= view?.findViewById(R.id.hiddenBarFavScreen)
        recyclerView= view?.findViewById(R.id.favoriteRecycler)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity= context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDatabase(myActivity)
        display_favorites_by_searching()
        buttomBarSetUp()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
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

    fun buttomBarSetUp(){
        try {
            BottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.stat.currentsonghelper?.songTitle)
            SongPlayingFragment.stat.mediaPlayer?.setOnCompletionListener{
                songTitle?.setText(SongPlayingFragment.stat.currentsonghelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
                if (SongPlayingFragment.stat.mediaPlayer?.isPlaying as Boolean){
                    NowPlayingButtonBar?.visibility = View.VISIBLE
                }else{
                    NowPlayingButtonBar?.visibility= View.INVISIBLE
                }
            }
            }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun BottomBarClickHandler(){
        NowPlayingButtonBar?.setOnClickListener({
            Statified.mediaPlayer = SongPlayingFragment.stat.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", SongPlayingFragment.stat.currentsonghelper?.songArtist)
            args.putString("path", SongPlayingFragment.stat.currentsonghelper?.songPath)
            args.putString("songTitle", SongPlayingFragment.stat.currentsonghelper?.songTitle)
            args.putInt("songId", SongPlayingFragment.stat.currentsonghelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.stat.currentsonghelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData", SongPlayingFragment.stat.fetchSongs)
            args.putString("FavBottomBar", "success")

           fragmentManager.beginTransaction()
                   .replace(R.id.details_fragment, songPlayingFragment)
                   .addToBackStack("SongPlayingFragment")
                   .commit()
        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.stat.mediaPlayer?.isPlaying as Boolean){
                SongPlayingFragment.stat.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.stat.mediaPlayer?.getCurrentPosition() as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }else{
                SongPlayingFragment.stat.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.stat.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun display_favorites_by_searching(){
      if(favoriteContent?.checkSize() as Int > 0){
          refreshList = ArrayList<Songs>()
          getListFromDatabase = favoriteContent?.queryDBlist()
          var fetchListFromDevice = getSongsFromPhone()
          if(fetchListFromDevice != null){
              for (i in 0..fetchListFromDevice?.size-1){
                  for (j in 0..getListFromDatabase?.size as Int -1){
                      if ((getListFromDatabase?.get(j)?.songId) === (fetchListFromDevice?.get(i)?.songId)){
                          refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
                      }
                  }
              }
          }else{

          }

          if (refreshList==null){
              recyclerView?.visibility = View.INVISIBLE
              noFavorite?.visibility = View.VISIBLE

          }else{
              var favadapter = FavoriteAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
              val mLayoutManager = LinearLayoutManager(activity)
              recyclerView?.layoutManager = mLayoutManager
              recyclerView?.itemAnimator = DefaultItemAnimator()
              recyclerView?.adapter = favadapter
              recyclerView?.setHasFixedSize(true)
          }


      }else{
          recyclerView?.visibility = View.INVISIBLE
          noFavorite?.visibility = View.VISIBLE
      }


    }
}// Required empty public constructor
