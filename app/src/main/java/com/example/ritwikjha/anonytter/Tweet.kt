package com.example.ritwikjha.anonytter

class Tweet {
    var tweetID:String?=null
    var tweetText:String?=null
    var tweetImgURL:String?=null
    var tweetPersonalUID:String?=null
    var tweetDate:String?=null
    var tweetLikes:String?=null

    constructor(tweetID:String,tweetText:String,tweetImgURL:String,tweetPersonalUID:String, tweetDate:String,tweetLikes:String){
        this.tweetID=tweetID
        this.tweetText=tweetText
        this.tweetImgURL=tweetImgURL
        this.tweetPersonalUID=tweetPersonalUID
        this.tweetDate=tweetDate
        this.tweetLikes=tweetLikes
    }
}
