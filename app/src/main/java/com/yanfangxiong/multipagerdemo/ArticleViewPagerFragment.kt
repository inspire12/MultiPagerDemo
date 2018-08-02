package com.yanfangxiong.multipagerdemo

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.ethanhua.skeleton.Skeleton
import com.squareup.picasso.Picasso
import com.yanfangxiong.multipagerdemo.Utils.Boyoung
import com.yanfangxiong.multipagerdemo.Utils.CustomLog
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleViewPagerFragment : Fragment() {

    lateinit var bottomSheet: BottomSheetBehavior<View>

    var pagerIndex = 0
    //외부에서 데이터를 받아옴
    var coverIndex = 0
    lateinit var listBoyoung: ArrayList<Boyoung>
    //var showskeletonScreen: ViewSkeletonScreen? = null
    // 애니메이션
    var animPagerProgress: ScaleAnimation = ScaleAnimation(0F, 1F, 1F, 1F)
    lateinit var animScaleImageX: Animator
    lateinit var animScaleImageY: Animator
    var animImage : AnimatorSet = AnimatorSet()
    var isAnimationing: Boolean = false

    lateinit var anim : Animator

    // 위 아래 swipe 확인
    var dragX: Float = 0.0F
    var dragY: Float = 0.0F
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {
        super.onStart()
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        val args = arguments

        listBoyoung = args!!.getParcelableArrayList<Boyoung>("boyoung")
        coverIndex = args.getInt("subPager", 0)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        setPaginator()
        //getId 로 설정 changeImage(pagerIndex )
        setImageEvent();

        initAnimation()
        // skeleton 설정
      //  showskeletonScreen = Skeleton.bind(clContainer).load(R.layout.item_skeleton).show()!!


    }
    private fun initAnimation(){
        changeImage(pagerIndex)

        // progress bar
        animPagerProgress.duration = 6000
        animPagerProgress.fillAfter = false

        // image scale animation
        animScaleImageX = ObjectAnimator.ofFloat(ivPagerImage, "scaleX", 1.0F, 1.4F)
        animScaleImageY = ObjectAnimator.ofFloat(ivPagerImage, "scaleY", 1.0F, 1.4F)

        animScaleImageX.setDuration(6000)
        animScaleImageY.setDuration(6000)

        animImage.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                CustomLog.cd("ANIM","REPEAT")
            }

            override fun onAnimationEnd(animation: Animator?) {
                CustomLog.cd("ANIM","END")
                pagerIndex = (pagerIndex + 1) % listBoyoung.size
                changeImage(pagerIndex)
                //startAnimation(ivPagerImage)
            }

            override fun onAnimationCancel(animation: Animator?) {
                CustomLog.cd("ANIM","CANCEL")
            }

            override fun onAnimationStart(animation: Animator?) {
                CustomLog.cd("ANIM","START")
            }
        })
        animImage.play(animScaleImageX).with(animScaleImageY)


    }
    override fun onResume() {
        super.onResume()
        CustomLog.d("onResume")
        tvTest.visibility = View.VISIBLE
        tsArticle.visibility = View.VISIBLE
        tsTitle.visibility = View.VISIBLE
        //initAnimation()
        //startAnimation(ivPagerImage)
        startAnimation(ivPagerImage)
        paginator.findViewWithTag<Button>(pagerIndex).setBackgroundResource(R.color.dark_transparent)

    }



    override fun onPause() {
        super.onPause()
        CustomLog.d("onPause")
        stopAnimation(ivPagerImage)
        paginator.findViewWithTag<Button>(pagerIndex).setBackgroundResource(R.color.dark_transparent)
    }

    companion object {
        fun newInstance(getListBoyoung: ArrayList<Boyoung>, coverIndex: Int): ArticleViewPagerFragment {
            val args = Bundle()

            args.putParcelableArrayList("boyoung", getListBoyoung)
            args.putInt("subPager", coverIndex)
            // loading 타이밍 질문
            val fragment = ArticleViewPagerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    /**
     *
     */
    private fun setImageEvent() {

        bottomSheet = BottomSheetBehavior.from(llBottomSheet)
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    stopAnimation(ivPagerImage)
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    startAnimation(ivPagerImage)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        ibBottomSheetCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })

        ibArticle.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        })
        var mLastMotionX = 0.0F
        var mLastMotionY = 0.0F
        val safeDistance = 20.0F
        var time_down = 0.0F

        ivPagerImage.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                    when (event!!.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            CustomLog.cd("CLICK", "ACTION_DOWN")
                            mLastMotionX = event.getX()
                            mLastMotionY = event.getY()   // 시작 위치 저장
                            time_down = System.currentTimeMillis().toFloat()
                           // stopAnimation(ivPagerImage)
                            ivPagerImage.animation
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            CustomLog.cd("CLICK", "ACTION_UP")
                            dragX = mLastMotionX - event.getX()
                            dragY = mLastMotionY - event.getY()
                            CustomLog.cd("CLICK", Math.abs(dragX).toString())
                            CustomLog.cd("CLICK", Math.abs(dragY).toString())
                            time_down = System.currentTimeMillis().toFloat() - time_down
                            CustomLog.cd("CLICK", System.currentTimeMillis().toFloat().toString())
                            CustomLog.cd("CLICK", System.currentTimeMillis().toFloat().toString())
                                // 좌우는 view pager로 처리

                            if(Math.abs(dragY) < safeDistance){
                                // 클릭 안
                                if(mLastMotionX < v!!.width / 2){
                                    pagerIndex = (pagerIndex - 1 + listBoyoung.size) % listBoyoung.size

                                }else {
                                    pagerIndex = (pagerIndex + 1) % listBoyoung.size
                                }
                                changeImage(pagerIndex)

                            }else if(dragY > safeDistance){
                                //위로
                                Log.d("Click", "move up")
                                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                            }else if( dragY < -1 * safeDistance){
                                Log.d("Click", "move down")
                                activity!!.onBackPressed()
                            }
                            return true
                        }
                    }
                return false
            }
        })


    }

    private fun stopAnimation(ivPagerImage: ImageView) {
        isAnimationing = false
        animImage.pause()

        //  animLottie.cancelAnimation()
        //  paginator.findViewWithTag<Button>(pagerIndex ).setBackgroundResource(R.drawable.page_button)
        paginator.findViewWithTag<Button>(pagerIndex).setBackgroundResource(R.color.dark_transparent)
        paginator.findViewWithTag<Button>(pagerIndex).clearAnimation()
    }

    private fun startAnimation(view: ImageView) {
        isAnimationing = true

        animImage.start()
    }

    private fun textAnimation(view: View, duration: Long, delay: Long ){
        view.visibility = View.VISIBLE
        var width =  -view.width.toFloat();
        if(width == 0.0F){
            width = 100F
        }
        val animator1: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", -view.width.toFloat(), 0F)
        val animator2: ObjectAnimator = ObjectAnimator.ofFloat(view, "Alpha", 0F, 0F)
        val animator3: ObjectAnimator = ObjectAnimator.ofFloat(view, "Alpha", 0F, 0.6F)

        animator2.setDuration(delay)
        animator1.setDuration(duration)
        animator3.startDelay = delay
        animator3.setDuration(duration)
        animator1.startDelay = delay
        animator1.start()
        animator2.start()
        animator3.start()

    }
    /**
     * 하나의 viewpager 안에서 이미지와 텍스트 애니메이션, 변환하는 함수
     * skeleton / llArticle / texts animation / paginator
     */
    private fun changeImage(index: Int) {
        //showskeletonScreen!!.show()
        val showskeletonScreen =
                Skeleton.bind(clContainer).load(R.layout.item_skeleton).show()
//        showskeletonScreen.hide()

        Picasso.with(context).load(resources.getIdentifier(listBoyoung[index].imageUri, "drawable", activity!!.packageName)).into(ivPagerImage, object : com.squareup.picasso.Callback {
            //다른 곳에 놓으면 이미지 로딩할 요소가 null 상태
            //로딩 후 실제 로직

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess() {

                CustomLog.cd("Speed", "Success")
                /**
                 * 애니메이션으로 설정
                 */
                showskeletonScreen.hide()
//                showskeletonScreen!!.hide()

                for (e in 0 until listBoyoung.size) {
                    paginator.findViewWithTag<Button>(e).animation = null
                    if (e < index) {
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.drawable.page_button, resources.newTheme())
                    } else if (e == index) {
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.drawable.page_button, resources.newTheme())
                        paginator.findViewWithTag<Button>(e).startAnimation(animPagerProgress)
                    } else {
                        paginator.findViewWithTag<Button>(e).background = resources.getDrawable(R.color.dark_transparent, resources.newTheme())
                    }
                }

                tsTitle.setText(listBoyoung[index].title)
                tsArticle.setText(listBoyoung[index].overView)
                tvSubTitle.setText(listBoyoung[index].title)
                tvSubContent.setText(listBoyoung[index].overView)

                textAnimation(tvTest, 2000, 0)
                textAnimation(tsTitle, 2000, 1000)
                textAnimation(tsArticle, 2000, 2000)

               startAnimation(ivPagerImage)
            }

            override fun onError() {
                // progressbar 변화 취소
//                showskeletonScreen!!.hide()
                paginator.findViewWithTag<Button>(index).clearAnimation()
                CustomLog.cd("Speed", "Error")
             }
        })
    }


    private fun setPaginator() {
        paginator.removeAllViews()
        for (e in 0 until listBoyoung.size) {
            val btnPage = Button(context)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f) //
            lp.setMargins(5, 0, 0, 0)
            if (e == listBoyoung.size - 1) {
                lp.setMargins(5, 0, 5, 0)
            }

            btnPage.tag = e
            btnPage.setLayoutParams(lp)

            btnPage.visibility = View.VISIBLE
            btnPage.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    Log.d("index", e.toString())
                    pagerIndex = e
                    changeImage(pagerIndex)
                    //startAnimation(ivPagerImage)
                }
            })
            paginator.addView(btnPage)
        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isResumed())
            if (isVisibleToUser == true) {
                CustomLog.cd("visible","ok")
                if(animImage.isPaused){
                    startAnimation(ivPagerImage)
                }
            } else {
                CustomLog.cd("visible","no")
                stopAnimation(ivPagerImage)
                tvTest.visibility = View.INVISIBLE
                tsArticle.visibility = View.INVISIBLE
                tsTitle.visibility = View.INVISIBLE
            }
    }
}

