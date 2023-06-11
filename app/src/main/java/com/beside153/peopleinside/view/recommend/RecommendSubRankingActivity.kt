package com.beside153.peopleinside.view.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.beside153.peopleinside.R
import com.beside153.peopleinside.databinding.ActivityRecommendSubRankingBinding
import com.beside153.peopleinside.model.RankingItem
import com.beside153.peopleinside.util.addBackPressedCallback
import com.beside153.peopleinside.util.setCloseActivityAnimation

class RecommendSubRankingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendSubRankingBinding
    private val rankingAdpater = RankingRecyclerViewAdapter(::onRankingItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recommend_sub_ranking)

        addBackPressedCallback()

        @Suppress("MagicNumber")
        val rankingList = listOf(
            RankingItem(
                1,
                "1",
                "어느 날 우리 집 현관으로 멸망이 들어왔다.",
                "이 드라마는 도전적이고 흥미진진한 플롯이었어.최대 2줄처리 필요합니다. 참고 부탁...",
                "전체 4.3점",
                "ISTJ 4.5점"
            ),
            RankingItem(
                2,
                "2",
                "그 해 우리는",
                "이 드라마는 도전적이고 흥미진진한 플롯이었어.이 드라마는 도전적이고 흥미...",
                "전체 4.3점",
                "ISTJ 4.5점"
            ),
            RankingItem(
                3,
                "3",
                "브람스를 좋아하세요?",
                "이 드라마는 도전적이고 흥미진진한 플롯이었어.최대 2줄처리 필요합니다. 참고 부탁...",
                "전체 4.3점",
                "ISTJ 4.5점"
            ),
            RankingItem(
                4,
                "1",
                "어느 날 우리 집 현관으로 멸망이 들어왔다.",
                "이 드라마는 도전적이고 흥미진진한 플롯이었어.최대 2줄처리 필요합니다. 참고 부탁...",
                "전체 4.3점",
                "ISTJ 4.5점"
            ),
            RankingItem(
                5,
                "2",
                "그 해 우리는",
                "이 드라마는 도전적이고 흥미진진한 플롯이었어.이 드라마는 도전적이고 흥미...",
                "전체 4.3점",
                "ISTJ 4.5점"
            )
        )

        binding.subRankingRecyclerView.apply {
            adapter = rankingAdpater
            layoutManager = LinearLayoutManager(this@RecommendSubRankingActivity)
            addItemDecoration(DividerItemDecoration(this@RecommendSubRankingActivity, LinearLayoutManager.VERTICAL))
        }
        rankingAdpater.submitList(rankingList)

        binding.backImageButton.setOnClickListener {
            finish()
            setCloseActivityAnimation()
        }
    }

    private fun onRankingItemClick(item: RankingItem) {
        Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RecommendSubRankingActivity::class.java)
        }
    }
}