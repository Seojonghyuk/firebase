package com.example.firebasetest2304195

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasetest2304195.databinding.ActivityListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ListActivity : AppCompatActivity() {
    lateinit var binding: ActivityListBinding
    lateinit var adapter: UserAdapter
    lateinit var userList: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userList = mutableListOf()
        adapter = UserAdapter(this, userList)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter
        getFireBaseUserList()
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 여기서부터 우리가 구현해야 될 내용
                val position = viewHolder.bindingAdapterPosition
                val userDAO = UserDAO()
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        val key = userList.get(position).userKey
                        userDAO.userDelete(key).addOnSuccessListener {
                            Toast.makeText(this@ListActivity, "삭제 성공", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener {
                            Toast.makeText(this@ListActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                        }

                    }
                }// end of when
            }//end of onSwiped

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(Color.BLUE)
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_outline_24)
                    .addSwipeLeftLabel("delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.recyclerview)//end of ItemTouchHelper

    }

    private fun getFireBaseUserList() {
        val userDAO = UserDAO()
        userDAO.UserSelect()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                // snapshot == RecordSet
                for (dataSnapShot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)
                    user?.userKey = dataSnapShot.key.toString()
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListActivity", "파이어베이스에서 데이터 로딩 실패 ${error.toString()}")
                Toast.makeText(
                    this@ListActivity,
                    "파이어베이스에서 데이터 로딩 실패 ${error.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}



