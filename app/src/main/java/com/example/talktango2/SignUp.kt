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

        btnSendVerification.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Send verification email
            sendVerificationEmail(name, email, password)
        }
    }

    private fun sendVerificationEmail(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this@SignUp,
                                    "Verification email sent successfully. Please check your email and verify.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Set an OnClickListener to make SignUp button visible after email verification
                                btnSignup.visibility = View.VISIBLE

                                // Add an OnClickListener to handle SignUp button click
                                btnSignup.setOnClickListener {
                                    // Add user to the database
                                    addUserToDatabase(name, email, user?.uid ?: "")

                                    // Redirect to MainActivity
                                    startActivity(Intent(this@SignUp, MainActivity::class.java))
                                    finish() // Optional: Finish SignUp activity if you don't want to come back to it
                                }
                            } else {
                                Toast.makeText(
                                    this@SignUp,
                                    "Email verification failed: ${verificationTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Show an error message
                                btnSignup.visibility = View.GONE // Hide SignUp button on verification failure
                            }
                        }
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Show an error message
                    btnSignup.visibility = View.GONE // Hide SignUp button on authentication failure
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef.child("users").child(uid).setValue(User(name, email, uid))
            .addOnCompleteListener { databaseTask ->
                if (databaseTask.isSuccessful) {
                    Toast.makeText(
                        this@SignUp,
                        "User added to the database successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Error adding user to the database: ${databaseTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}





