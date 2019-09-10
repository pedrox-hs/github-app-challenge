package com.pedrenrique.githubapp.features.pr

import com.pedrenrique.githubapp.core.data.PullRequest
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.domain.LoadPRFromRepository
import com.pedrenrique.githubapp.features.common.PaginateViewModel
import com.pedrenrique.githubapp.features.common.adapter.model.PullRequestModelHolder

class PullRequestsViewModel(
    private val listPRFromRepository: ListPRFromRepository,
    private val loadPRFromRepository: LoadPRFromRepository
) : PaginateViewModel<PullRequest>() {
    fun load(repository: Repository) {
        loadIfNeeded {
            listPRFromRepository(ListPRFromRepository.Params(repository))
        }
    }

    fun loadMore(repository: Repository) {
        loadMoreIfNeeded { p ->
            loadPRFromRepository(LoadPRFromRepository.Params(p, repository))
        }
    }

    override fun transformData(data: PullRequest) =
        PullRequestModelHolder(data)
}