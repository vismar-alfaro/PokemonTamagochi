package ph.kodego.alfaro.vismarjay.pokemotamagochi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityPlayBinding
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityPvpBinding

class PlayActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlayBinding
    private lateinit var opponentRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var opponentId = intent.getStringExtra("opponent_id")
//        opponentRef = database.getReference("eggs").orderByChild("userId").equalTo(opponentId)



    }
}