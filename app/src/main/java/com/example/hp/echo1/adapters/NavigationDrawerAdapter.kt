package com.example.hp.echo1.adapters

import android.content.Context
import android.support.v7.view.menu.MenuView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hp.echo1.Fragments.AboutUsFragment
import com.example.hp.echo1.Fragments.FavoriteFragment
import com.example.hp.echo1.Fragments.MainScreenFragment
import com.example.hp.echo1.Fragments.SettingsFragment
import com.example.hp.echo1.R
import com.example.hp.echo1.activity.MainActivity

/**
 * Created by maniraj on 15/12/17.
 */
class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray,_context: Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){

    var contentList: ArrayList<String>?= null
    var getImages: IntArray?= null
    var mcontext: Context?=null

    init {
        this.contentList= _contentList
        this.getImages = _getImages
        this.mcontext = _context
    }
    override fun onBindViewHolder(holder: NavViewHolder?, position: Int) {

        holder?.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_GET?.setText(contentList?.get(position))
        holder?.contentHolder?.setOnClickListener({
            if(position== 0){
                val mainScreenFragment = MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, mainScreenFragment)
                        .commit()
            }else if (position==1){
                val favFragment = FavoriteFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,favFragment)
                        .commit()

            }else if (position==2){
                val settingFragment = SettingsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, settingFragment)
                        .commit()
            }else {
                val aboutFragment = AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment, aboutFragment)
                        .commit()
            }

            MainActivity.Statified.drawerlayout?.closeDrawers()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavViewHolder {
      var itemView=  LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_navigationdrawer, parent,false)

        val returnThis = NavViewHolder(itemView)

        return returnThis
    }

    override fun getItemCount(): Int {
        return contentList?.size as Int
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        var icon_GET: ImageView? = null
        var text_GET: TextView? = null
        var contentHolder: RelativeLayout?= null


        init {
            icon_GET= itemView?.findViewById(R.id.icon_navdrawer)
            text_GET= itemView?.findViewById(R.id.text_navdrawer)
            contentHolder= itemView?.findViewById(R.id.nav_item_content_holder)
        }
    }

}