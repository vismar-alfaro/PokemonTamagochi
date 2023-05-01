package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityRegisterBinding
import ph.kodego.alfaro.vismarjay.pokemotamagochi.model.User
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private val storage = FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        database = Firebase.database
        userRef = database.getReference("users")
        auth = Firebase.auth



        binding.btnUploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }


        binding.btnRegister.setOnClickListener {
            performSignUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            binding.userImageView.setImageBitmap(bitmap)
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storage.child("images/${UUID.randomUUID()}")

            val uploadTask = ref.putFile(filePath!!)

            uploadTask.addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()

                ref.downloadUrl.addOnSuccessListener { uri ->
                    auth.currentUser?.let { it1 -> userRef.child(it1.uid).child("profile").setValue(uri.toString()) }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match, please try again"
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { userRef.child(it.uid).child("firstName").setValue(firstName) }
                    auth.currentUser?.let { userRef.child(it.uid).child("lastName").setValue(lastName) }
                    auth.currentUser?.let { userRef.child(it.uid).child("id").setValue(it.uid) }
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this, "Authentication failed: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        uploadImage()
    }
}
