package com.yanfangxiong.multipagerdemo

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.ethanhua.skeleton.SkeletonScreen
import com.squareup.picasso.Picasso
import com.yanfangxiong.multipagerdemo.Utils.Boyoung
import com.yanfangxiong.multipagerdemo.Utils.CustomLog
import com.yanfangxiong.multipagerdemo.Utils.OnSwipeTouchListener
import kotlinx.android.synthetic.main.fragment_article.*
import xyz.klinker.android.drag_dismiss.DragDismissIntentBuilder
import android.view.MotionEvent


class ArticleViewPagerFragment : Fragment() {


    var animPagerProgress: Animation = ScaleAnimation(0F, 1F, 1F, 1F)
    lateinit var animTextFade: Animator
    lateinit var animScaleImage: Animation
    var isAnimationing: Boolean = false
    var coverIndex = 0
    var pagerIndex = 0
    lateinit var bottomSheet: BottomSheetBehavior<View>
    //외부에서 데이터를 받아옴
    lateinit var listBoyoung: ArrayList<Boyoung>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {
        super.onStart()
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        animTextFade = AnimatorInflater.loadAnimator(context, R.animator.blink)

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
        //startAnimation(ivPagerImage)
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
        var mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop; // 드래그를 인식
        var mHasPerformedLongPress = false
        var mHandler: Handler
        ivPagerImage.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                    when (event!!.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d("CLICK", "ACTION_DOWN")
                            mLastMotionX = event.getX()
                            mLastMotionY = event.getY()   // 시작 위치 저장
                           // stopAnimation(ivPagerImage)
                            ivPagerImage.animation
                            return true
                        }
                        MotionEvent.ACTION_UP ->{
                            Log.d("CLICK", "ACTION_UP")
                           // startAnimation(ivPagerImage)
                            if(mLastMotionX < v!!.width / 2){
                                pagerIndex = (pagerIndex - 1 + listBoyoung.size) % listBoyoung.size
                                changeImage(pagerIndex)
                            }else {
                                pagerIndex = (pagerIndex + 1) % listBoyoung.size
                                changeImage(pagerIndex)
                            }
                            mHasPerformedLongPress = false
                            return true
                        }
                    }
                return true
            }
        })
        viewPrev.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                pagerIndex = (pagerIndex - 1 + listBoyoung.size) % listBoyoung.size
                changeImage(pagerIndex)
            }
        })
        viewNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                pagerIndex = (pagerIndex + 1) % listBoyoung.size
                changeImage(pagerIndex)
            }
        })
        // long click 시 애니메이션 조작
        ivPagerImage.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                CustomLog.d("longClick")
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
        animScaleImage.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
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

    /**
     * 하나의 viewpager 안에서 이미지와 텍스트 애니메이션, 변환하는 함수
     * skeleton / llArticle / tsArticle / paginator
     */
    private fun changeImage(index: Int) {
        Picasso.with(context).load(resources.getIdentifier(listBoyoung[index].imageUri, "drawable", activity!!.packageName)).into(ivPagerImage, object : com.squareup.picasso.Callback {
            //다른 곳에 놓으면 이미지 로딩할 요소가 null 상태
            val skeletonScreen: SkeletonScreen = Skeleton.bind(llArticle)
                    .load(R.layout.item_skeleton)
                    .duration(2000)
                    .show()!!

            //로딩 후 실제 로직
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onSuccess() {
                skeletonScreen.hide()
                /**
                 * 애니메이션으로 설정
                 */
                tsTitle.setCurrentText(listBoyoung[index].title)
                tsArticle.setCurrentText(listBoyoung[index].overView)
                val animTextFadeIn = AnimationUtils.loadAnimation(context, R.anim.anim_fade_in_text)
                val animTextFadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
                animTextFadeIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        tsTitle.setText(listBoyoung[index].title)
                        tsArticle.setText(listBoyoung[index].overView)
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
//                startAnimation(ivPagerImage)
                //         tsTitle.startAnimation(animTextFadeIn)
                //         tsArticle.startAnimation(animTextFadeIn)

//                tsTitle.setFactory(object : ViewSwitcher.ViewFactory{
//                    override fun makeView(): View {
//                        var tvInject = TextView(baseContext)
//                        tvInject.setTextSize(30F)
//                        tvInject.setText("asdfasdf")
//                        return tvInject
//                    }
//                })

                animTextFadeOut.duration = 800

                tsArticle.inAnimation = animTextFadeIn
                tsArticle.outAnimation = animTextFadeOut
//                animLottie.setOnClickListener(object : View.OnClickListener {
//                    override fun onClick(v: View?) {
//                        count++
//                        if (count % 2 == 0) {
//                            tsArticle.setText("Learn android with examples");
//                        } else {
//                            tsArticle.setText("Welcome to android tutorials");
//                        }
//                    }
//                })
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
                paginator.findViewWithTag<Button>(index).clearAnimation()
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
//        val skeletonScreen: SkeletonScreen = Skeleton.bind(llArticle)
//                .load(R.layout.item_skeleton)
//                .duration(2000)
//                .show()!!
        CustomLog.d(isVisibleToUser.toString())
        if (isResumed())
            if (isVisibleToUser == true) {

                startAnimation(ivPagerImage)
//            skeletonScreen.hide()
            } else {
                stopAnimation(ivPagerImage)
//                skeletonScreen.show()
            }
    }
}

