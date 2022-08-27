package com.github.browsermovies.settings

import android.content.Context
import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.preference.Preference


public class PreferFragment: LeanbackPreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        var root = arguments.getString("root",null)
        var prefResId = arguments.getInt("preferenceResource")
        if (root == null) {
            addPreferencesFromResource(prefResId);
        } else {
            setPreferencesFromResource(prefResId, root);
        }

    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
//            val keys = arrayOf("prefs_listnum_key","prefs_sourcesnum_key","prefs_imgsize_key")
//            if (preference!!.key in keys) {
//                Toast.makeText(getActivity(), "Implement your own action handler.", Toast.LENGTH_SHORT).show();
//                return true;
//            }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onAttach(context: Context?) {
        //fragments.push(this);
        super.onAttach(context)
    }

    override fun onDetach() {
        //fragments.pop();
        super.onDetach()
    }


}