package com.comet.floatingtwitter.twitter.api.repository

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.comet.floatingtwitter.getClassName
import com.comet.floatingtwitter.twitter.api.dao.TwitterDao
import com.comet.floatingtwitter.twitter.api.dto.Data
import com.comet.floatingtwitter.twitter.api.dto.DirectMessageDTO
import com.comet.floatingtwitter.twitter.api.model.EventData
import com.comet.floatingtwitter.twitter.api.model.EventType
import com.comet.floatingtwitter.twitter.oauth.model.OAuthToken
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.message
import com.skydoves.sandwich.messageOrNull
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration

/**
 * @param interval 초단위 반복시간
 */
class TwitterAPIRepository(private val twitterDao: TwitterDao, private val interval: Int) : TwitterRepository {

    @Volatile // for thread sync
    private var isRunning: Boolean = false
    private var job: Job? = null // long-running, thread 쓰는게 좀더 좋긴함
    private var lastTweetId: String = "" // lastTweetId
    private var lastDMId : String = ""

    override suspend fun startAPIListening(user: User, token: OAuthToken, callback: (List<EventData>) -> Unit) {
        //이미 작동중인경우 리턴
        if (isRunning) return
        isRunning = true
        // IO Dispatcher pooling
        val events: MutableList<EventData> = mutableListOf()
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                // 동시에 async으로 호출해서 서로 유사한 시각에 api 호출하기
                val mentionCall = async { twitterDao.getMentionData(user.id, token) }
                val directMessageResponseCall = async { twitterDao.getDMData(token) }

                // 멘션 데이터
                val mention = mentionCall.await()
                if (mention.isSuccess) events.addAll(handleMention(mention.getOrThrow())) // throw 안됨
                else Log.w(
                    getClassName(), mention.exceptionOrNull()) // exception 알려줌

                // dm 데이터
                val directMessageResponse = directMessageResponseCall.await()
                directMessageResponse.suspendOnSuccess {
                    events.addAll(handleDm(this.data)) // 추가
                }.suspendOnFailure {
                    Log.w(getClassName(), this.message()) // 메시지 로깅
                }.suspendOnException {
                    Log.w(getClassName(), this.throwable) // exception 핸들
                }

                // 이벤트가 있는경우
                if (events.isNotEmpty()) {
                    callback(events)
                    events.clear()
                }

                delay(interval * 1000L)
            }
        }


    }

    // 이거 둘다 처리하는 로직이 유사한데..

    // dm 데이터 처리하기
    private fun handleDm(directMessages: DirectMessageDTO): List<EventData> {
        val result : MutableList<EventData> = mutableListOf()
        val data = directMessages.data
        val topMessage : Data = data[0]

        // 최초 init시
        if (lastDMId.isEmpty()) {
            lastDMId = topMessage.id
            return emptyList()
        }

        // 같은경우 리턴
        if (topMessage.id == lastDMId)
            return emptyList()

        // message index find
        val index = data.indexOfFirst { message -> message.id == lastDMId }
        if (index == -1)
            //찾지 못한경우
            result.add(EventData(EventType.DM, data.size)) // 모든 트윗을 다 알림으로 설정
        else
            //찾은경우 index가 밀려난만큼 멘션이 들어옴 -> 최신순 (1번째 인덱스 -> 0번째 dm이 설정됨)
            result.add(EventData(EventType.DM, index))
        lastDMId = topMessage.id // 최상단으로 설정
        return result
    }

    // mention 데이터 처리하기
    private fun handleMention(tweets: List<Tweet>): List<EventData> {
        val result: MutableList<EventData> = mutableListOf() // 리턴용 객체
        val topTweet: Tweet = tweets[0] //최상단 트윗
        // early return. 만약 id가 같거나 비어있는경우 id 초기화후 리턴
        if (lastTweetId.isEmpty()) {
            lastTweetId = topTweet.id // 최초 init시 id만 필요
            return emptyList()
        }
        if (topTweet.id == lastTweetId) return emptyList()

        // tweet index find
        val index = tweets.indexOfFirst { tweet -> tweet.id == lastTweetId }
        if (index == -1)
        //찾지 못한경우
            result.add(EventData(EventType.MENTION, tweets.size)) // 모든 트윗을 다 알림으로 설정
        else
        //찾은경우 index가 밀려난만큼 멘션이 들어옴 -> 최신순 (1번째 인덱스 -> 0번째 트윗이 작성됨)
            result.add(EventData(EventType.MENTION, index))
        lastTweetId = topTweet.id // 최상단으로 설정
        return result
    }

    override suspend fun stopAPIListening() {
        job?.cancel()
        isRunning = false
        job = null
        lastTweetId = ""
        lastDMId = ""
    }

    override suspend fun isRunning(): Boolean {
        return isRunning && job != null
    }

    // drawable을 쓰는것보단, 그냥 bitmap으로 return 해줘서 service에서 decode하는게 좋을듯. data layer에 android 패키지 안쓰는게 좋기도 해서
    // 현재는 디코딩 과정에서 exception도 wrapping하고 있기 때문에 편의성을 위해서라도 일단 현행유지
    override suspend fun getAvatarResource(user: User, token: OAuthToken): Result<Drawable> {
        val imageResponse = twitterDao.getAvatarResource(user, token)
        return kotlin.runCatching {
            val body = imageResponse.getOrThrow() // exception으로 반환하기
            val drawable = body.byteStream().use { // try with resource
                val bitmap = BitmapFactory.decodeStream(it) // decoding
                RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
                    .apply { cornerRadius = 360f }
            }
            drawable
        }
    }

    override suspend fun getUserInfo(token: OAuthToken): Result<User> {
        return twitterDao.getUserInfo(token)
    }
}