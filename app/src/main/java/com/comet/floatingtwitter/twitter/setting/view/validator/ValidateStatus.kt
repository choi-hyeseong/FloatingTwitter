package com.comet.floatingtwitter.twitter.setting.view.validator

/**
 * 검증 결과를 나타내는 클래스
 * @param field validate 되지 않은 필드를 나타냅니다. type이 ok인경우 비어있습니다.
 */
data class ValidateStatus(val type: ValidateResult, val field: List<String>)

enum class ValidateResult {
    OK, ERROR
}