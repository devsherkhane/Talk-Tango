package com.example.talktango2

class Message {
    var message: String? = null
    var senderId: String? = null
    var senderName: String? = null
    var timestamp: Long = System.currentTimeMillis()

    constructor(){}

    constructor(message: String?, senderId: String?) {
        this.message = message
        this.senderId = senderId
        this.timestamp = System.currentTimeMillis()
    }
    
    constructor(message: String?, senderId: String?, senderName: String?) {
        this.message = message
        this.senderId = senderId
        this.senderName = senderName
        this.timestamp = System.currentTimeMillis()
    }
}
