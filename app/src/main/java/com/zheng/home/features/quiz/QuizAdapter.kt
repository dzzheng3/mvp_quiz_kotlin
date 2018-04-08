package com.zheng.home.features.quiz

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.zheng.home.R
import javax.inject.Inject

class QuizAdapter @Inject constructor() : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    var context: Context? = null

    private var list: List<String>? = null

    private var selectedPosition = -1

    init {
        list = emptyList()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): QuizViewHolder {
        context = parent?.context
        return QuizViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_quiz, parent, false))
    }

    fun setQuizAnswers(list: List<String>) {
        selectedPosition = -1
        this.list = list
    }

    override fun getItemCount(): Int {
        return list?.size as Int
    }

    override fun onBindViewHolder(holder: QuizViewHolder?, position: Int) {
        holder?.cb_answer?.isChecked = selectedPosition == position
        holder?.position = position
        holder?.iv_quiz?.layout(0, 0, 0, 0)
        Glide.with(context).load(list?.get(position))
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder?.iv_quiz)
    }

    open inner class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iv_quiz: ImageView? = view.findViewById(R.id.iv_quiz)
        var cb_answer: CheckBox? = view.findViewById(R.id.cb)
        var position: Int? = null

        init {
            view.setOnClickListener({
                if (onClickListenr != null) onClickListenr?.clickItem(position as Int)
                selectedPosition = position as Int
                notifyDataSetChanged()
            })

        }
    }

    private var onClickListenr: OnClickListenr? = null

    interface OnClickListenr {
        fun clickItem(answer: Int)
    }

    fun setOnClickListener(onClickListenr: OnClickListenr) {
        this.onClickListenr = onClickListenr

    }

}

private var String.isSelect: Boolean
    get() {
        return isSelect
    }
    set(value) {
        isSelect = value
    }
