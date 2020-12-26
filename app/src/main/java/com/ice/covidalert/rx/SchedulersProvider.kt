package com.ice.covidalert.rx

import io.reactivex.Scheduler

interface SchedulersProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
    fun computation(): Scheduler
}