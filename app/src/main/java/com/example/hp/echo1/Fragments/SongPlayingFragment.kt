package com.example.hp.echo1.Fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.hp.echo1.R
import com.example.hp.echo1.Songs
import com.example.hp.echo1.currentSongHelper
import com.example.hp.echo1.database.EchoDatabase
import kotlinx.android.synthetic.main.fragment_song_playing.*
import org.w3c.dom.Text
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {

    object stat {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var prevImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var shuffleImageButton: ImageButton? = null
        var audiovisualization: AudioVisualization? = null
        var fab: ImageButton? = null
        var glview: GLAudioVisualizationView? = null
        var currentsonghelper: currentSongHelper? = null
        var currentPos: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        var favoriteContent: EchoDatabase? = null
        var nSensorManager : SensorManager?= null
        var mSensorListner: SensorEventListener?= null
        var MY_PREFS_NAME = "ShakeFeature"

        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = stat.mediaPlayer?.currentPosition
                stat.startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong() as Long - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as Long)))))

                Handler().postDelayed(this, 1000)


            }

        }
    }


        object Staticated {
            var MY_PREFS_SHUFFLE = "shuffle feature"
            var MY_PREFS_LOOP = "loop feature"
            fun onSongComplete() {
                if (stat.currentsonghelper?.isShuffle as Boolean) {
                    playNext("PlayNextLikeNormalShuffle")
                    stat.currentsonghelper?.isPlaying = true
                } else {
                    if (stat.currentsonghelper?.isLoop as Boolean) {
                        stat.currentsonghelper?.isPlaying = true
                        var nextSong = stat.fetchSongs?.get(stat.currentPos)

                        stat.currentsonghelper?.songTitle = nextSong?.songTitle
                        stat.currentsonghelper?.songPath = nextSong?.songData
                        stat.currentsonghelper?.currentPosition = stat.currentPos
                        stat.currentsonghelper?.songId = nextSong?.songId as Long

                        updateTextViews(stat.currentsonghelper?.songTitle as String, stat.currentsonghelper?.songArtist as String)

                        stat.mediaPlayer?.reset()
                        try {
                            stat.mediaPlayer?.setDataSource(stat.myActivity, Uri.parse(stat.currentsonghelper?.songPath))
                            stat.mediaPlayer?.prepare()
                            stat.mediaPlayer?.start()
                            processInfo(stat.mediaPlayer as MediaPlayer)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        playNext("PlayNextNormal")
                        stat.currentsonghelper?.isPlaying = true
                    }
                }
                if (stat.favoriteContent?.checkifIdExists(stat.currentsonghelper?.songId?.toInt() as Int) as Boolean) {
                    stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_on))
                } else {
                    stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_off))

                }

            }

            fun updateTextViews(songTitle:String, songArtist: String){
                var songTitleUpdated = songTitle
                var songArtistUpdated = songArtist
                if(songTitle.equals("<unknown>",true)){
                    songTitleUpdated = "unknown"

                }

                if(songArtist.equals("<unknown>",true)){
                    songArtistUpdated = "unknown"
                }
                stat.songTitleView?.setText(songTitleUpdated)
                stat.songArtistView?.setText(songArtistUpdated)
            }


            fun processInfo(mediaPlayer: MediaPlayer){
                var finalTime = mediaPlayer.duration
                var startTime= mediaPlayer.currentPosition
                stat.seekBar?.max= finalTime
                stat.startTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(startTime.toLong() as Long- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong() as Long)))))

                stat.endTimeText?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong() as Long- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong() as Long)))))
                stat.seekBar?.setProgress(startTime)
                Handler().postDelayed(stat.updateSongTime, 1000)

            }


            fun playNext(check: String) {

                if (check.equals("PlayNextNormal", true)) {
                    stat.currentPos = stat.currentPos + 1

                } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                    var randobj = Random()
                    var randpos = randobj.nextInt(stat.fetchSongs?.size?.plus(1) as Int)
                    stat.currentPos = randpos

                }
                if (stat.currentPos == stat.fetchSongs?.size) {
                    stat.currentPos = 0
                }
                stat.currentsonghelper?.isLoop = false
                var nextSong = stat.fetchSongs?.get(stat.currentPos)
                stat.currentsonghelper?.songTitle = nextSong?.songTitle
                stat.currentsonghelper?.songArtist = nextSong?.artist
                stat.currentsonghelper?.songPath = nextSong?.songData
                stat.currentsonghelper?.currentPosition = stat.currentPos
                stat.currentsonghelper?.songId = nextSong?.songId as Long

                updateTextViews(stat.currentsonghelper?.songTitle as String, stat.currentsonghelper?.songArtist as String)

                stat.mediaPlayer?.reset()
                try {
                    stat.mediaPlayer?.setDataSource(stat.myActivity, Uri.parse(stat.currentsonghelper?.songPath))
                    stat.mediaPlayer?.prepare()
                    stat.mediaPlayer?.start()
                    processInfo(stat.mediaPlayer as MediaPlayer)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (stat.favoriteContent?.checkifIdExists(stat.currentsonghelper?.songId?.toInt() as Int) as Boolean) {
                    stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_on))
                } else {
                    stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_off))

                }
            }

        }


    var mAcceleration : Float = 0f
    var mAccelerationCurrent : Float = 0f
    var mAccelerationLast: Float = 0f


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)

        stat.seekBar = view?.findViewById(R.id.seekBar)
        activity.title = "Now Playing"
        stat.startTimeText = view?.findViewById(R.id.startTime)
        stat.endTimeText = view?.findViewById(R.id.endTime)
        stat.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        stat.nextImageButton = view?.findViewById(R.id.nextButton)
        stat.prevImageButton = view?.findViewById(R.id.previousButton)
        stat.loopImageButton = view?.findViewById(R.id.loopButton)
        stat.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        stat.songArtistView = view?.findViewById(R.id.songArtist)
        stat.songTitleView = view?.findViewById(R.id.songTitle)
        stat.glview= view?.findViewById(R.id.visualizer_view)
        stat.fab= view?.findViewById(R.id.favIcon)
        stat.fab?.alpha = 0.8f
        return view


    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stat.audiovisualization= stat.glview as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        stat.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        stat.myActivity = activity as Activity
    }

    override fun onResume() {
        super.onResume()
        stat.audiovisualization?.onResume()
        stat.nSensorManager?.registerListener(stat.mSensorListner, stat.nSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        stat.audiovisualization?.onPause()
        stat.nSensorManager?.unregisterListener(stat.mSensorListner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stat.audiovisualization?.release()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stat.nSensorManager = stat.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationLast= SensorManager.GRAVITY_EARTH
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        bindShakeListner()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val item : MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible= true
        val item2 : MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible= false

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                stat.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        stat.favoriteContent = EchoDatabase(stat.myActivity)
        stat.currentsonghelper = currentSongHelper()
        stat.currentsonghelper?.isPlaying = true
        stat.currentsonghelper?.isLoop = false
        stat.currentsonghelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null

        var _songArtist: String? = null

        var _songId: Long = 0

        try {
            path = arguments.getString("path")
            _songTitle = arguments.getString("songTitle")
            _songArtist = arguments.getString("songArtist")
            _songId = arguments.getInt("songId").toLong()
            stat.currentPos = arguments.getInt("songPosition")
            stat.fetchSongs = arguments.getParcelableArrayList("songData")

            stat.currentsonghelper?.songPath = path
            stat.currentsonghelper?.songTitle = _songTitle
            stat.currentsonghelper?.songArtist = _songArtist
            stat.currentsonghelper?.songId = _songId
            stat.currentsonghelper?.currentPosition = stat.currentPos

            Staticated.updateTextViews(stat.currentsonghelper?.songTitle as String, stat.currentsonghelper?.songArtist as String)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments.get("FavBottomBar") as? String

        if (fromFavBottomBar != null){
            stat.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
        }else{ stat.mediaPlayer = MediaPlayer()
            stat.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                stat.mediaPlayer?.setDataSource(stat.myActivity, Uri.parse(path))
                stat.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            stat.mediaPlayer?.start()
        }

        Staticated.processInfo(stat.mediaPlayer as MediaPlayer)
        if (stat.currentsonghelper?.isPlaying as Boolean) {
            stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            stat.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        stat.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

        clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(stat.myActivity as Context, 0)
        stat.audiovisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            stat.currentsonghelper?.isShuffle = true
            stat.currentsonghelper?.isLoop = false
            stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            stat.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            stat.currentsonghelper?.isShuffle = false
            stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

        }

        var prefsForLoop = stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            stat.currentsonghelper?.isShuffle = false
            stat.currentsonghelper?.isLoop = true
            stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            stat.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            stat.currentsonghelper?.isLoop = false
            stat.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }

        if (stat.favoriteContent?.checkifIdExists(stat.currentsonghelper?.songId?.toInt() as Int) as Boolean) {
            stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_on))
        } else {
            stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_off))

        }
    }

    fun clickHandler() {

        stat.fab?.setOnClickListener({
            if (stat.favoriteContent?.checkifIdExists(stat.currentsonghelper?.songId?.toInt() as Int) as Boolean) {
                stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_off))
                stat.favoriteContent?.deleteFavorite(stat.currentsonghelper?.songId?.toInt() as Int)
                Toast.makeText(stat.myActivity, "Removed From Favorites", Toast.LENGTH_SHORT).show()
            } else {
                stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_on))
                stat.favoriteContent?.storeAsFavorite(stat.currentsonghelper?.songId?.toInt(), stat.currentsonghelper?.songArtist, stat.currentsonghelper?.songTitle, stat.currentsonghelper?.songPath)
                Toast.makeText(stat.myActivity, "Added to favorites", Toast.LENGTH_SHORT).show()

            }
        })

        stat.shuffleImageButton?.setOnClickListener({
            var editorShuffle = stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (stat.currentsonghelper?.isShuffle as Boolean) {
                stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                stat.currentsonghelper?.isShuffle = false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            } else {
                stat.currentsonghelper?.isShuffle = true
                stat.currentsonghelper?.isLoop = false
                stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                stat.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("featue", false)
                editorLoop?.apply()
            }

        })

        stat.nextImageButton?.setOnClickListener({
            stat.currentsonghelper?.isPlaying = true
            stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (stat.currentsonghelper?.isShuffle as Boolean) {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else
                Staticated.playNext("PlayNextNormal")

        })

        stat.prevImageButton?.setOnClickListener({
            stat.currentsonghelper?.isPlaying = true
            if (stat.currentsonghelper?.isLoop as Boolean)
                stat.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

            playPrevious()

        })

        stat.loopImageButton?.setOnClickListener({
            var editorShuffle= stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop=stat.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
            if (stat.currentsonghelper?.isLoop as Boolean) {
                stat.currentsonghelper?.isLoop = false
                stat.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                stat.currentsonghelper?.isLoop = true
                stat.currentsonghelper?.isShuffle = false
                stat.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                stat.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()

            }

        })
        stat.playPauseImageButton?.setOnClickListener({
            if (stat.mediaPlayer?.isPlaying as Boolean) {
                stat.mediaPlayer?.pause()
                stat.currentsonghelper?.isPlaying = false
                stat.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                stat.mediaPlayer?.start()
                stat.currentsonghelper?.isPlaying = true
                stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }



    fun playPrevious() {
        stat.currentPos = stat.currentPos - 1

        if (stat.currentPos == -1)
            stat.currentPos = 0

        if (stat.currentsonghelper?.isPlaying as Boolean)
            stat.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        else
            stat.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

        stat.currentsonghelper?.isLoop = false

        val nextSong = stat.fetchSongs?.get(stat.currentPos)
        stat.currentsonghelper?.songTitle = nextSong?.songTitle
        stat.currentsonghelper?.songArtist = nextSong?.artist
        stat.currentsonghelper?.songPath = nextSong?.songData
        stat.currentsonghelper?.currentPosition = stat.currentPos
        stat.currentsonghelper?.songId = nextSong?.songId as Long
        Staticated.updateTextViews(stat.currentsonghelper?.songTitle as String, stat.currentsonghelper?.songArtist as String)

        stat.mediaPlayer?.reset()
        try {
            stat.mediaPlayer?.setDataSource(activity, Uri.parse(stat.currentsonghelper?.songPath))
            stat.mediaPlayer?.prepare()
            stat.mediaPlayer?.start()
            Staticated.processInfo(stat.mediaPlayer as MediaPlayer)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (stat.favoriteContent?.checkifIdExists(stat.currentsonghelper?.songId?.toInt() as Int) as Boolean) {
            stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_on))
        } else {
            stat.fab?.setImageDrawable(ContextCompat.getDrawable(stat.myActivity, R.drawable.favorite_off))

        }

    }

    fun bindShakeListner(){
        stat.mSensorListner = object : SensorEventListener{
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z= p0.values[2]

                mAccelerationLast= mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x + y*y + z*z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if (mAcceleration> 12){
                    val prefs = stat.myActivity?.getSharedPreferences(stat.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature",false)

                    if (isAllowed as Boolean){

                        Staticated.playNext("PlayNextNormal")

                    }

                }

            }

        }
    }


}// Required empty public constructor
