package com.example.hp.echo1.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hp.echo1.Fragments.SongPlayingFragment
import com.example.hp.echo1.R
import com.example.hp.echo1.Songs

/**
 * Created by maniraj on 26/12/17.
 */
class FavoriteAdapter(_songDetails: ArrayList<Songs>, _context: Context): RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>(){
    var songDetails: ArrayList<Songs>?=null
    var mcontext: Context? = null

    init {
        this.songDetails=_songDetails
        this.mcontext=_context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObj= songDetails?.get(position)
        holder.trackTitle?.text = songObj?.songTitle
        holder.trackArtist?.text= songObj?.artist
        holder.contentHolder?.setOnClickListener({
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString("songArtist", songObj?.artist)
            args.putString("path", songObj?.songData)
            args.putString("songTitle", songObj?.songTitle)
            args.putInt("songId", songObj?.songId?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)
            songPlayingFragment.arguments = args
            (mcontext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, songPlayingFragment)
                    .addToBackStack("SongPlayingFragmentFavorite")

                    .commit()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder{
        val itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainscreen_adapter,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails== null)
            return 0
        else
            return (songDetails as ArrayList<Songs>).size

    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackTitle: TextView? = null
        var trackArtist: TextView?= null
        var contentHolder: RelativeLayout?= null

        init {
            trackTitle= view.findViewById<TextView>(R.id.track_title)
            trackArtist= view.findViewById<TextView>(R.id.track_artist)
            contentHolder=view.findViewById<RelativeLayout>(R.id.content_row)
        }
    }
}