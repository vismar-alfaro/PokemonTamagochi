package ph.kodego.alfaro.vismarjay.pokemotamagochi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.kodego.alfaro.vismarjay.pokemotamagochi.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendButton.setOnClickListener{
            val intent = Intent()
            intent.putExtra("email",binding.emailEditText.text.toString())
            setResult(1,intent)
            finish()
        }

    }
}