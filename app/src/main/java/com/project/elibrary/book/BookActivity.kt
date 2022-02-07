package com.project.elibrary.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.elibrary.databinding.ActivityBookBinding

class BookActivity : AppCompatActivity() {

    var binding : ActivityBookBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_GENRE = "genre"
    }
}