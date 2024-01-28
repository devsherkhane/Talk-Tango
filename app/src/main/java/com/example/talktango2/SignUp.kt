package com.example.talktango2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnSendVerification: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        edtEmail = findViewById(R.id.email)
        edtName = findViewById(R.id.name)
        edtPassword = findViewById(R.id.pass)
        btnSignup = findViewById(R.id.signupBtn)
        btnSendVerification = findViewById(R.id.sendVerificationBtn)

        btnSignup.visibility = View.GONE // Initially hide the SignUp button

        btnSignup.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Perform the SignUp action
            sendVerificationEmail(name, email, password)
        }

        btnSendVerification.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Send verification email and make the SignUp button visible after successful email sent
            sendVerificationEmail(name, email, password)
        }
    }

    private fun sendVerificationEmail(name: String, email: String, password: String) {
        btnSignup.visibility = View.GONE

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this@SignUp,
                                    "Verification email sent successfully. Press Sign Up to complete the registration.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnSignup.visibility = View.VISIBLE // Make the SignUp button visible
                            } else {
                                Toast.makeText(
                                    this@SignUp,
                                    "Email verification failed: ${verificationTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // If verification fails, show the Sign Up button again
                                btnSignup.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // If authentication fails, show the Sign Up button again
                    btnSignup.visibility = View.GONE
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef.child("users").child(uid).setValue(User(name, email, uid))
    }
}
