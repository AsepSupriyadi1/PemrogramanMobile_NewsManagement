package com.asepsupriyadi22552011203.newsmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager

import android.app.ProgressDialog
import android.util.Log
import android.view.View

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var myAdapter: AdapterList
    private lateinit var itemList: MutableList<ItemList>
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog

    override fun onStart() {
        super.onStart()
        // Ambil data dari firestore
        getData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        floatingActionButton = findViewById(R.id.floatAddNews);

        progressDialog = ProgressDialog(this@MainActivity).apply {
            setTitle("Loading...")
        }

        // Setup RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = ArrayList()
        myAdapter = AdapterList(itemList)
        recyclerView.adapter = myAdapter

        floatingActionButton.setOnClickListener {
            val toAddPage = Intent(this@MainActivity, NewsAdd::class.java)
            startActivity(toAddPage)
        }

        myAdapter.setOnItemClickListener(object : AdapterList.OnItemClickListener {
            override fun onItemClick(item: ItemList) {
                val intent = Intent(this@MainActivity, NewsDetail::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("title", item.judul)
                    putExtra("desc", item.subJudul)
                    putExtra("imageUrl", item.imageUrl)
                }
                startActivity(intent)
            }
        })


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        progressDialog.show()
        db.collection("news")
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    itemList.clear();
                    for(document in task.result) {
                        val item = ItemList(
                            document.id,
                            document.getString("title") ?: "",
                            document.getString("desc") ?: "",
                            document.getString("imageUrl") ?: ""
                        )
                        itemList.add(item)
                        Log.d("data", "${document.id} => ${document.data}")
                    }
                     myAdapter.notifyDataSetChanged()
                } else {
                    Log.w("data", "Error getting documents", task.exception)
                }
                progressDialog.dismiss()
            }
    }


}