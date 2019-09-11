package com.pedrenrique.githubapp.core.di.modules.features

import com.pedrenrique.githubapp.core.data.datasource.ProjectRepository
import com.pedrenrique.githubapp.core.domain.LoadPRFromRepository
import com.pedrenrique.githubapp.features.pr.PullRequestsFragment
import com.pedrenrique.githubapp.features.pr.PullRequestsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val pullRequestModule = module {
    scope(named<PullRequestsFragment>()) {
        viewModel {
            PullRequestsViewModel(get())
        }

        factory {
            LoadPRFromRepository(get())
        }

        factory<ProjectRepository> {
            ProjectRepository.Impl(get())
        }
    }
}