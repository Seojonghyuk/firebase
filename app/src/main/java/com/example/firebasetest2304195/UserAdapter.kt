package com.example.firebasetest2304195

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetest2304195.databinding.UserLayoutBinding

class UserAdapter(val context: Context, val userList:MutableList<User>):RecyclerView.Adapter<UserAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size


    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val user = userList.get(position)
        val binding = holder.binding
        binding.tvkey.text = user.userKey
        binding.bntName.text = user.userName
        binding.btnAge.text = user.userAge
        binding.btnNo.text = user.userNo

        binding.root.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("user",user)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }
    class CustomViewHolder(val binding: UserLayoutBinding):RecyclerView.ViewHolder(binding.root)
}