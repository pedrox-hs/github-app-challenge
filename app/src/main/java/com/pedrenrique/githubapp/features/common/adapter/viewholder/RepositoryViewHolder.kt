package com.pedrenrique.githubapp.features.common.adapter.viewholder

import android.view.View
import com.pedrenrique.githubapp.R
import com.pedrenrique.githubapp.core.data.Repository
import com.pedrenrique.githubapp.core.ext.setRemoteImage
import com.pedrenrique.githubapp.features.common.adapter.BaseViewHolder
import com.pedrenrique.githubapp.features.common.adapter.factory.TypesFactory
import com.pedrenrique.githubapp.features.common.adapter.model.RepositoryModelHolder
import kotlinx.android.synthetic.main.item_repository.view.*
import java.text.NumberFormat
import java.util.*

class RepositoryViewHolder(view: View) : BaseViewHolder<RepositoryModelHolder>(view) {
    override fun bind(item: RepositoryModelHolder, typesFactory: TypesFactory) {
        itemView.populate(typesFactory, item.repo)
    }

    private fun View.populate(
        typesFactory: TypesFactory,
        repo: Repository
    ) {
        setOnClickListener {
            typesFactory.click(repo)
        }
        tvRepoName.text = repo.fullName
        tvDescription.text = repo.description?.takeIf { it.isNotBlank() }
            ?: context.getText(R.string.text_item_without_description)

        val numberFormat = NumberFormat.getInstance(Locale.getDefault())

        tvFork.text = numberFormat.format(repo.forksCount)
        tvFork.contentDescription = resources.getQuantityString(
            R.plurals.text_forks_description,
            repo.forksCount,
            repo.forksCount
        )

        tvStars.text = numberFormat.format(repo.stargazersCount)
        tvStars.contentDescription = resources.getQuantityString(
            R.plurals.text_stars_description,
            repo.stargazersCount,
            repo.stargazersCount
        )

        ivOwner.setRemoteImage(repo.owner.avatarUrl) {
            error(R.drawable.ic_person)
            placeholder(R.drawable.ic_person)
        }
    }
}