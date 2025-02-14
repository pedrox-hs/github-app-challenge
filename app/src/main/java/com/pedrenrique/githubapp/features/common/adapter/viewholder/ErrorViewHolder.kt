package com.pedrenrique.githubapp.features.common.adapter.viewholder

import android.view.View
import com.pedrenrique.githubapp.core.ext.defaultErrorDrawable
import com.pedrenrique.githubapp.core.ext.defaultFriendlyMsg
import com.pedrenrique.githubapp.core.ext.defaultFriendlyTitle
import com.pedrenrique.githubapp.features.common.adapter.BaseViewHolder
import com.pedrenrique.githubapp.features.common.adapter.factory.TypesFactory
import com.pedrenrique.githubapp.features.common.adapter.model.ErrorModelHolder
import kotlinx.android.synthetic.main.item_error_state.view.*

class ErrorViewHolder(view: View) : BaseViewHolder<ErrorModelHolder>(view) {
    override fun bind(item: ErrorModelHolder, typesFactory: TypesFactory) {
        itemView.btnTryAgain.setOnClickListener {
            typesFactory.click(item.error)
        }
        val error = item.error.error
        itemView.tvErrorTitle.setText(error.defaultFriendlyTitle)
        itemView.tvErrorMessage.setText(error.defaultFriendlyMsg)
        itemView.ivProblem.setImageResource(error.defaultErrorDrawable)
    }
}