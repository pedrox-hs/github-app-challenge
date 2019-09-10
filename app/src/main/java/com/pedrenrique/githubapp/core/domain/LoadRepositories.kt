package com.pedrenrique.githubapp.core.domain

import com.pedrenrique.githubapp.core.data.PaginatedData
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.data.datasource.ProjectRepository

class LoadRepositories(
    private val repository: ProjectRepository
) : UseCase<LoadRepositories.Params, PaginatedData<Repository>>() {

    override suspend fun run(params: Params) =
        repository.load(params.page)

    data class Params(val page: Int = 1)
}