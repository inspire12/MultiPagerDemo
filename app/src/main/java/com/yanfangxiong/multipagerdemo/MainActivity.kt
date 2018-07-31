package com.yanfangxiong.multipagerdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.yanfangxiong.multipagerdemo.Utils.Boyoung
import com.yanfangxiong.multipagerdemo.Utils.BoyoungHelper
import com.yanfangxiong.multipagerdemo.Utils.RoundCornersTransformation
import com.yanfangxiong.multipagerdemo.factory.PageTransformerFactory
import com.yanfangxiong.multipagerdemo.factory.ScalePageTransformerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val views: ArrayList<View> = ArrayList()

    companion object {
        val REQUEST_ACT = 9001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ACT && resultCode == Activity.RESULT_OK){
            if (data != null){
                viewPager.setCurrentItem(data.getIntExtra("RESULT_CUR",0))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val listCoverBoyoung: ArrayList<Boyoung> = BoyoungHelper.getBoyoungFromJson("data", baseContext)
        //var index : Int = 0
        for( (index, image) in listCoverBoyoung.withIndex()) {
            val firstView = LayoutInflater.from(this).inflate(R.layout.pageritem, null)
            /**
             * image.imageUri 에 해당하는 이미지 파일이 drawble에 있어야함
             */
            Picasso.with(this).load(resources.getIdentifier(image.imageUri, "drawable",packageName))
                    .transform(RoundCornersTransformation(20, 20, true, true))
                    .into(firstView.findViewById<ImageView>(R.id.imageView))

            /**
             * 5개의 image를 보여줄 animation pager 를 보여줌
             */
            firstView.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    intent  = Intent(baseContext, ArticleViewPagerActivity::class.java)
                    intent.putExtra("subPager", index)
                    intent.putExtra("cover", listCoverBoyoung)
                    startActivity(intent)
                }
            })
            views.add(firstView)
        }

        viewPager.offscreenPageLimit = 3
        val metrics = DisplayMetrics()
        viewPager.pageMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, metrics).toInt()
        windowManager.defaultDisplay.getMetrics(metrics)


        viewPager.apply {
            adapter = object : PagerAdapter() {
                override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

                override fun getCount(): Int = views.size

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val view = views[position]
                    container.addView(view)
                    return view
                }
                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(views[position])
                }
            }
        }
        val factory: PageTransformerFactory = ScalePageTransformerFactory()
        viewPager.setPageTransformer(false, factory.generatePageTransformer())
    }

}

