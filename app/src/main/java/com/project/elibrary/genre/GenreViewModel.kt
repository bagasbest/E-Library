package com.project.elibrary.genre

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class GenreViewModel : ViewModel(){
    private val categoryList = MutableLiveData<ArrayList<GenreModel>>()
    private val listItems = ArrayList<GenreModel>()
    private val TAG = GenreViewModel::class.java.simpleName

    fun setListCategory() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("genre")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = GenreModel()
                        model.genre = document.data["category"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()

                        listItems.add(model)
                    }
                    categoryList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getGenreList() : LiveData<ArrayList<GenreModel>> {
        return categoryList
    }
}