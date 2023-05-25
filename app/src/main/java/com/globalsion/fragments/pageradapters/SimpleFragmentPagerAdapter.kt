package com.globalsion.fragments.pageradapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/** Paging adapter for [android.support.v4.view.ViewPager] */
class SimpleFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val pages = ArrayList<Pair<String, CreateFragmentCallback>>()
    private val initialized = ArrayList<Pair<Int, Fragment>>()
    /**
     * Add fragment to the adapter
     * (Note: only works before set the adapter to [android.support.v4.view.ViewPager])
     * @param title Page title
     * @param callback Delegate to create fragment
     **/
    fun addItem(title: String, callback: CreateFragmentCallback) {
        pages.add(Pair(title, callback))
    }
    /**
     * Add fragment to the adapter
     * (Note: only works before set the adapter to [android.support.v4.view.ViewPager])
     * @param position Position to insert
     * @param title Page title
     * @param callback Delegate to create fragment
     **/
    fun addItem(position: Int, title: String, callback: CreateFragmentCallback) {
        pages.add(position, Pair(title, callback))
    }
    /**
     * Add fragment to the adapter.
     * (Note: works before set the adapter to [android.support.v4.view.ViewPager])
     * @param title Page title
     * @param callback Delegate to create fragment
     **/
    fun addItem(title: String, callback: (position: Int) -> Fragment) {
        pages.add(Pair(title, object: CreateFragmentCallback {
            override fun createFragment(position: Int): Fragment {
                return callback(position)
            }
        }))
    }
    /**
     * Add fragment to the adapter.
     * (Note: only works before set the adapter to [android.support.v4.view.ViewPager])
     * @param position Position to insert
     * @param title Page title
     * @param callback Delegate to create fragment
     **/
    fun addItem(position: Int, title: String, callback: (position: Int) -> Fragment) {
        pages.add(position, Pair(title, object: CreateFragmentCallback {
            override fun createFragment(position: Int): Fragment {
                return callback(position)
            }
        }))
    }
    /**
     * Remove fragment from the adapter.
     * (Note: only works before set the adapter to [android.support.v4.view.ViewPager])
     * @param position Position to remove
     **/
    fun removeItem(position: Int) {
        pages.removeAt(position)
    }
    /**
     * Get the fragment from the adapter.
     * (Note: this will call the delegate to create fragment.)
     * @param position Position of the fragment.
     **/
    override fun getItem(position: Int) : Fragment {
        return pages[position].second.createFragment(position)
    }

    /**
     * Get fragment created by the view pager.
     * (Note: this will returns null if the fragment is detached or not initialized)
     * @param position Position of the fragment
     **/
    @Suppress("UNCHECKED_CAST")
    fun <T : Fragment> getInitializedItem(position: Int) : T? {
        return initialized
                .filter { it.first == position }
                .map { it.second }
                .singleOrNull() as T?
    }
    /**
     * Get the page count.
     **/
    override fun getCount(): Int {
        return pages.size
    }
    /**
     * Get the page title
     **/
    override fun getPageTitle(position: Int): CharSequence {
        return pages[position].first
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        initialized.add(Pair(position, fragment))
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val index = initialized.indexOfFirst { it.first == position }
        initialized.removeAt(index)
        super.destroyItem(container, position, `object`)
    }
    /** Interface to implement the create fragment callback. */
    interface CreateFragmentCallback {
        /** Callback for create fragment.
         * @param position Page position to create fragment
         **/
        fun createFragment(position: Int) : Fragment
    }
}