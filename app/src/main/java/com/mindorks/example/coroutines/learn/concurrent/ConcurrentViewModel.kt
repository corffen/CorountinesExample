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
//            _sortList.value = mockSortArray(ascending)
            /**
             * 每次取消前一次的任务,然后再执行当前的任务
             */
//            _sortList.value = controlRunner.cancelPreviousThenRun {
//                mockSortArray(ascending)
//            }
            /**
             * 串行执行,也就是前一个执行完,再执行当前的
             */
            _sortList.value = singleRunner.afterPrevious {
                mockSortArray(ascending)
            }
            /**
             * 等待前一次执行完,否则
             */
//            _sortList.value = controlRunner.joinPreviousOrRun {
//                mockSortArray(ascending)
//            }
        }
    }

    private suspend fun mockSortArray(ascending: Boolean): String {
      return  withContext(Dispatchers.IO) {
            val rand = Random(System.nanoTime())
            //模拟耗时操作
            delay((800..1200).random(rand).toLong())

            if (ascending) {
                generateArray(6).sortedArray().joinToString()
            } else {
                generateArray(7).sortedArrayDescending().joinToString()
            }
        }
    }

    private fun generateArray(size:Int):IntArray{
        val nums = IntArray(size)
        val rand = Random(System.nanoTime())
        repeat(size) {
            nums[it] = rand.nextInt(1,10)
        }
        return nums
    }
}
