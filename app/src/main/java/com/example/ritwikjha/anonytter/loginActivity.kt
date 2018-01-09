package com.example.ritwikjha.anonytter

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import java.util.Arrays.asList



class loginActivity : AppCompatActivity() {

    var PATH_TOS=""
    var RC_SIGN_IN = 200

    var mAuth:FirebaseAuth?=null

    var providers = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        if (isUserLogin()){
            LoginUser()
        }
        setContentView(R.layout.activity_login)

        tvInfo.text="Welcome to the ANONYMOUS community."
        tvAd.text= """1.Express yourself without fear.
2.Take political debate to the highest level of discussion
3.Generate unbiased advice
4.Provide entertainment via gossip and humor
5.Become a source for breaking news"""

        buLogin.setOnClickListener(View.OnClickListener {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setTosUrl(PATH_TOS)
                    .setAvailableProviders(providers)
                    .build(),RC_SIGN_IN)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RC_SIGN_IN){
            if (resultCode== Activity.RESULT_OK){
                LoginUser()
            }
            if (resultCode== Activity.RESULT_CANCELED){
                DisplayMessage(getString(R.string.signin_failed))
            }
            return
        }
        DisplayMessage(getString(R.string.unknown_response))
    }

    fun isUserLogin():Boolean{
        if (mAuth!!.currentUser!=null){
            return true
        }
        return false
    }

    fun LoginUser(){
        var intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun DisplayMessage(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT)
    }
}
