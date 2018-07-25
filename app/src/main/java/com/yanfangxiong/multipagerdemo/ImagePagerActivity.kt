package com.yanfangxiong.multipagerdemo

import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.*
import android.widget.*
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.squareup.picasso.Picasso
import com.yanfangxiong.multipagerdemo.R.id.ivImagePager
import com.yanfangxiong.multipagerdemo.R.id.paginator
import com.yanfangxiong.multipagerdemo.Utils.Boyoung
import com.yanfangxiong.multipagerdemo.Utils.BoyoungHelper
import com.yanfangxiong.multipagerdemo.Utils.CustomLog
import kotlinx.android.synthetic.main.activity_image_pager.*
import kotlinx.android.synthetic.main.activity_image_pager.view.*

class ImagePagerActivity : AppCompatActivity() {
    var animScaleImage:Animation? = null
    var animPagerProgress: Animation? = null
    var animTextFade:Animation? = null
    // Default text
    companion object {
        private const val title:String = "박보영"
        private const val article:String = "\"너의 결혼식 개봉.\"\n"
    }

    var index = 0


    //외부에서 데이터를 받아옴
    lateinit var getId:String
    lateinit var listBoyoung: ArrayList<Boyoung>
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)
        animScaleImage = AnimationUtils.loadAnimation(this, R.anim.scale);
        animPagerProgress = ScaleAnimation(0F,1F, 1F, 1F)
        animTextFade = AnimationUtils.loadAnimation(this, R.anim.blink)
        /**
         * 이전 viewpager에서 위치를 받은 후 처리
         * 받아온 후 애니메이션, 이미지 전환
         */
        intent.getStringExtra("subPager")?.let {

            // 데이터를 받음
            getId = intent.getStringExtra("subPager")   // imageUri
            CustomLog.cd("getId",getId)
            listBoyoung = BoyoungHelper.getBoyoungFromJson(getId, baseContext)

            //변화하는 부분
            setPaginator()
            startAnimation(ivImagePager)
        }
    }
    private fun startAnimation(view: ImageView){
        changeImage(index)
        animScaleImage!!.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
                index = (index + 1) % listBoyoung.size
                changeImage(index)
            }
            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
        view.startAnimation(animScaleImage)

    }

    private fun changeImage(index:Int){
        // progressbar 변화

        Picasso.with(baseContext).load(resources.getIdentifier(listBoyoung[index].imageUri, "drawable", packageName)).into(ivImagePager, object: com.squareup.picasso.Callback{
            //다른 곳에 놓으면 이미지 로딩할 요소가 null 상태
            val skeletonScreen: SkeletonScreen  = Skeleton.bind(llArticle)
                    .load(R.layout.item_skeleton)
                    .duration(2000)
                    .show()!!

            //로딩 후 실제 로직
            override fun onSuccess() {
                skeletonScreen.hide()
                //
                tvTitle.setText(listBoyoung[index].title)
                tvArticle.setText(listBoyoung[index].overView)
                tvTitle.startAnimation(animTextFade)
                tvArticle.startAnimation(animTextFade)

                animPagerProgress?.duration = animScaleImage!!.duration
                animPagerProgress?.fillAfter = true

                for (e in 0 until listBoyoung.size){
                    CustomLog.cd("e", e.toString())
                    paginator.findViewWithTag<Button>(e).animation = null
                    if (e < index) {
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.drawable.page_button)
                    }else if( e == index){
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.drawable.page_button)
                        paginator.findViewWithTag<Button>(e).startAnimation(animPagerProgress)
                    }
                    else{
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.color.dark_transparent)
                    }
                }
            }
            override fun onError() {
                // progressbar 변화 취소
            }
        })
    }
    private fun setPaginator(){
        paginator.removeAllViews()
        for (e in 0 until listBoyoung.size ) {
            val btnPage = Button(this)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f) //
            lp.setMargins(5, 0, 0 , 0)
            if(e == listBoyoung.size -1){
                lp.setMargins(5, 0, 5 , 0)
            }

            btnPage.tag = e
            btnPage.setLayoutParams(lp)

            btnPage.visibility = View.VISIBLE
            btnPage.setOnClickListener(object: View.OnClickListener{
                override fun onClick(v: View?) {
                    Log.d("index",e.toString())
                    index = e

                    startAnimation(ivImagePager)
                }
            })
            paginator.addView(btnPage)

        }
    }
}
