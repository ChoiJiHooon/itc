package com.example.itc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ManageAdapter(private val context: Context) : RecyclerView.Adapter<ManageAdapter.ViewHolder>() {

    var datas = mutableListOf<ManageData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_ex,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.tv_rv_name)
        private val txtAge: TextView = itemView.findViewById(R.id.tv_rv_age)
        private val txtPlace : TextView = itemView.findViewById(R.id.rv_place)
        private val txtSituate : TextView = itemView.findViewById(R.id.rv_situate)
        private val imgProfile: ImageView = itemView.findViewById(R.id.img_rv_photo)

        fun bind(item: ManageData) {
            txtName.text = item.name
            txtAge.text = item.age.toString()
            txtPlace.text = item.place.toString()
            txtSituate.text = item.situate.toString()
            Glide.with(itemView).load(item.img).into(imgProfile)
        }
    }

}
data class ManageData(
    val name : String, val age : String, val img : String , val place : String , val situate : String

)