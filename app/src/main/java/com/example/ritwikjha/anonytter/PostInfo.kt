package com.example.ritwikjha.anonytter

class PostInfo{
    var UserUID:String?=null
    var txt:String?=null
    var postImg:String?=null
    var date:String?=null
    var likes:String?=null

    constructor(UserUID:String,txt:String, postImg:String, date:String,likes:String){
        this.UserUID=UserUID
        this.txt=txt
        this.postImg=postImg
        this.date=date
        this.likes=likes
    }
}