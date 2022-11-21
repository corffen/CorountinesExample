package com.mindorks.example.coroutines.learn.errorhandling.supervisor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.example.coroutines.data.api.ApiHelper
import com.mindorks.example.coroutines.data.local.DatabaseHelper
import com.mindorks.example.coroutines.data.model.ApiUser
import com.mindorks.example.coroutines.utils.Resource
import kotlinx.coroutines.*

class IgnoreErrorAndContinueViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val users = MutableLiveData<Resource<List<ApiUser>>>()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            users.postValue(Resource.loading(null))
            supervisorScope {
                val usersFromApiDeferred = async { apiHelper.getUsersWithError() }
                val moreUsersFromApiDeferred = async { apiHelper.getMoreUsers() }

                val usersFromApi = usersFromApiDeferred.await()

                val moreUsersFromApi = try {
                    moreUsersFromApiDeferred.await()
                } catch (e: Exception) {
                    emptyList()
                }

                val allUsersFromApi = mutableListOf<ApiUser>()
                allUsersFromApi.addAll(usersFromApi)
                allUsersFromApi.addAll(moreUsersFromApi)

                users.postValue(Resource.success(allUsersFromApi))
            }
            val userss = load { getUsers() }
        }
    }

    fun getUsers(): LiveData<Resource<List<ApiUser>>> {
        return users
    }

    suspend fun <T> load(action: () -> T): T {
        return withContext(Dispatchers.IO) {
            action()
        }
    }
}
