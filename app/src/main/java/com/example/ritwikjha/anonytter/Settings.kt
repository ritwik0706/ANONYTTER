package com.example.ritwikjha.anonytter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        ivDelete.setOnClickListener {
            DeleteCurrentUser()
        }
        tvDelete.setOnClickListener {
            DeleteCurrentUser()
        }

        ivLogout.setOnClickListener {
            LogOut()
        }

        tvLogout.setOnClickListener {
            LogOut()
        }
        ivAbout.setOnClickListener {
            GotoAbout()
        }
        tvAbout.setOnClickListener{
            GotoAbout()
        }

    }

    fun LogOut(){

        var simpleAlert=AlertDialog.Builder(this).create()
        simpleAlert.setTitle("LOGOUT")
        simpleAlert.setMessage("Are you sure you want to Logout?")
        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",{
            dialogInterface, i->
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful){

                            SignOut()

                        }
                    }
        })
        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",{
            dialogInterface, i->
        })
        simpleAlert.show()


    }

    fun DeleteCurrentUser(){
        var simpleAlert=AlertDialog.Builder(this).create()
        simpleAlert.setTitle("DELETE")
        simpleAlert.setMessage("Are you sure you want to delete this account?")
        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",{
            dialogInterface, i->
            AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){

                            SignOut()

                        }else{

                            Toast.makeText(applicationContext,"Can't Delete the Account", Toast.LENGTH_SHORT).show()

                        }
                    }
        })
        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",{
            dialogInterface, i->
        })
        simpleAlert.show()

    }

    fun SignOut(){
        var signout= Intent(this,loginActivity::class.java)
        startActivity(signout)
        finish()
    }

    fun GotoAbout(){
        var goto=Intent(this,AboutActivity::class.java)
        startActivity(goto)
    }

}
