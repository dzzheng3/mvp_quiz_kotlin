package com.zheng.home.features.quiz

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.zheng.home.R
import com.zheng.home.data.model.Quiz
import com.zheng.home.features.base.BaseActivity
import com.zheng.home.features.common.ErrorView
import kotlinx.android.synthetic.main.activity_quiz.*
import javax.inject.Inject


class QuizActivity : BaseActivity(), QuizMvpView, ErrorView.ErrorListener, QuizAdapter.OnClickListenr, DialogInterface.OnDismissListener {

    companion object {
        private var QUIZ: String = "quiz"
        private var USERCLICK_POSITION: String = "userClickPosition"
        private var DIALOG_SHOW: String = "dialogBuilder is showing"
    }

    @Inject
    lateinit var quizPresenter: QuizPresenter
    @Inject
    lateinit var quizAdapter: QuizAdapter
    private var quizPair: Pair<Int, Quiz>? = null
    private var dialogBuilder: AlertDialog.Builder? = null
    private var alertDialog: AlertDialog? = null
    private var userClickPosition: Int = -2
    private var isShowing: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        quizPresenter.attachView(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.quiz_title)
        swipe_to_refresh.setProgressBackgroundColorSchemeResource(R.color.primary)
        swipe_to_refresh.setColorSchemeResources(R.color.white)
        swipe_to_refresh.setOnRefreshListener { quizPresenter.getQuize() }
        swipe_to_refresh.isRefreshing = true
        quizAdapter.setOnClickListener(this)
        rv_quiz.layoutManager = GridLayoutManager(this, 2)
        rv_quiz.adapter = quizAdapter
        view_error.setErrorListener(this)
        dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder?.setCancelable(false)
        dialogBuilder?.setOnDismissListener(this)
        if (savedInstanceState != null) {
            val answer = savedInstanceState.getInt(QUIZ)
            val quiz = savedInstanceState.getParcelable<Quiz>(QUIZ)
            isShowing = savedInstanceState.getBoolean(DIALOG_SHOW)
            userClickPosition = savedInstanceState.getInt(USERCLICK_POSITION)
            showQuiz(quiz, answer)
            if (userClickPosition != -2 && isShowing) {
                if (userClickPosition == -1)
                    showTimeOut()
                else
                    showResult(userClickPosition)
            }
        } else {
            quizPresenter.getQuize()
        }
    }

    override fun showQuiz(quiz: Quiz, answer: Int) {
        quizPair = Pair(answer, quiz)
        tv_name.text = quiz.name
        swipe_to_refresh.isRefreshing = false
        view_error.visibility = View.INVISIBLE
        quizAdapter.setQuizAnswers(quiz.images)
        quizAdapter.notifyDataSetChanged()
    }

    override fun showProgress(show: Boolean) {
        view_error.visibility = View.INVISIBLE
        swipe_to_refresh.isRefreshing = true
    }

    override fun showError(error: Throwable) {
        swipe_to_refresh.isRefreshing = false
        view_error.visibility = View.VISIBLE
    }

    override fun onReloadData() {
        swipe_to_refresh.isRefreshing = true
        view_error.visibility = View.INVISIBLE
        quizPresenter.getQuize()
    }

    override val layout: Int
        get() = R.layout.activity_quiz

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(USERCLICK_POSITION, userClickPosition)
        outState.putBoolean(DIALOG_SHOW, isShowing)
        if (quizPair != null) {
            outState.putInt(QUIZ, quizPair!!.first)
            outState.putParcelable(QUIZ, quizPair!!.second)
        }
        super.onSaveInstanceState(outState)
    }

    override fun clickItem(answer: Int) {
        userClickPosition = answer
    }

    override fun onDismiss(p0: DialogInterface?) {
        isShowing = false
    }

    override fun countDown(time: Long) {
        tv_count.text = (30 - time).toString()
    }

    override fun showTimeOut() {
        userClickPosition = -1
        if (isShowing && alertDialog != null) {
            alertDialog?.dismiss()
        }
        dialogBuilder?.setMessage("OOPs! Time out. Would you want to try another one?")
        dialogBuilder?.setPositiveButton("Yes",
                { dialog, _ ->
                    quizPresenter.getQuize()
                    dialog.dismiss()
                })
        dialogBuilder?.setNegativeButton("No",
                { dialog, _ ->
                    dialog.dismiss()
                    finish()
                })
        dialogBuilder?.show()
        isShowing = true
    }

    override fun onDestroy() {
        super.onDestroy()
        quizPresenter.clear()
        if (isShowing && dialogBuilder != null) {
            dialogBuilder = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_submit -> {
                showResult(userClickPosition)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showResult(answer: Int) {
        if (answer == quizPair?.first) {
            dialogBuilder?.setMessage("Congratulations! You have selected correct answer! Would you like to try again?")
            dialogBuilder?.setPositiveButton("Yes",
                    { dialog, _ ->
                        quizPresenter.getQuize()
                        dialog.dismiss()
                    })
            dialogBuilder?.setNegativeButton("No",
                    { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    })
        } else {
            dialogBuilder?.setMessage("Nope. You are wrong! Would you like to try again?")
            dialogBuilder?.setPositiveButton("Yes",
                    { dialog, _ ->
                        dialog.dismiss()
                    })
            dialogBuilder?.setNegativeButton("No",
                    { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    })
        }
        isShowing = true
        alertDialog = dialogBuilder?.show()
    }
}
