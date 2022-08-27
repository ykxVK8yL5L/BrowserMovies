package com.github.browsermovies.settings


import android.annotation.SuppressLint
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.leanback.preference.LeanbackPreferenceFragment
import androidx.leanback.preference.LeanbackSettingsFragment
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceScreen
import com.github.browsermovies.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SysSettingsFragment : LeanbackSettingsFragment(), DialogPreference.TargetFragment{

    val fragments = Stack<Fragment>()

    override fun onPreferenceStartFragment( caller: PreferenceFragment?, pref: Preference?): Boolean {
        return false;
    }

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment(R.xml.prefs,null))
    }

    override fun onPreferenceStartScreen( caller: PreferenceFragment?, pref: PreferenceScreen? ): Boolean {
        val frag = buildPreferenceFragment(R.xml.prefs, pref!!.getKey());
        startPreferenceFragment(frag);
        return true;
    }

    override fun findPreference(key: CharSequence?): Preference {
        return (fragments.peek() as PreferFragment).findPreference(key);
    }

    private fun buildPreferenceFragment(preferenceResId:Int, root:String?):PreferFragment{
        val fragment = PreferFragment()
        var args = Bundle()
        args.putInt("preferenceResource", preferenceResId);
        args.putString("root", root);
        fragment.setArguments(args);
        return fragment;
    }




}
