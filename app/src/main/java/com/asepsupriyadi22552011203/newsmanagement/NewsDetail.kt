package com.asepsupriyadi22552011203.newsmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class NewsDetail : AppCompatActivity() {
    lateinit var newsTitle: TextView
    lateinit var newsSubtitle: TextView
    lateinit var newsImage: ImageView

    lateinit var edit: Button
    lateinit var hapus: Button

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI Components
        newsTitle = findViewById(R.id.newsTitle)
        newsSubtitle = findViewById(R.id.newsSubtitle)
        newsImage = findViewById(R.id.newsImage)

        edit = findViewById(R.id.editButton)
        hapus = findViewById(R.id.deleteButton)
        db = FirebaseFirestore.getInstance()

        // Get data from Intent
        val intent = intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("desc")
        val imageUrl = intent.getStringExtra("imageUrl")

        // set data to UI Components
        newsTitle.text = title
        newsSubtitle.text = subtitle
        Glide.with(this).load(imageUrl).into(newsImage)

        // EDIT ACTION
        edit.setOnClickListener {
            val editIntent = Intent(this@NewsDetail, NewsAdd::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("desc", subtitle)
                putExtra("imageUrl", imageUrl)
            }
            startActivity(editIntent)
        }

        // HAPUS ACTION
        hapus.setOnClickListener {
            id?.let {documentId ->
                db.collection("news").document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this@NewsDetail, "News deleted successfully", Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(this@NewsDetail, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(mainIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@NewsDetail, "Error deleting news ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.w("NewsDetails", "Error deleting document", e)
                    }
            }
        }
    }
}