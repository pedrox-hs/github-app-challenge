package com.pedrenrique.githubapp.core.domain

import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.data.PullRequest
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.data.datasource.PullRequestRepository

class LoadPRFromRepository(
    private val repository: PullRequestRepository
) : UseCase<LoadPRFromRepository.Params, PaginatedData<PullRequest>>() {

    override suspend fun run(params: Params) =
        repository.load(params.repository, params.page)

    data class Params(val repository: Repository, val page: Int = 1)
}