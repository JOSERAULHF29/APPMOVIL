package com.example.profinal.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.profinal.R
import com.example.profinal.databinding.ActivityIntroBinding
import com.example.profinal.databinding.ActivityMainBinding


class Intro : AppCompatActivity() {


    private  lateinit var binding: ActivityIntroBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btningreso.setOnClickListener {

            val int=Intent(this,login::class.java)
            startActivity(int)


        }


    }
}