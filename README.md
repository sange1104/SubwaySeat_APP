# Components
    1. MobileApp - 모바일 앱 개발
    2. ServerApp - 실시간 지하철 정보를 불러오기 위한 서버개발

# Convention
### 변수명, 함수명
    1. global한 변수의 경우 전체 대문자
    2. local한 변수의 경우 전체 소문자
    3. 함수명의 경우 _(언더바)없이, 대문자로 구분
        ex) updateData()

### 분할 정복
    1. 하나의 함수에 두개 이상의 기능 넣지 않기 (분할)
    2. 한개의 모듈에는 관련있는 함수끼리만 묶기

### 주석
    함수나 변수에서 이해하기 어려운 곳의 경우 주석으로 설명달기

### 함수설명
    함수 정의 바로 밑에 """ """ 이걸로 처리하면
    나중에 클릭하면 바로 볼 수 있도록 나옴

# Commit Rule
    1. 함수 하나가 완성되면 "앱/모듈명/함수명 ~변경" 같은 식으로 커밋
    2. 기존의 것과 겹쳐서 변경이 필요하면 상의