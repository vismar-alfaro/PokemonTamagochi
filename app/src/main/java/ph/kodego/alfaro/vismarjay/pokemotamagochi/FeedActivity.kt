package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityFeedBinding
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityMainBinding
import java.util.*


class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var eggsRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        database = Firebase.database
        eggsRef = database.getReference("eggs")
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val query = eggsRef.orderByChild("userId").equalTo(currentUser?.uid)

        val userRef = database.getReference("users")
        if (currentUser != null) {
            userRef.child(currentUser.uid).push()
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val monster = snapshot.child("monster")
                    val imageURL = monster.child("imageUrl").getValue(String::class.java)
                    val name = monster.child("name").getValue(String::class.java)
                    var exp = monster.child("experience").getValue(Int::class.java) ?: 0
                    var happiness = monster.child("happiness").getValue(Int::class.java) ?: 0
                    var hunger = monster.child("hunger").getValue(Int::class.java) ?: 0

                    // Load image using Glide
                    Glide.with(this@FeedActivity)
                        .load(imageURL)
                        .into(binding.eggImage)

                    // Update UI with data
                    binding.monsterName.text = name
                    binding.monsterExp.text = "Exp: $exp"
                    binding.monsterHappiness.text = "Happiness: $happiness"
                    binding.monsterHunger.text = "Hunger: $hunger"

                    binding.food.setOnClickListener {
                        if (hunger == 100) {
                            toast()
                        }else{
                            hunger += 10
                            hunger = hunger.coerceAtMost(100)

                            happiness += 2
                            happiness = happiness.coerceAtMost(100)
                        }

                        snapshot.child("monster/hunger").ref.setValue(hunger)
                        snapshot.child("monster/happiness").ref.setValue(happiness)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })

    }

    override fun onStop() {
        super.onStop()
        // Create a Handler object
        val currentUser = auth.currentUser
        val query = eggsRef.orderByChild("userId").equalTo(currentUser?.uid)

        currentUser?.let {
            val userRef = database.getReference("users")
            userRef.child(it.uid).push()
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val monster = snapshot.child("monster")
                    var hunger = monster.child("hunger").getValue(Int::class.java) ?: 0

//                    val decreaseHungerRunnable = Runnable { // Decrease the hunger node value by 10
//                        hunger -= 10
//                        hunger = hunger.coerceAtLeast(0)
//                        snapshot.child("monster/hunger").ref.setValue(hunger)
//                    }
//
//                    handler.removeCallbacks(decreaseHungerRunnable)
//
//                    // Post the Runnable to decrease hunger value after 12 hours
//                    handler.postDelayed(decreaseHungerRunnable, 12 * 60 * 60 * 1000)

                    val timer = Timer()

                    // Schedule the TimerTask to decrease hunger value every minute
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            hunger -= 5
                            hunger = hunger.coerceAtLeast(0)
                            snapshot.child("monster/hunger").ref.setValue(hunger)
                        }
                    }, 0, 60 * 1000) // 60,000 ms = 1 minute

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        }).apply {
            // Save the ValueEventListener instance to remove it later
            eggsRef.removeEventListener(this)
        }

    }

    fun toast() {
        Toast.makeText(this, "Monster is already full", Toast.LENGTH_SHORT).show()
    }
}

