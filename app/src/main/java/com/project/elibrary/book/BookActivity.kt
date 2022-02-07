package com.project.elibrary.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.elibrary.databinding.ActivityBookBinding

class BookActivity : AppCompatActivity() {

    var binding : ActivityBookBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val genre = intent.getStringExtra(EXTRA_GENRE)
        if(genre != "all"){
            binding?.category?.text = genre
            binding?.edit?.visibility = View.VISIBLE
            binding?.delete?.visibility = View.VISIBLE
            binding?.addBook?.visibility = View.VISIBLE
        }

        binding?.addBook?.setOnClickListener {

        }

        binding?.edit?.setOnClickListener {

        }

        binding?.delete?.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_GENRE = "genre"
    }
}