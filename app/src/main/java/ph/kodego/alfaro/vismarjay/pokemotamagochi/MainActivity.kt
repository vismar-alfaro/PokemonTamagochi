package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var eggsRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        database = Firebase.database
        eggsRef = database.getReference("eggs")
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val query = eggsRef.orderByChild("userId").equalTo(currentUser?.uid)


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
                    Glide.with(this@MainActivity)
                        .load(imageURL)
                        .into(binding.eggImage)

                    // Update UI with data
                    binding.monsterName.text = name
                    binding.monsterExp.text = "Exp: $exp"
                    binding.monsterHappiness.text = "Happiness: $happiness"
                    binding.monsterHunger.text = "Hunger: $hunger"

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })

        val handleTouch = object : View.OnTouchListener {
            var lastX = 0f
            var lastY = 0f
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX
                        lastY = event.rawY
                        animateCreature(lastX, lastY)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        lastX = event.rawX
                        lastY = event.rawY
                    }
                    MotionEvent.ACTION_UP -> {
                        animateCreature(lastX, lastY)
                    }
                }
                return true
            }
        }

        binding.mainall.setOnTouchListener(handleTouch)

        binding.feedButton.setOnClickListener{
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)

        }

        binding.pvpButton.setOnClickListener{
            val intent = Intent(this, PvpActivity::class.java)
            startActivity(intent)


        }
    }

    private fun animateCreature(x: Float, y: Float) {
        binding.eggImage.animate()
            .x(x - binding.eggImage.width / 2)
            .y(y - binding.eggImage.height / 2)
            .setDuration(500)
            .start()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {

        val auth = FirebaseAuth.getInstance()
        val intent = Intent(this, LoginActivity::class.java)
        auth.signOut()
        startActivity(intent)
        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        finish()
    }


}






