package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.ARGUMENT_ID
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.friendFragment
import com.github.drunlin.webappbox.common.showDialog
import com.github.drunlin.webappbox.data.UserAgent
import com.github.drunlin.webappbox.model.UserAgentManager
import kotlinx.android.synthetic.main.item_user_agent.view.*
import javax.inject.Inject

class UserAgentsFragment() : ListFragment<UserAgent, UserAgentManager>() {
    @Inject override lateinit var manager: UserAgentManager

    override val titleResId = R.string.user_agents
    override val itemResId = R.layout.item_user_agent

    private var currentId: Long
        set(value) { arguments!!.putLong(ARGUMENT_ID, value) }
        get() = arguments!!.getLong(ARGUMENT_ID)

    private val listener: OnChangeListener get() = friendFragment as OnChangeListener

    constructor(id: Long?, listener: Fragment) : this() {
        arguments = Bundle()
        currentId = id ?: -1
        friendFragment = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)
    }

    override fun ListFragment<UserAgent, UserAgentManager>.ViewHolder.onItemCreated() {
        itemView.button.setOnClickListener {
            if (data?.id != currentId) {
                currentId = data!!.id
                listener.onUserAgentChange(data)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun ListFragment<UserAgent, UserAgentManager>.ViewHolder.onItemClick() {
        showDialog(UserAgentEditorFragment(data!!.id))
    }

    override fun ListFragment<UserAgent, UserAgentManager>.ViewHolder.onBindItem(data: UserAgent) {
        binding.setVariable(BR.userAgent, data)
        binding.setVariable(BR.checked, data.id == currentId)
    }

    override fun onInsert() {
        showDialog(UserAgentEditorFragment())
    }

    override fun onRemove() {
        super.onRemove()

        if (selectedSet!!.contains(currentId)) listener.onUserAgentChange(null)
    }

    interface OnChangeListener {
        fun onUserAgentChange(userAgent: UserAgent?)
    }
}
