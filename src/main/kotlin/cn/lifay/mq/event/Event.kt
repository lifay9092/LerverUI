/* SPDX-License-Identifier: MIT */
package cn.lifay.mq.event

abstract class Event protected constructor(val id: String) {

//    companion object {
//        fun <E : Event?> publish(event: E) {
//            DefaultEventBus.getInstance().publish(event)
//        }
//    }
}