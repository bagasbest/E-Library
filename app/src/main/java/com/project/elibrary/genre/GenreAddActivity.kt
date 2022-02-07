package com.project.elibrary.genre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.elibrary.databinding.ActivityGenreAddBinding

class GenreAddActivity : AppCompatActivity() {

    private var binding: ActivityGenreAddBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenreAddBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}