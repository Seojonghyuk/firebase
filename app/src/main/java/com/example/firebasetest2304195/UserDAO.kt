package com.example.firebasetest2304195

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class UserDAO {
    private var databaseReference:DatabaseReference? = null
    init {
        //실시간 데이타베이스 연결
        var db = FirebaseDatabase.getInstance()
        //user 테이블 생성객체 생각할것
        databaseReference = db.getReference("user")
    }
    //insert into user values(_,_,_,_)
    fun fbInsert(user:User?): Task<Void>{
        return databaseReference?.push()!!.setValue(user)

    }
    //select * from user
    fun UserSelect():Query?{
        return  databaseReference
    }
    //updata set user set userkey="~~~~",
    fun userUpdate(userKey:String,hashMap: HashMap<String,Any>):Task<Void>{
        return databaseReference!!.child(userKey).updateChildren(hashMap)
    }

    //delete from user where userkey = ?
    fun userDelete(userKey: String):Task<Void>{
        return databaseReference!!.child(userKey).removeValue()
    }

}