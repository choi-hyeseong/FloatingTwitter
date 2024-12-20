package com.comet.floatingtwitter.twitter.api.dto


/**
 * Twitter DM 정보 담는 DTO Class
 */
data class DirectMessageDTO(var data: ArrayList<Data>, var meta: Meta)

data class Data(var text: String, var id: String, var dmConversationId: String, var eventType: String, var senderId: String, var createdAt: String)

data class Meta(var resultCount: Int, var nextToken: String, var previousToken: String)