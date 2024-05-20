package com.example.capstone_mursicapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mikhaellopez.circularimageview.CircularImageView
import java.util.concurrent.TimeUnit

class PostAdapter(var posts: List<PostModel>?) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var context: Context? = null

    var reactionType: String? = null

    var spotManager: SpotifyManager = SpotifyManager()

    interface PostLikedCallback {
        fun onPostLiked(isLiked: Boolean)
    }

    fun isPostLiked(postID: String?, callback: PostLikedCallback) {
        val documentReference = db.collection("Posts").document(
            (postID)!!
        )
        documentReference.get().addOnCompleteListener({ task: Task<DocumentSnapshot> ->
            if (task.isSuccessful()) {
                val doc: DocumentSnapshot = task.getResult()
                if (doc.exists()) {
                    val likes: List<Map<String, Any>>? = doc.get("Likes") as List<Map<String, Any>>?
                    var isLiked: Boolean = false
                    if (likes != null && !likes.isEmpty()) {
                        for (likesMap: Map<String, Any> in likes) {
                            val userID: String? = likesMap.get("User") as String?
                            if ((currentUser!!.getUid() == userID)) {
                                isLiked = true
                                break
                            }
                        }
                        callback.onPostLiked(isLiked)
                    } else {
                        callback.onPostLiked(isLiked)
                    }
                }
            } else {
                Log.e("isPostLiked", "Error getting document", task.getException())
                callback.onPostLiked(false) // Handle error case
            }
        })
    }

    interface reactionCallback {
        fun reactionFound(reactionType: String?)
    }

    fun getReactionType(postID: String?, callback: reactionCallback) {
        val documentReference = db.collection("Posts").document(
            (postID)!!
        )
        documentReference.get().addOnCompleteListener({ task: Task<DocumentSnapshot> ->
            if (task.isSuccessful()) {
                val doc: DocumentSnapshot = task.getResult()
                if (doc.exists()) {
                    val likes: List<Map<String, Any>>? = doc.get("Likes") as List<Map<String, Any>>?
                    if (likes != null && !likes.isEmpty()) {
                        for (likesMap: Map<String, Any> in likes) {
                            val user: String? = likesMap.get("User") as String?
                            if ((user == currentUser!!.getUid())) {
                                reactionType = likesMap.get("ReactionType") as String?
                                callback.reactionFound(reactionType)
                            }
                        }
                    }
                }
            }
        })
        if (reactionType == null) {
            callback.reactionFound("")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postlayout, parent, false)
        context = view.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postModel = posts!![position]
        holder.bind(postModel)

        holder.like.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d("post", "Clicked")
                val postID = postModel.getUserID()
                val likes: MutableMap<String, Any> = HashMap()

                isPostLiked(postID, object : PostLikedCallback {
                    override fun onPostLiked(isLiked: Boolean) {
                        if (!isLiked) {
                            Log.d("Post", "Post not Liked by user")
                            likes["User"] = currentUser!!.uid
                            likes["ReactionType"] = "like"
                            val documentReference = db.collection("Posts").document(postID)
                            documentReference.update("Likes", FieldValue.arrayUnion(likes))
                                .addOnSuccessListener { Log.d("Post", "Post Liked") }
                        }
                        getReactionType(postID, object : reactionCallback {
                            override fun reactionFound(reactionType: String?) {
                                if ((reactionType == "like") && isLiked) {
                                    Log.d("Post", "Post already liked")
                                    val userID = currentUser!!.uid
                                    val documentReference = db.collection("Posts").document(postID)
                                    val removeLike: MutableMap<String, Any> = HashMap()
                                    removeLike["User"] = userID
                                    removeLike["ReactionType"] = reactionType
                                    documentReference.update(
                                        "Likes",
                                        FieldValue.arrayRemove(removeLike)
                                    ).addOnSuccessListener(
                                        OnSuccessListener { Log.d("Firestore", "removed like") })
                                }
                            }
                        })
                    }
                })
            }
        })
        holder.like.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val activity = v.context as AppCompatActivity
                val reactions = Reactions(postModel.getUserID())
                reactions.show(activity.supportFragmentManager, reactions.javaClass.simpleName)
                return false
            }
        })

        holder.comment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val postID = postModel.getUserID()
                val activity = v.context as AppCompatActivity
                val dialog = CommentsFrag(postID)
                dialog.show(activity.supportFragmentManager, "")
            }
        })
    }


    override fun getItemCount(): Int {
        if (posts == null) {
            this.posts = ArrayList()
        } else {
            this.posts = posts
        }
        return posts!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var usernameTextView: TextView
        var timeTextView: TextView
        var testtext: TextView? = null
        var profilePic: CircularImageView
        var postImage: ImageView
        var songImage: ImageView
        var like: ImageButton
        var comment: ImageButton


        init {
            usernameTextView = itemView.findViewById(R.id.username)
            profilePic = itemView.findViewById(R.id.pfp)
            timeTextView = itemView.findViewById(R.id.time)
            postImage = itemView.findViewById(R.id.postImage)
            like = itemView.findViewById(R.id.like)
            comment = itemView.findViewById(R.id.comment)
            songImage = itemView.findViewById(R.id.songImage)
        }

        fun bind(postModel: PostModel) {
            val postID = postModel.getUserID()
            val documentReference = db.collection("Users").document(postID)
            documentReference.get()
                .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot> {
                    override fun onComplete(task: Task<DocumentSnapshot>) {
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                val username = document.getString("Username")
                                var pfp = document.getString("profilePicture")
                                if (pfp == null) {
                                    val defaultProfilePicResId = R.drawable.default_pfp
                                    pfp = defaultProfilePicResId.toString()
                                }
                                usernameTextView.text = username
                                val imageLoader = ImageLoader(itemView.context)
                                imageLoader.loadImage(pfp, profilePic)
                                val songId = postModel.getSongID()
                                Log.e("a", (songId==null).toString())
                                if (songId != null) {
                                    spotManager.getTrack(songId) {response ->
                                        Log.e("E", response!!.toString())
                                        if (response != null) {
                                            imageLoader.loadImage(response.body()!!.album.images[0].url, songImage)
                                        }
                                    }
                                }
                            }
                        }
                    }
                })

            like.setBackgroundResource(R.drawable.baseline_thumb_up_24)
            val currentTime = Timestamp.now()
            val pTime = postModel.gettimeStamp()

            val timePassed = currentTime.toDate().time - pTime.toDate().time
            val hours = TimeUnit.MILLISECONDS.toHours(timePassed)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timePassed) % 60

            val timeStamp: String
            if (hours < 1) {
                timeStamp = minutes.toString() + "ms ago"
            } else {
                timeStamp = hours.toString() + "hrs ago"
            }

            timeTextView.text = timeStamp
            val imageLoader = ImageLoader(itemView.context)
            imageLoader.loadImage(postModel.getpImage(), postImage)
            getReactionType(postID, object : reactionCallback {
                override fun reactionFound(reactionType: String?) {
                    Log.d("test", "reaction type found, entering switch")
                    when (reactionType) {
                        "like" -> like.setBackgroundResource(R.drawable.likedthumb_up_24)
                        "heart" -> like.setBackgroundResource(R.drawable.heart_reaction)

                        "love" -> like.setBackgroundResource(R.drawable.love_reaction)
                        "laugh" -> like.setBackgroundResource(R.drawable.laugh_reaction)
                        "sleep" -> like.setBackgroundResource(R.drawable.sleep_reaction)
                        "trash" -> like.setBackgroundResource(R.drawable.trash_reaction)
                    }
                }
            })

            val songID = postModel.getSongID()
        }
    }
}

