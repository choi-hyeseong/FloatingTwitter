# FloatingTwitter
![화면 캡처 2024-12-20 215955](https://github.com/user-attachments/assets/57927335-df7d-4c91-abbe-05631ebf145f)

페이스북의 버블처럼 화면위에서 알림 띄워주는 앱

### WARN
2023년 API 유료화로 인해 거의 작동하지 않습니다. (무료 API 범위 감소 및 지원 종료)

### ETC
2024년 리팩토링을 수행하긴 했습니다. 물론 API 문제가 있어서 정상적인 작동이 어렵습니다. (아이콘 표시는 잘댐)

### Trouble Shooting

1. Token 저장시 Bearer 붙인채로 저장했다가 API Client와 충돌생겨서 평범하게 저장 후 Retrofit 사용시에만 붙이기
2. Retrofit의 baseURL은 /로 끝나야 하는데 query문구가 있는 DM endpoint는 적용되지 않아 @URL 사용해서 변경
3. Overlay Service는 MVVM에서 뷰에 해당되는데 DAO로 접근했었으나 수정함 (DAO 로써 사용가능한거는 GPS, 블루투스등등 정보 가져올때만!!)
4. PreferenceDataStore를 사용했으나 제네릭부분으로 인해 Class Cast Exception이 발생함. Any로 받아서 안전하게 캐스팅 하려 했지만 여전히 오류는 간간히 발생 (String 키값에 Int로 조회등등 -> 올바르게 접근하면 문제 없음, 제네릭값이 런타임에 사라져서 그런것으로 보임
5. 트위터 API Lib가 Java EE관련 API의 클래스 (GenericType)을 사용하는데 안드로이드에선 미지원. 해당 오류는 API 호출과정에서 exception 발생할때 사용되므로 network inspection 이용해서 오류 해결 후 해당 에러 처리 로직으로 이동하지 않게 해서 최대한 방지



:)