package com.comet.floatingtwitter.twitter.setting.model

/**
 * 버블 크기 & 컬러 정보
 * @property size 크기
 * @property mentionColor 멘션만 왔을때 색
 * @property directMessageColor dm만 왔을때 색
 * @property bothNotifyColor 둘다 알림 왔을때 색
 * @see Color.java
 */
data class SettingData(val size : Int, val mentionColor : String, val directMessageColor : String, val bothNotifyColor : String)