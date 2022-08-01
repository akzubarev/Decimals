package com.education4all.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.education4all.R
import com.education4all.firebase.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var repeatPasswordEditText: EditText? = null
    private var toggleLoginSignUpTextView: TextView? = null
    private var loginSignUpButton: Button? = null
    private var signUpModeActive = false
    private var database: FirebaseDatabase? = null
    private var usersDatabaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersDatabaseReference = database!!.reference.child("users")
        if (auth!!.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
            toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView)
            loginSignUpButton = findViewById(R.id.loginSignUpButton)
            toggleLoginSignUpTextView.setOnClickListener(View.OnClickListener { view: View? ->
                toggleLoginMode(
                    view
                )
            })
            signUpModeActive = false
            setModeUI()
        }
    }

    fun signUp(v: View?) {
        loginSignUpUser(
            emailEditText!!.text.toString().trim { it <= ' ' },
            passwordEditText!!.text.toString().trim { it <= ' ' })
    }

    private fun checkInput(): Boolean {
        if (passwordEditText!!.text.toString().trim { it <= ' ' }.length < 7) {
            Toast.makeText(
                this,
                "Password must be at least 7 characters",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (emailEditText!!.text.toString().trim { it <= ' ' } == "") {
            Toast.makeText(
                this,
                "Please input your email",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (signUpModeActive && passwordEditText!!.text.toString()
                .trim { it <= ' ' } != repeatPasswordEditText!!.text.toString().trim { it <= ' ' }
        ) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loginSignUpUser(email: String, password: String) {
        if (checkInput()) {
            if (signUpModeActive) auth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        val user = auth!!.currentUser
                        user?.let { createUser(it) }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        val user = auth!!.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun createUser(firebaseUser: FirebaseUser) {
        val user = User()
        user.id = firebaseUser.uid
        user.email = firebaseUser.email
        usersDatabaseReference!!.child(firebaseUser.uid).setValue(user)
    }

    fun toggleLoginMode(view: View?) {
        signUpModeActive = !signUpModeActive
        setModeUI()
    }

    private fun setModeUI() {
        if (signUpModeActive) {
            loginSignUpButton!!.text = "Создать аккаунт"
            toggleLoginSignUpTextView!!.text = "Нажмите чтобы войти"
            repeatPasswordEditText!!.visibility = View.VISIBLE
        } else {
            loginSignUpButton!!.text = "Войти"
            toggleLoginSignUpTextView!!.text = "Нажмите чтобы создать аккаунт"
            repeatPasswordEditText!!.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAndRemoveTask() else finishAffinity()
    }
}