package com.comet.floatingtwitter.twitter.api.model

/**
 * 이벤트 콜백을 처리하기 위한 데이터 클래스
 * @property type 입력된 이벤트 타입 (DM, 멘션)
 * @property amount 해당 이벤트가 몇개나 쌓였는지
 */
data class EventData(val type : EventType, val amount : Int)