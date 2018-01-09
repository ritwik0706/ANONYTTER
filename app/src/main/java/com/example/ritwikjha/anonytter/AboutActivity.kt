package com.example.ritwikjha.anonytter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.about_ticket.view.*
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    private var listofAbouts=ArrayList<AboutInfo>()
    var adapter:AboutAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        listofAbouts.add(0,AboutInfo("UPDATE","Check for Updates"))
        listofAbouts.add(1,AboutInfo("PERMISSIONS","Internet and External Storage"))
        listofAbouts.add(2,AboutInfo("VERSION","Anonytter v1.0.0"))
        listofAbouts.add(3,AboutInfo("DEVELOPER","RITWIK JHA"))

        adapter= AboutAdapter(this,listofAbouts)
        lvAbout.adapter=adapter

    }

    inner class AboutAdapter:BaseAdapter{

        var context:Context?=null
        var listofAbout=ArrayList<AboutInfo>()

        constructor(context:Context,listofAbout:ArrayList<AboutInfo>){

            this.context=context
            this.listofAbout=listofAbout

        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myAbout=listofAbout[position]

            var inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView=inflater.inflate(R.layout.about_ticket,null)

            myView.tvTitle.text=myAbout.title
            myView.tvInfo.text=myAbout.info

            if (myAbout==listofAbout[0]){
                myView.tvTitle.setOnClickListener(View.OnClickListener {
                    UpdateDialog()
                })
                myView.tvInfo.setOnClickListener(View.OnClickListener {
                    UpdateDialog()
                })
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listofAbout[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listofAbout.size
        }

    }

    fun UpdateDialog(){
         AlertDialog.Builder(this).create().apply {
             setTitle("UPDATE")
             setMessage("No Updates Available Now.")
             setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
                dialogInterface, i ->

            })
        }.show()
    }
}
