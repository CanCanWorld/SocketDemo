package com.zrq.socketdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T:ViewBinding> : Fragment() {

    lateinit var mBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = providedViewBinding(inflater, container)
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initEvent()
    }

    abstract fun providedViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun initData()

    abstract fun initEvent()
}