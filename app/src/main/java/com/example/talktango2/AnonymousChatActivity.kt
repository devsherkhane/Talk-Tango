package com.example.talktango2

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class AnonymousChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var anonymousId: String
    private lateinit var anonymousName: String
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.title = "Anonymous Chat"
        
        // Get or generate anonymous ID
        anonymousId = getAnonymousId()
        anonymousName = getAnonymousName()
        roomId = intent.getStringExtra("roomId") ?: "global_anonymous_chat"

        mDbRef = FirebaseDatabase.getInstance().getReference()

        chatRecyclerView = findViewById(R.id.chatRecyclerview)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // Load messages from the anonymous chat room
        mDbRef.child("anonymous_chats").child(roomId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                    
                    // Scroll to the bottom
                    if (messageList.size > 0) {
                        chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AnonymousChatActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            })

        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString()
            if (messageText.isNotEmpty()) {
                val messageObject = Message(messageText, anonymousId, anonymousName)

                mDbRef.child("anonymous_chats").child(roomId).child("messages").push()
                    .setValue(messageObject)

                messageBox.setText("")
            }
        }
    }
    
    private fun getAnonymousId(): String {
        // Check if we already have an anonymous ID in shared preferences
        val sharedPref = getSharedPreferences("TalkTangoPrefs", MODE_PRIVATE)
        var id = sharedPref.getString("anonymousId", null)
        
        // If no ID exists, create one and save it
        if (id == null) {
            id = UUID.randomUUID().toString()
            with(sharedPref.edit()) {
                putString("anonymousId", id)
                apply()
            }
        }
        
        return id
    }
    
    private fun getAnonymousName(): String {
        // Check if we already have an anonymous name in shared preferences
        val sharedPref = getSharedPreferences("TalkTangoPrefs", MODE_PRIVATE)
        var name = sharedPref.getString("anonymousName", null)
        
        // If no name exists, create one and save it
        if (name == null) {
            name = "Anon" + Random().nextInt(10000)
            with(sharedPref.edit()) {
                putString("anonymousName", name)
                apply()
            }
        }
        
        return name
    }
} 
