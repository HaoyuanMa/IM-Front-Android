package com.mahaoyuan.realtime.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    val handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                1 ->  {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init UserInfo
        UserInfo.userEmail.value = ""
        setContentView(R.layout.activity_login)
        val loginButton : Button = findViewById(R.id.login)
        loginButton.setOnClickListener{

            login()
        }

    }

    private fun login() {
        var token : String? = ""
        thread {
            val email : EditText = findViewById(R.id.email)
            val password : EditText = findViewById(R.id.password)

            var jsonbody = JSONObject()
            jsonbody.put("Email",email.text.toString())
            jsonbody.put("Password",password.text.toString())
            val body = jsonbody.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                    .url("http://10.0.2.2:12165/Account/Login")
                    .addHeader("Content-Type","application/json")
                    .addHeader("Data-Type","text")
                    .post(body)
                    .build()
            val client = OkHttpClient()
            val call = client.newCall(request)
            var data : Response
            try {
                data = call.execute()
            }catch (e:Exception){
                Log.e("error",e.toString())
                throw (e)
            }
            token  = data.body?.string()
            if(data.code==200){
                UserInfo.token.postValue(token.toString())
                val msg = Message()
                msg.what = 1
                handler.sendMessage(msg)
            }
            token?.let { Log.i("mhy", it) }
        }
    }
}