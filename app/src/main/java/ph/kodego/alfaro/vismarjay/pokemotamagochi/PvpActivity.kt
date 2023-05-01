package ph.kodego.alfaro.vismarjay.pokemotamagochi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ph.kodego.alfaro.vismarjay.pokemotamagochi.adapter.UserListAdapter
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityPvpBinding
import ph.kodego.alfaro.vismarjay.pokemotamagochi.model.User

class PvpActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPvpBinding
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPvpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                val adapter = UserListAdapter(userList)
                recyclerView.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }
}