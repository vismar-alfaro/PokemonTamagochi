package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var eggsRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.database

        auth = Firebase.auth

        binding.tvRegister.setOnClickListener {
            checkEggs()
        }

        binding.btnLogin.setOnClickListener {
            performLogin()
        }


    }

    private fun checkEggs() {
        eggsRef = database.getReference("eggs")
        val query = eggsRef.orderByChild("userId")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var children = snapshot.children.toList()
                    var count = 0

                    for (child in children) {
                        val userIdValue = child.child("userId").value.toString()
                        if (userIdValue == "") {
                            count = 0
                            goToRegister()
                        } else {
                            count += 1
                            if (count == 10) {
                                toast()
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun goToRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)

    }

    fun toast(){
        Toast.makeText(this,"Sorry, No eggs available at the moment",Toast.LENGTH_LONG).show()
    }

    private fun performLogin() {
        val email: EditText = findViewById(R.id.et_email)
        val password: EditText = findViewById(R.id.et_password)

        if (email.text.isEmpty() || password.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val emailInput = email.text.toString()
        val passwordInput = password.text.toString()

        auth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(
                        baseContext, "Authentication Failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    baseContext, "Authentication Failed. ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

}