package com.project.elibrary.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BookViewModel : ViewModel() {

    private val bookList = MutableLiveData<ArrayList<BookModel>>()
    private val listItems = ArrayList<BookModel>()
    private val TAG = BookViewModel::class.java.simpleName


    fun setListBookByAll() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("book")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BookModel()
                        model.genre = document.data["genre"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()
                        model.description = document.data["description"].toString()
                        model.writer = document.data["writer"].toString()
                        model.title = document.data["title"].toString()
                        model.pdf = document.data["pdf"].toString()

                        listItems.add(model)
                    }
                    bookList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListBookByAllAndQuery(query: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("book")
                .whereGreaterThanOrEqualTo("titleTemp", query)
                .whereLessThanOrEqualTo("titleTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BookModel()
                        model.genre = document.data["genre"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()
                        model.description = document.data["description"].toString()
                        model.writer = document.data["writer"].toString()
                        model.title = document.data["title"].toString()
                        model.pdf = document.data["pdf"].toString()

                        listItems.add(model)
                    }
                    bookList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListBookByLatest() {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("book")
                .orderBy("uid", Query.Direction.DESCENDING).limit(5)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BookModel()
                        model.genre = document.data["genre"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()
                        model.description = document.data["description"].toString()
                        model.writer = document.data["writer"].toString()
                        model.title = document.data["title"].toString()
                        model.pdf = document.data["pdf"].toString()

                        listItems.add(model)
                    }
                    bookList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun setListBookByGenre(genre: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("book")
                .whereEqualTo("genre", genre)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BookModel()
                        model.genre = document.data["genre"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()
                        model.description = document.data["description"].toString()
                        model.writer = document.data["writer"].toString()
                        model.title = document.data["title"].toString()
                        model.pdf = document.data["pdf"].toString()

                        listItems.add(model)
                    }
                    bookList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setListBookByGenreAndQuery(genre: String, query: String) {
        listItems.clear()


        try {
            FirebaseFirestore.getInstance().collection("book")
                .whereEqualTo("genre", genre)
                .whereGreaterThanOrEqualTo("titleTemp", query)
                .whereLessThanOrEqualTo("titleTemp", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val model = BookModel()
                        model.genre = document.data["genre"].toString()
                        model.uid = document.data["uid"].toString()
                        model.image = document.data["image"].toString()
                        model.description = document.data["description"].toString()
                        model.writer = document.data["writer"].toString()
                        model.title = document.data["title"].toString()
                        model.pdf = document.data["pdf"].toString()

                        listItems.add(model)
                    }
                    bookList.postValue(listItems)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getGenreList() : LiveData<ArrayList<BookModel>> {
        return bookList
    }



}