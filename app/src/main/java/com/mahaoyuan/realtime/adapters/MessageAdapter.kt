package com.mahaoyuan.realtime.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mahaoyuan.realtime.R
import com.mahaoyuan.realtime.UserInfo
import com.mahaoyuan.realtime.models.Message


class MessageAdapter(private var msgList: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgFrom : TextView = view.findViewById(R.id.msg_from_text)
        val textMsg : TextView = view.findViewById(R.id.msg_content_text)
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgFrom : TextView = view.findViewById(R.id.msg_from_image)
        val imageView : ImageView = view.findViewById(R.id.msg_content_image)
    }

    inner class FileUploadViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgFrom : TextView = view.findViewById(R.id.msg_from_file_upload)
        val fileTitle : TextView = view.findViewById(R.id.msg_file_title_upload)
        val progressBar : ProgressBar = view.findViewById(R.id.uploading)
        val finish : TextView = view.findViewById(R.id.upload_finish)
    }

    inner class FileDownloadViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val msgFrom : TextView = view.findViewById(R.id.msg_from_file_download)
        val fileTitle : TextView = view.findViewById(R.id.msg_file_title_download)
        val downloadBtn : Button = view.findViewById(R.id.btn_download)
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        //show nothing
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return if (msg.type == "chat"){
            if ((msg.from == UserInfo.userEmail.value && msg.to[0] == UserInfo.chatTo.value) || (msg.from == UserInfo.chatTo.value  && msg.to[0] == UserInfo.userEmail.value)){
                when(msg.contentType){
                    "text" -> 1
                    "image" -> 2
                    "file" -> when (msg.from) {
                        UserInfo.userEmail.value -> 3
                        else -> 4
                    }
                    else -> 0
                }
            }else{
                0
            }
        }else{
            when(msg.contentType){
                "text" -> 1
                "image" -> 2
                "file" -> when (msg.from) {
                    UserInfo.userEmail.value -> 3
                    else -> 4
                }
                else -> 0
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_text, parent, false)
                TextViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_image, parent, false)
                ImageViewHolder(view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_file_upload, parent, false)
                FileUploadViewHolder(view)
            }
            4 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_file_download, parent, false)
                FileDownloadViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_empty, parent, false)
                EmptyViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = msgList[position]
        when(holder){
            is TextViewHolder -> {
                holder.textMsg.text = ("   " + msg.content)
                holder.msgFrom.text = (" " + msg.from)
            }
            is ImageViewHolder -> {
                holder.msgFrom.text = (" " + msg.from)
                //Log.i("mhy", msg.content.split(",")[1])

                val decodedString = Base64.decode(msg.content.split(",")[1], Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                holder.imageView.setImageBitmap(decodedByte)
            }
            is FileUploadViewHolder -> {
                holder.msgFrom.text = (" " + msg.from)
                holder.fileTitle.text = if (msg.content.length > 8) {
                    "  " + msg.content.subSequence(0, 7).toString() + "..."
                } else {
                    "  " + msg.content
                }
                if (msg.fileSize > 0){
                    //do nothing
                }else{
                    holder.progressBar.visibility = View.GONE
                    holder.finish.visibility = View.VISIBLE
                }
            }
            is FileDownloadViewHolder -> {
                holder.msgFrom.text = (" " + msg.from)
                holder.fileTitle.text = if (msg.content.length > 8) {
                    "  " + msg.content.subSequence(0, 7).toString() + "..."
                } else {
                    "  " + msg.content
                }
                holder.downloadBtn.setOnClickListener {
                    downloadFile(it.context,UserInfo.host + "/UploadFiles/" + msg.from + "/" + msg.content)
                }
            }
            is EmptyViewHolder -> {

            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount() = msgList.size


    @SuppressLint("ShowToast")
    private fun downloadFile(context: Context, url : String) {
        Log.i("mhy","$context download $url")
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            Toast.makeText(context,"please open permission",Toast.LENGTH_SHORT)
            return
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/") + 1))
        downloadManager!!.enqueue(request)
    }

}