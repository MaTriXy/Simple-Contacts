package com.simplemobiletools.contacts.dialogs

import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.commons.views.MyAppCompatCheckbox
import com.simplemobiletools.contacts.R
import com.simplemobiletools.contacts.activities.SimpleActivity
import com.simplemobiletools.contacts.extensions.config
import com.simplemobiletools.contacts.helpers.ContactsHelper
import com.simplemobiletools.contacts.models.Group
import kotlinx.android.synthetic.main.dialog_select_groups.view.*
import kotlinx.android.synthetic.main.item_checkbox.view.*
import kotlinx.android.synthetic.main.item_textview.view.*
import java.util.*

class SelectGroupsDialog(val activity: SimpleActivity, val selectedGroups: ArrayList<Group>, val callback: (newGroups: ArrayList<Group>) -> Unit) {
    private val view = activity.layoutInflater.inflate(R.layout.dialog_select_groups, null) as ViewGroup
    private val checkboxes = ArrayList<MyAppCompatCheckbox>()
    private val groups = ContactsHelper(activity).getStoredGroups()
    private val config = activity.config
    private val dialog: AlertDialog?

    init {
        groups.forEach {
            addGroupCheckbox(it)
        }

        addCreateNewGroupButton()

        dialog = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok, { dialog, which -> dialogConfirmed() })
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this)
                }
    }

    private fun addGroupCheckbox(group: Group) {
        activity.layoutInflater.inflate(R.layout.item_checkbox, null, false).apply {
            checkboxes.add(item_checkbox)
            item_checkbox_holder.setOnClickListener {
                item_checkbox.toggle()
            }
            item_checkbox.apply {
                isChecked = selectedGroups.contains(group)
                text = group.title
                tag = group.id
                setColors(config.textColor, config.primaryColor, config.backgroundColor)
            }
            view.dialog_groups_holder.addView(this)
        }
    }

    private fun addCreateNewGroupButton() {
        val newGroup = Group(0, activity.getString(R.string.create_new_group))
        activity.layoutInflater.inflate(R.layout.item_textview, null, false).item_textview.apply {
            text = newGroup.title
            tag = newGroup.id
            setTextColor(config.textColor)
            view.dialog_groups_holder.addView(this)
            setOnClickListener {
                CreateNewGroupDialog(activity) {
                    selectedGroups.add(it)
                    groups.add(it)
                    view.dialog_groups_holder.removeViewAt(view.dialog_groups_holder.childCount - 1)
                    addGroupCheckbox(it)
                    addCreateNewGroupButton()
                }
            }
        }
    }

    private fun dialogConfirmed() {
        val selectedGroups = ArrayList<Group>()
        checkboxes.filter { it.isChecked }.forEach {
            val groupId = it.tag as Long
            groups.firstOrNull { it.id == groupId }?.apply {
                selectedGroups.add(this)
            }
        }

        callback(selectedGroups)
    }
}
