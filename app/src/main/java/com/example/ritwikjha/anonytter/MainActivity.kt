package com.example.ritwikjha.anonytter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.support.v4.app.ActivityCompat
import android.view.*
import android.widget.BaseAdapter
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.tweets.*
import kotlinx.android.synthetic.main.tweets.view.*
import kotlinx.android.synthetic.main.tweetswithoutimage.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    var adapter:MyTweetsAdapter?=null

    private var database= FirebaseDatabase.getInstance()
    private var myRef= database.reference

    private var mAuth:FirebaseAuth?=null

    var listOfTweets=ArrayList<Tweet>()

    private var myEmail:String?=null
    private var myUID:String?=null

    var mprocessLike:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        if (!isUserLogin()){
            LogOut()
        }
        setContentView(R.layout.activity_main)

        myEmail=mAuth!!.currentUser.toString()
        myUID=mAuth!!.uid.toString()

        listOfTweets.add(Tweet("0","TEXT","ImageURL","add","05-01-17","0"))

        adapter=MyTweetsAdapter(this,listOfTweets)
        lvTweets.adapter=adapter

        LoadPosts()

        if (!uid.contains(myUID!!)){
            NotifyMe()
            uid.clear()
            uid.add(myUID!!)
        }else{
            Toast.makeText(applicationContext,"Post Added",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        if (!isUserLogin()){
            LogOut()
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!=null){
            when(item.itemId){
                R.id.settings->{
                    Settings()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    var uid=ArrayList<String>()

    inner class MyTweetsAdapter:BaseAdapter{

        private var listOfTweetAdapter=ArrayList<Tweet>()
        var context:Context?=null

        constructor(context:Context,listOfTweetAdapter:ArrayList<Tweet>){
            this.context=context
            this.listOfTweetAdapter=listOfTweetAdapter
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myTweet=listOfTweetAdapter[position]
            var inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            uid.add(myTweet.tweetPersonalUID.toString())

            if (myTweet.tweetPersonalUID.equals("add")){

                var myView=inflater.inflate(R.layout.add_ticket,null)

                myView.ivAttach.setOnClickListener(View.OnClickListener {
                    CheckPermission()
                })

                myView.ivPostButton.setOnClickListener(View.OnClickListener {

                    val df=SimpleDateFormat("dd-MM-yy")
                    val dateObj=Date()
                    val date=df.format(dateObj).toString()

                    var likes="0"

                    myRef.child("Posts").push().setValue(PostInfo(myUID!!,
                            myView.etPost.text.toString(),
                            DownloadUrl!!,
                            date,
                            likes))

                    myView.etPost.setText("")
                    DownloadUrl=""

                })

                return myView
            }else if (myTweet.tweetImgURL==""){

                var myView=inflater.inflate(R.layout.tweetswithoutimage,null)
                myView.tvTweetPost_woi.text=myTweet.tweetText
                myView.tvDate_woi.text=myTweet.tweetDate

                myView.ivCommentShow_woi.setOnClickListener {
                    Toast.makeText(applicationContext,"Not Implemented Yet \n Will be Implemented in Updates",Toast.LENGTH_SHORT).show()
                }

                myRef.child("Likes")
                        .addValueEventListener(object:ValueEventListener{

                            override fun onDataChange(p0: DataSnapshot?) {

                                if (p0!!.hasChild(myTweet.tweetText)) {

                                    if (p0!!.child(myTweet.tweetText).hasChild(mAuth!!.currentUser!!.uid)) {

                                        myView.ivFav_woi.setImageResource(R.drawable.star_on)


                                    } else {

                                        myView.ivFav_woi.setImageResource(R.drawable.star_off)

                                    }

                                }
                            }

                            override fun onCancelled(p0: DatabaseError?) {

                            }
                        })

                myView.ivFav_woi.setOnClickListener {

                    mprocessLike=true

                    myRef.child("Likes")
                            .addValueEventListener(object : ValueEventListener {

                                override fun onDataChange(p0: DataSnapshot?) {

                                    if (mprocessLike) {

                                            if (p0!!.child(myTweet.tweetText).hasChild(mAuth!!.currentUser!!.uid)) {
                                                myRef.child("Likes").child(myTweet.tweetText).child(mAuth!!.currentUser!!.uid).removeValue()
                                                myView.ivFav_woi.setImageResource(R.drawable.star_off)
                                                mprocessLike = false

                                            } else {
                                                myRef.child("Likes").child(myTweet.tweetText).child(mAuth!!.currentUser!!.uid).setValue(SplitString(mAuth!!.currentUser!!.email.toString()))
                                                myView.ivFav_woi.setImageResource(R.drawable.star_on)
                                                mprocessLike = false
                                            }

                                    }
                                }

                                override fun onCancelled(p0: DatabaseError?) {
                                }

                            })
                }


                return myView
            }else{

                var myView=inflater.inflate(R.layout.tweets,null)
                myView.tvTweetPost.text=myTweet.tweetText
                myView.tvDate.text=myTweet.tweetDate
                myView.ivCommentShow.setOnClickListener {
                    Toast.makeText(applicationContext,"Not Implemented Yet \n" +
                            " Will be Implemented in Updates",Toast.LENGTH_SHORT).show()
                }

                myRef.child("Likes")
                        .addValueEventListener(object:ValueEventListener{

                            override fun onDataChange(p0: DataSnapshot?) {

                                if (p0!!.hasChild(myTweet.tweetText)) {

                                    if (p0!!.child(myTweet.tweetText).hasChild(mAuth!!.currentUser!!.uid)) {

                                        myView.ivFav.setImageResource(R.drawable.star_on)

                                    } else {

                                        myView.ivFav.setImageResource(R.drawable.star_off)

                                    }

                                }
                            }

                            override fun onCancelled(p0: DatabaseError?) {

                            }
                        })
                myView.ivFav.setOnClickListener {
                    mprocessLike=true


                        myRef.child("Likes")
                                .addValueEventListener(object : ValueEventListener {

                                    override fun onDataChange(p0: DataSnapshot?) {


                                        if (mprocessLike) {


                                                if (p0!!.child(myTweet.tweetText).hasChild(mAuth!!.currentUser!!.uid)) {
                                                    myRef.child("Likes").child(myTweet.tweetText).child(mAuth!!.currentUser!!.uid).removeValue()
                                                    myView.ivFav.setImageResource(R.drawable.star_off)

                                                    mprocessLike = false
                                                } else {
                                                    myRef.child("Likes").child(myTweet.tweetText).child(mAuth!!.currentUser!!.uid).setValue(SplitString(mAuth!!.currentUser!!.email.toString()))
                                                    myView.ivFav.setImageResource(R.drawable.star_on)
                                                    mprocessLike = false
                                                }
                                        }
                                    }

                                        override fun onCancelled(p0: DatabaseError?) {
                                        }

                                })
                }


                if (myTweet.tweetImgURL!=""){
                    Picasso.with(context).load(myTweet.tweetImgURL).into(myView.ivPost)

                }


                return myView
            }

        }

        override fun getItem(position: Int): Any {
            return listOfTweetAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listOfTweetAdapter.size
        }
    }



    var READIMAGE:Int=253

    fun CheckPermission(){
        if (Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

                return
            }
        }
        LoadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            READIMAGE->{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    LoadImage()
                }else{
                    Toast.makeText(applicationContext,"Can't Grant Permission",Toast.LENGTH_SHORT).show()
                }
            }
            else->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    var PICK_IMAGE_CODE=123

    fun LoadImage(){
        var photoGallery=Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(photoGallery,PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==PICK_IMAGE_CODE && data!=null && resultCode== Activity.RESULT_OK){

            val selectedImage=data.data
            val filePathCol= arrayOf(MediaStore.Images.Media.DATA)
            var cursor=contentResolver.query(selectedImage,filePathCol,null,null,null)
            cursor.moveToFirst()
            val colIndex=cursor.getColumnIndex(filePathCol[0])
            val picturePath=cursor.getString(colIndex)
            cursor.close()

            UploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }

    var DownloadUrl:String=""

    fun UploadImage(bitmap: Bitmap){

        val storage=FirebaseStorage.getInstance()
        val storageRef=storage.getReferenceFromUrl("gs://anonytter-82dfd.appspot.com")
        val df=SimpleDateFormat("ddMMyyHHmmss")
        val dateObj=Date()
        val imgPath=SplitString(myEmail!!)+"."+ df.format(dateObj)+ ".jpg"
        val imgRef=storageRef.child("PostImage/"+imgPath)
        var baos=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data=baos.toByteArray()
        val uploadTask=imgRef.putBytes(data)

        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"Uploading Image Failed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            DownloadUrl=taskSnapshot.downloadUrl!!.toString()
        }
    }

    fun SplitString(str:String):String{
        var split=str.split("@")
        return split[0]
    }


    fun LoadPosts(){
        myRef.child("Posts")
                .addValueEventListener(object:ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot?) {

                        try {

                            NotifyMe()

                            listOfTweets.clear()
                            listOfTweets.add(Tweet("0","TEXT","ImageURL","add","05-01-17","0"))

                            var td= p0!!.value as HashMap<String,Any>


                            for (key in td.keys){
                                var post=td[key] as HashMap<String,Any>


                                listOfTweets.add(Tweet(key,
                                        post["txt"] as String,
                                        post["postImg"] as String,
                                        post["userUID"] as String,
                                        post["date"] as String,
                                        post["likes"] as String))

                                adapter!!.notifyDataSetChanged()
                            }

                        }catch(ex:Exception){}
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
    }

    fun Settings(){
        var set=Intent(this,Settings::class.java)
        startActivity(set)
    }

    fun NotifyMe(){
        myRef.child("Posts")
                .addChildEventListener(object :ChildEventListener {

                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(applicationContext,"Plese Login Again",Toast.LENGTH_SHORT).show()
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {

                        try {
                            var notify=Notifications()
                            notify.Notify(applicationContext,"Anonytter has a new post")

                        }catch(ex:Exception){}
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                          Toast.makeText(applicationContext,"A post has been deleted",Toast.LENGTH_SHORT).show()
                    }

                })
    }

    fun isUserLogin():Boolean{
        if (mAuth!!.currentUser!=null){
            return true
        }
        return false
    }

    fun SignOut(){
        var signout= Intent(this,loginActivity::class.java)
        startActivity(signout)
        finish()
    }

    fun LogOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){

                        SignOut()

                    }
                }
    }

}
