package com.mfarrelathaillahnugrohojsleepmn.imeonapps.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.TaskcardBinding

class ToDoAdapter(private val list:MutableList<ToDoData>) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {


    private var listener:ToDoAdapterClicksInterface? = null

    fun setListener(listener:ToDoAdapterClicksInterface) {
        this.listener = listener

    }

    inner class ToDoViewHolder(val binding:TaskcardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = TaskcardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]){
                binding.taskcardName.text = this.taskName

                binding.taskcardDate.text = this.taskDate

                binding.taskcardDelete.setOnClickListener {
                    listener?.onDeleteTaskBtnClick(this)
                }

                binding.taskcardEdit.setOnClickListener {
                    listener?.onEditTaskBtnClick(this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteTaskBtnClick(toDoData: ToDoData)
        fun onEditTaskBtnClick(toDoData: ToDoData)
    }
}