package com.pedrenrique.githubapp.features.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.exceptions.EmptyResultException
import com.pedrenrique.githubapp.core.exceptions.InvalidPageException
import com.pedrenrique.githubapp.core.exceptions.NoMoreResultException
import com.pedrenrique.githubapp.features.common.adapter.ModelHolder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class PaginateViewModel<T> : ViewModel() {
    val state = MutableLiveData<DataState>()
    var page = 0
        private set

    fun setInitialState(state: DataState, page: Int = 0) {
        this.state.value = state
        this.page = page
    }

    protected abstract fun transformData(data: T): ModelHolder

    protected fun loadIfNeeded(requestData: suspend () -> PaginatedData<T>) {
        retrieveData {
            state.postValue(DataState.Pending)
            requestData()
        }
    }

    protected fun loadMoreIfNeeded(requestData: suspend (page: Int) -> PaginatedData<T>) {
        val value = state.value
        val data = (value as? DataState.Loaded)?.data ?: (value as? DataState.NextFailed)?.lastData
        ?: listOf()
        retrieveData(data) {
            state.postValue(DataState.NextPending(data))
            requestData(page)
        }
    }

    private fun retrieveData(
        lastData: List<ModelHolder>? = null,
        provider: suspend () -> PaginatedData<T>
    ) {
        viewModelScope.launch {
            val lastValue = state.value
            state.value = try {
                onDataReceived(provider())
            } catch (e: EmptyResultException) {
                DataState.Empty
            } catch (e: NoMoreResultException) {
                DataState.Completed(lastData ?: listOf())
            } catch (e: InvalidPageException) {
                e.printStackTrace()
                lastValue
            } catch (e: Throwable) {
                lastData?.let { DataState.NextFailed(e, it) }
                    ?: DataState.Failed(e)
            }
        }
    }

    private fun onDataReceived(
        result: PaginatedData<T>
    ): DataState.Loaded {
        val data = mutableSetOf<ModelHolder>()
        data.addAll(result.content.map {
            transformData(it)
        })
        page = result.page
        return DataState.Loaded(data.toList())
    }

    override fun onCleared() {
        super.onCleared()
        if (viewModelScope.isActive)
            viewModelScope.cancel()
    }
}