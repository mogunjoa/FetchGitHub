package com.mogun.fetchgithub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mogun.fetchgithub.adapter.RepoAdapter
import com.mogun.fetchgithub.databinding.ActivityRepoBinding
import com.mogun.fetchgithub.model.Repo
import com.mogun.fetchgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RepoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepoBinding
    private lateinit var repoAdapter: RepoAdapter

    // 페이징 처리 관련
    private var page = 0
    private var hasMore = true

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: return
        binding.usernameTextView.text = username

        repoAdapter = RepoAdapter(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.htmlUrl))
            startActivity(intent)
        })

        val linearLayoutManager = LinearLayoutManager(this)

        binding.repoRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = repoAdapter
        }

        binding.repoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()   // 마지막 아이템

                if (lastVisiblePosition >= totalItemCount - 1 && hasMore) {
                    page += 1
                    listRepo(username, page)
                }
            }
        })

        listRepo(username, 0)
    }

    private fun listRepo(username: String, page: Int) {
        val githubService = retrofit.create(GithubService::class.java)

        githubService.listRepos(username, page).enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.e("[RES:::]", "SEARCH_REPO:::${response.body().toString()}")

                // 추가 조회 가능 여부
                hasMore = response.body()?.count() == 30

                // 조회 시 기존 리스트에 데이터 추가
                repoAdapter.submitList(repoAdapter.currentList + response.body().orEmpty())
            }

            override fun onFailure(call: Call<List<Repo>>, response: Throwable) {
                Log.e("[Fail:::]", response.toString())
            }
        })
    }
}