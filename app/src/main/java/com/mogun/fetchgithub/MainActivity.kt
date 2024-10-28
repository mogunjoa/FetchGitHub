package com.mogun.fetchgithub

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.mogun.fetchgithub.databinding.ActivityMainBinding
import com.mogun.fetchgithub.model.Repo
import com.mogun.fetchgithub.model.UserDto
import com.mogun.fetchgithub.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val githubService = retrofit.create(GithubService::class.java)

        binding.searchRepo.setOnClickListener {
            githubService.listRepos("square").enqueue(object : Callback<List<Repo>> {
                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    Log.e("[RES:::]", "SEARCH_REPO:::${response.body().toString()}")
                }

                override fun onFailure(call: Call<List<Repo>>, response: Throwable) {
                    Log.e("[Fail:::]", response.toString())
                }
            })
        }

        binding.searchUser.setOnClickListener {
            githubService.searchUsers("squar").enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    Log.e("[RES:::]", "SEARCH_USER:::${response.body().toString()}")
                }

                override fun onFailure(call: Call<UserDto>, response: Throwable) {
                    Log.e("[Fail:::]", response.toString())
                }
            })
        }
    }
}