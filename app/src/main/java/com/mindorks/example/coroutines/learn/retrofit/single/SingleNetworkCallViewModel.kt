package com.mindorks.example.coroutines.learn.retrofit.single

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.mindorks.example.coroutines.data.api.ApiHelper
import com.mindorks.example.coroutines.data.local.DatabaseHelper
import com.mindorks.example.coroutines.data.model.ApiUser
import com.mindorks.example.coroutines.utils.Resource
import kotlinx.coroutines.launch

class SingleNetworkCallViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val _users = MutableLiveData<Resource<List<ApiUser>>>()
    val users: LiveData<Resource<List<ApiUser>>>
        get() = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _users.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getUsers()
                _users.postValue(Resource.success(usersFromApi))
            } catch (e: Exception) {
                _users.postValue(Resource.error(e.toString(), null))
            }
        }
    }

}
