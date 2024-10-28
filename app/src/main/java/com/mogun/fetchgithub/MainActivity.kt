package com.mogun.fetchgithub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mogun.fetchgithub.adapter.UserAdapter
import com.mogun.fetchgithub.databinding.ActivityMainBinding
import com.mogun.fetchgithub.model.UserDto
import com.mogun.fetchgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var searchFor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userAdapter = UserAdapter(onClick = {
            val intent = Intent(this, RepoActivity::class.java)
            intent.putExtra("username", it.username)
            startActivity(intent)
        })

        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }

        val runnable = Runnable {
            searchUser()
        }

        binding.searchEditText.addTextChangedListener {
            searchFor = it.toString()
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 300)
        }
    }

    private fun searchUser() {
        val githubService = ApiClient.retrofit.create(GithubService::class.java)

        githubService.searchUsers(searchFor).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                Log.e("[RES:::]", "SEARCH_USER:::${response.body().toString()}")
                userAdapter.submitList(response.body()?.items)
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@MainActivity, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }
}