package com.pedrenrique.githubapp.features.pr

import com.pedrenrique.githubapp.core.data.PullRequest
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.domain.LoadPRFromRepository
import com.pedrenrique.githubapp.features.common.PaginateViewModel
import com.pedrenrique.githubapp.features.common.adapter.model.PullRequestModelHolder

class PullRequestsViewModel(
    private val loadPRFromRepository: LoadPRFromRepository
) : PaginateViewModel<PullRequest>() {
    fun load(repository: Repository) {
        loadIfNeeded {
            loadPRFromRepository(LoadPRFromRepository.Params(repository))
        }
    }

    fun loadMore(repository: Repository) {
        loadMoreIfNeeded { p ->
            loadPRFromRepository(LoadPRFromRepository.Params(repository, p + 1))
        }
    }

    override fun transformData(data: PullRequest) =
        PullRequestModelHolder(data)
}