package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var eggsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "This app is develop and made by Vismar Jay Alfaro", Toast.LENGTH_LONG)
            .show()

        FirebaseApp.initializeApp(this)
        database = Firebase.database
        eggsRef = database.getReference("eggs")
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val query = eggsRef.orderByChild("userId")
        val userData = query.equalTo(currentUser?.uid)

        if (currentUser != null) {
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get a random child
                        var children = snapshot.children.toList()

                        for (child in children){
                        val randomChild = children.random()

                        // Check if userId is null
                        val userIdValue = randomChild.child("userId").value.toString()
                        if (userIdValue == "") {
                            // Set the userId value
                            randomChild.ref.child("userId").setValue(currentUser.uid)
                            binding.generateEgg.visibility = View.VISIBLE

                            break

                        } else{
                            binding.generateEgg.visibility = View.INVISIBLE
                        }}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors here
                }
            })
        }

        binding.generateEgg.setOnClickListener {
            userData.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val parentValue = snapshot.key // retrieves the parent node value
                        // do something with the parent value
                        val userEgg = eggsRef.child(parentValue!!).child("imageUrl")
                        userEgg.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val imageURL = dataSnapshot.getValue(String::class.java)

                                Picasso.get().load(imageURL).into(binding.eggImage)

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Error loading egg image", error.toException())
                            }
                        })
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
            binding.generateEgg.visibility = View.GONE
            binding.hatchEgg.visibility = View.VISIBLE
        }

        binding.hatchEgg.setOnClickListener {
            userData.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val parentValue = snapshot.key // retrieves the parent node value
                        // do something with the parent value
                        val monsterParent = eggsRef.child(parentValue!!).child("monster")
                        val userMonster =
                            eggsRef.child(parentValue!!).child("monster").child("imageUrl")
                        val monsterName =
                            eggsRef.child(parentValue!!).child("monster").child("name")

                        userMonster.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val imageURL = dataSnapshot.getValue(String::class.java)

                                Picasso.get().load(imageURL).into(binding.eggImage)

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Error loading egg image", error.toException())
                            }
                        })

                        monsterName.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val name = dataSnapshot.getValue(String::class.java)
                                binding.monsterName.text = name

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Error loading monster name", error.toException())
                            }
                        })

                        binding.hatchEgg.visibility = View.INVISIBLE
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
            binding.goToHome.visibility = View.VISIBLE
        }

        binding.goToHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        finish()
    }

    fun toast(){
        Toast.makeText(this,"No eggs are available",Toast.LENGTH_SHORT).show()
    }


}

