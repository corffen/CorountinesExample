package com.mindorks.example.coroutines.learn.concurrent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindorks.example.coroutines.utils.ControlledRunner
import com.mindorks.example.coroutines.utils.SingleRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ConcurrentViewModel : ViewModel() {
    private val _sortList = MutableLiveData<String>()
    val sortList: LiveData<String>
        get() = _sortList

    fun onSortAsc() = sortListBy(true)
    fun onSortDes() = sortListBy(false)
    private val controlRunner = ControlledRunner<String>()
    private val singleRunner = SingleRunner()
    private fun sortListBy(ascending: Boolean) {
        viewModelScope.launch {
//            _sortList.value = controlRunner.cancelPreviousThenRun {
//                mockSortArray(ascending)
//            }

//            _sortList.value = singleRunner.afterPrevious {
//                mockSortArray(ascending)
//            }

            _sortList.value = controlRunner.joinPreviousOrRun {
                mockSortArray(ascending)
            }
        }
    }

    private suspend fun mockSortArray(ascending: Boolean): String {
        withContext(Dispatchers.IO) {
            val rand = Random(System.nanoTime())
            //模拟耗时操作
            delay((800..1200).random(rand).toLong())
        }
        //排序
        return if (ascending) {
            intArrayOf(3, 4, 5, 1, 2, 9, 7).sortedArray().joinToString()
        } else {
            intArrayOf(3, 4, 5, 1, 2, 9, 7).sortedArrayDescending().joinToString()
        }
    }
}