/* SPDX-License-Identifier: MIT */
package cn.lifay.mq.event

import cn.lifay.mq.EventBusId

abstract class Event protected constructor(val id: EventBusId) {

//    companion object {
//        fun <E : Event?> publish(event: E) {
//            DefaultEventBus.getInstance().publish(event)
//        }
//    }
}