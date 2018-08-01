package com.yanfangxiong.multipagerdemo

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.squareup.picasso.Picasso
import com.yanfangxiong.multipagerdemo.Utils.Boyoung
import com.yanfangxiong.multipagerdemo.Utils.CustomLog
import com.yanfangxiong.multipagerdemo.Utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleViewPagerFragment : Fragment() {



    lateinit var bottomSheet: BottomSheetBehavior<View>


    var pagerIndex = 0
    //외부에서 데이터를 받아옴
    var coverIndex = 0
    lateinit var listBoyoung: ArrayList<Boyoung>
    lateinit var skeletonScreen: ViewSkeletonScreen.Builder

    // 애니메이션
    var animPagerProgress: ScaleAnimation = ScaleAnimation(0F, 1F, 1F, 1F)
    lateinit var animTextFade: Animator
    lateinit var animScaleImage: Animation
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
        animScaleImage = AnimationUtils.loadAnimation(context, R.anim.scale)

        listBoyoung = args!!.getParcelableArrayList<Boyoung>("boyoung")

        //getListCoverBoyoung = args.getParcelableArrayList<Boyoung>("cover")
        coverIndex = args.getInt("subPager", 0)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setPaginator()
        //getId 로 설정 changeImage(pagerIndex )
        setImageEvent();

        skeletonScreen = Skeleton.bind(clContainer).load(R.layout.item_skeleton)
        //

        startAnimation(ivPagerImage)
    }

    override fun onResume() {
        super.onResume()
        CustomLog.d("onResume")
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
        fun newInstance(getListCoverBoyoung: ArrayList<Boyoung>, getListBoyoung: ArrayList<Boyoung>, coverIndex: Int): ArticleViewPagerFragment {
            val args = Bundle()

            args.putParcelableArrayList("cover", getListCoverBoyoung)
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
        tvTest.visibility = View.INVISIBLE
        tsTitle.visibility = View.INVISIBLE
        tsArticle.visibility = View.INVISIBLE

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
        ivPagerImage.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
            override fun onSwipeUp() {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onSwipeDown() {
                //    activity!!.onBackPressed()
            }
        })
        ibArticle.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
//                val intent = Intent(context, ArticleActivity::class.java)
//                DragDismissIntentBuilder(context)
//                        .setShowToolbar(false)
//                        .setFullscreenOnTablets(true)
//                        .setDragElasticity(DragDismissIntentBuilder.DragElasticity.XXLARGE)
//                        .build(intent)
//                intent.putExtra(ArticleActivity.EXTRA_SHOW_PROGRESS, true)
//                intent.putExtra("title", listBoyoung[pagerIndex ].title)
//                intent.putExtra("article", listBoyoung[pagerIndex ].overView)
//                startActivity(intent)
//                activity!!.overridePendingTransition(R.anim.anim_slide_up, R.anim.anim_stay)
            }
        })
        var mLastMotionX = 0.0F
        var mLastMotionY = 0.0F
        val safeDistance = 10.0F
        var time_down = 0.0F
        val longClickTimer = 3.0F
        var mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop; // 드래그를 인식

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
                            CustomLog.cd("CLICK", time_down.toString())
                                // 좌우는 view pager로 처리

                            if(Math.abs(dragY) < safeDistance){
                                // 클릭 안
                                if(mLastMotionX < v!!.width / 2){
                                    pagerIndex = (pagerIndex - 1 + listBoyoung.size) % listBoyoung.size
                                    startAnimation(ivPagerImage)
                                }else {
                                    pagerIndex = (pagerIndex + 1) % listBoyoung.size
                                    startAnimation(ivPagerImage)
                                }

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

        // long click 시 애니메이션 조작
        ivPagerImage.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Log.d("Click","longClick")
                if (isAnimationing) {
                    stopAnimation(ivPagerImage)
                } else {
                    startAnimation(ivPagerImage)
                }
                return true
            }
        })
    }

    private fun stopAnimation(ivPagerImage: ImageView) {
        isAnimationing = false
        ivPagerImage.clearAnimation()
        //  animLottie.cancelAnimation()
        //  paginator.findViewWithTag<Button>(pagerIndex ).setBackgroundResource(R.drawable.page_button)
        paginator.findViewWithTag<Button>(pagerIndex).setBackgroundResource(R.color.dark_transparent)
        paginator.findViewWithTag<Button>(pagerIndex).clearAnimation()
    }

    private fun startAnimation(view: ImageView) {
        isAnimationing = true
        changeImage(pagerIndex)
        animScaleImage.repeatMode = Animation.INFINITE

        animScaleImage.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                CustomLog.cd("REPEAT","repeat")
                pagerIndex = (pagerIndex + 1) % listBoyoung.size
                changeImage(pagerIndex)
                //            animLottie.playAnimation()
            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })

        ivPagerImage.startAnimation(animScaleImage)
    }

    private fun textAnimation(view: View, duration: Long, delay: Long ){
        view.visibility = View.VISIBLE

        val animator1: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", -view.width.toFloat(), 0F)
        val animator2: ObjectAnimator = ObjectAnimator.ofFloat(view, "Alpha", 0F, 0F)
        val animator3: ObjectAnimator = ObjectAnimator.ofFloat(view, "Alpha", 0F, 0.6F)

        animator2.setDuration(delay)
        animator1.setDuration(duration)
        animator3.startDelay = delay
        animator3.setDuration(duration)
        animator1.startDelay = delay
        animator2.start()
        animator3.start()
        animator1.start()
    }
    /**
     * 하나의 viewpager 안에서 이미지와 텍스트 애니메이션, 변환하는 함수
     * skeleton / llArticle / tsArticle / paginator
     */
    private fun changeImage(index: Int) {
        val showskeletonScreen = skeletonScreen.show()
        Picasso.with(context).load(resources.getIdentifier(listBoyoung[index].imageUri, "drawable", activity!!.packageName)).into(ivPagerImage, object : com.squareup.picasso.Callback {
            //다른 곳에 놓으면 이미지 로딩할 요소가 null 상태
            //로딩 후 실제 로직
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess() {
                showskeletonScreen.hide()
                CustomLog.cd("Speed", "Success")
                /**
                 * 애니메이션으로 설정
                 */
                tsTitle.setCurrentText(listBoyoung[index].title)
                tsArticle.setCurrentText(listBoyoung[index].overView)

                textAnimation(tvTest, 2000, 0)
                textAnimation(tsTitle, 2000, 1000)
                textAnimation(tsArticle, 2000, 2000)

                animPagerProgress.duration = animScaleImage.duration
                animPagerProgress.fillAfter = false

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
            }

            override fun onError() {
                // progressbar 변화 취소
                showskeletonScreen.hide()
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
                    startAnimation(ivPagerImage)
                }
            })
            paginator.addView(btnPage)
        }
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isResumed())
            if (isVisibleToUser == true) {
                startAnimation(ivPagerImage)
            } else {
                stopAnimation(ivPagerImage)
            }
    }
}

