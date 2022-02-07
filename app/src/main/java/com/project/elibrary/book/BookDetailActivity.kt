package com.project.elibrary.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.elibrary.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {

    private var binding: ActivityBookDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_BOOK = "book"
    }
}