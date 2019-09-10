package com.pedrenrique.githubapp.features.repositories

import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.domain.LoadRepositories
import com.pedrenrique.githubapp.features.common.PaginateViewModel
import com.pedrenrique.githubapp.features.common.adapter.model.RepositoryModelHolder

class RepositoriesViewModel(
    private val loadRepositories: LoadRepositories
) : PaginateViewModel<Repository>() {

    fun load() {
        loadIfNeeded {
            loadRepositories(LoadRepositories.Params(1))
        }
    }

    fun loadMore() {
        loadMoreIfNeeded { p ->
            loadRepositories(LoadRepositories.Params(p + 1))
        }
    }

    override fun transformData(data: Repository) =
        RepositoryModelHolder(data)
}

