package ph.kodego.alfaro.vismarjay.pokemotamagochi.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ph.kodego.alfaro.vismarjay.pokemotamagochi.PlayActivity
import ph.kodego.alfaro.vismarjay.pokemotamagochi.R
import ph.kodego.alfaro.vismarjay.pokemotamagochi.model.User

class UserListAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        if (user.id == FirebaseAuth.getInstance().currentUser?.uid) {
            holder.itemView.visibility = View.GONE
            return
        }

        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val userProfileImageView: ImageView = itemView.findViewById(R.id.user_profile)
        private val userFirstNameTextView: TextView = itemView.findViewById(R.id.user_first_name)
        private val userLastNameTextView: TextView = itemView.findViewById(R.id.user_last_name)
        private val challengeButton: Button = itemView.findViewById(R.id.btn_challenge)
        private val currentUser = FirebaseAuth.getInstance().currentUser

        fun bind(user: User) {
            // Set user's profile image, first name, and last name to the views
            Glide.with(itemView.context)
                .load(user.profile)
                .circleCrop()
                .into(userProfileImageView)
            userFirstNameTextView.text = user.firstName
            userLastNameTextView.text = user.lastName

            challengeButton.setOnClickListener{
//                val intent = Intent(itemView.context, PlayActivity::class.java)
//                intent.putExtra("opponent_id", user.id)
//                itemView.context.startActivity(intent)
                val context: Context = itemView.context
                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                alertDialogBuilder.setMessage("Do you want to challenge ${user.firstName} ${user.lastName} to a battle?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        // Send a notification to the challenged user
                        val database = FirebaseDatabase.getInstance()
                        val opponentRef = database.getReference("users").child(user.id)
                        opponentRef.child("challenges").push().setValue(currentUser?.uid)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Notify the user that the challenge has been sent
                                    AlertDialog.Builder(context)
                                        .setTitle("Challenge Sent")
                                        .setMessage("Your challenge has been sent to ${user.firstName} ${user.lastName}. Please wait for them to accept the challenge.")
                                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                                            dialog.dismiss()
                                        })
                                        .show()
                                }
                            }
                    })
                    .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })

                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }

            // Listen for challenges from the user and handle them
            val currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser?.uid!!)
            currentUserRef.child("challenges").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val challengerId = snapshot.value as String
                    // Retrieve the challenger's information
                    val database = FirebaseDatabase.getInstance()
                    val challengerRef = database.getReference("users").child(challengerId)
                    challengerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val challenger = snapshot.getValue(User::class.java)
                            if (challenger != null) {
                                // Display a notification to the user that they have been challenged
                                AlertDialog.Builder(itemView.context)
                                    .setTitle("Challenge Received")
                                    .setMessage("You have been challenged by ${challenger.firstName} ${challenger.lastName}. Do you accept the challenge?")
                                    .setPositiveButton("Accept", DialogInterface.OnClickListener { dialog, id ->
                                        // Send a notification to the challenger that the challenge has been accepted
                                        // and start the play activity
                                        challengerRef.child("opponents").child(currentUser.uid!!).setValue(true)
                                        val intent = Intent(itemView.context, PlayActivity::class.java)
                                        intent.putExtra("opponent_id", challengerId)
                                        itemView.context.startActivity(intent)
                                    })
                                    .setNegativeButton("Decline", DialogInterface.OnClickListener { dialog, id ->
                                        // Remove the challenge from the user's list of challenges
//                                        snapshot.ref.parent?.child("challenges")?.child(snapshot.key!!)?.removeValue()
                                        database.getReference("users").child(currentUser.uid).child("challenges").removeValue()

                                    })
                                    .show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle child changed
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Handle child removed
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle child moved
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })


        }

    }

}
