package com.yanfangxiong.multipagerdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.yanfangxiong.multipagerdemo.Utils.CustomLog
import com.yanfangxiong.multipagerdemo.Utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_article.*
import xyz.klinker.android.drag_dismiss.activity.DragDismissActivity


class ArticleActivity : DragDismissActivity() {

    companion object {
        val EXTRA_SHOW_PROGRESS = "extra_show_progress"
    }
    lateinit var title: String
    lateinit var overView: String

    override fun onCreateContent(inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?): View {

        val v = inflater!!.inflate(R.layout.activity_article, parent, false)
        val tv: TextView = v.findViewById<TextView>(R.id.tvSubArticle)

        if (!dragDismissDelegate.shouldShowToolbar()) {
            // don't need the padding that pushes it below the toolbar
            tv.setPadding(0, 0, 0, 0)
        }

        if (intent.getBooleanExtra(EXTRA_SHOW_PROGRESS, false)) {
            showProgressBar()
            tv.visibility = View.GONE
            title = intent.getStringExtra("title")
            overView = intent.getStringExtra("article")
            tv.setText(overView)
            tv.postDelayed({
                tv.visibility = View.VISIBLE
                hideProgressBar()
            }, 1500)
        }

        return v
    }

}
