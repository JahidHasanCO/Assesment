package dev.jahidhasanco.assesment.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.jahidhasanco.assesment.data.model.User
import dev.jahidhasanco.assesment.databinding.SingleUserItemBinding

class UserAdapter(private val userList: MutableList<User> = mutableListOf()) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var listener: OnUserClickListener? = null

    fun setOnItemClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUserList(list: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffUtilCallback(userList, list))
        userList.clear()
        userList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }


    inner class UserViewHolder(
        private val binding: SingleUserItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(user: User, listener: OnUserClickListener?) {
            binding.apply {
                name.text = user.name
                address.text = "Country: ${user.country}, City: ${user.city}"
                var skillsWithComma = ""
                user.skill.forEach {
                    skillsWithComma += "$it, "
                }
                skillsWithComma.dropLast(2)
                skills.text = skillsWithComma
                dob.text = user.dateOfBirth
                resume.text = user.resumeTitle
            }

            binding.edit.setOnClickListener {
                listener?.onItemEditClick(user)
            }
            binding.delete.setOnClickListener {
                listener?.onItemDeleteClick(user)
            }

            binding.show.setOnClickListener {
                listener?.onUserItemClick(user)
            }
            binding.resume.setOnClickListener {
                listener?.onItemResumeClick(user)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = SingleUserItemBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return UserViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, listener)
    }

    override fun getItemCount() = userList.size
}

interface OnUserClickListener {
    fun onUserItemClick(user: User)
    fun onItemDeleteClick(user: User)
    fun onItemEditClick(user: User)
    fun onItemResumeClick(user: User)
}