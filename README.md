# 모션센서를 이용한 사용자 모션 인식과 그에 따른 데이터 분석 및 모델링에 관한 연구


### 개요 : 안드로이드 어플리케이션의 모션센서를 활용하여 사용자의 움직임을 시각화하여 화면에 보여준다.정확도를 측정하기 위해 MNIST 데이터 베이스로 훈련된 이미지 판별 서비스를 사용한다. 사용자마다 다른 신체 조건 등으로 인해 차이가 발생하고 이를 활용한 사용자 인증의 가능성을 예측해 본다.
 

### 연구 과정
1) 모션 센서 데이터 수집과 그래프 생성
안드로이드 어플리케이션을 이용하여 가속도 센서, 자이로 센서, 로테이션 센서, 오리엔테이션 센
서의 데이터를 csv파일로 저장했고 그 csv파일을 R 언어로 분석하였다.
R언어에 있는 scatterplot3d 라이브러리를 이용하여 3차원 좌표로 센서들을 나타내었다.

![3](https://user-images.githubusercontent.com/68365881/93417134-3f81f400-f8e2-11ea-8462-74a353268dca.png)


2) 모션 시각화 - 자바 프로그램
현재 시각화에 적용하는 알고리즘은 Orientation Sensor의 값을 직관적으로 2차원 평면에 표시하
는 방법으로, 방위를 나타내는 Orientation Sensor의 Z축에 대한 값과 X축에 대한 값을 사용하였다.
Orientation의 Z값은 모바일이 가리키는 방위를 측정 값으로 갖는다. 북쪽을 가리킬 때 0, 남쪽을
가리킬 때 180을 가지게 된다. 즉, 0에서 360사이의 값을 가지게 되는 것이다.
![2](https://user-images.githubusercontent.com/68365881/93417111-32fd9b80-f8e2-11ea-811e-04269a086d97.png)

3) 안드로이드 어플리케이션을 사용하여 수집한 모션 데이터를 자바 프로그램의 입력 값으로 넣
어 모션을 시각화한 이미지를 얻는다. 그리고 생성한 이미지를 28 x 28 사이즈로 만들어 텐서플로우를
사용한 파이썬 프로그램에 입력 값으로 넣었을 때 이미지가 어던 숫자인지 판별하여 모션의 시각화의
정확도를 측정할 수 있다.
![noname01](https://user-images.githubusercontent.com/68365881/93417070-17929080-f8e2-11ea-83aa-039ffd350154.png)


#### 프로젝트 목적
- 본 프로젝트의 기본적 연구 방향은 안드로이드 기반 모바일 기기에 포함되어 있는 모션 센
서를 이용하여 여러 가지 데이터를 수집 및 분석하는 것이다. 모션 센서의 종류는 세 가지로,
가속도 센서, 자이로 센서, 방향 센서이다. 각각의 센서는 측정 기기의 방향을 기준으로 하는 X
, Y , Z 축에 대한 출력값이 있다.
이 센서의 원리와 출력값이 가지는 의미를 파악하기 위해 여러 사용자의 동일 제스처에 대
한 결과 데이터를 모으고 분석한다. 모션 센서가 가지는 특징과 의미를 이용하여 특정 제스처
에 대한 결괏값을 분석하고 화면에 점으로 나타낸다. 이 점들의 모임으로 사용자의 제스처에
대한 대략적인 움직임을 시각화하여 확인할 수 있다. 특정 제스처에 대한 여러 사용자의 고유
데이터를 분석하여 사람마다 다른 행위적 특징을 발견한다.
안드로이드 모션 센서 데이터 분석 결과를 활용하여 사용자의 모션을 구분하고 판별하는
방법을 연구한다. 사용자의 행위적 특성을 구분하여 구분된 데이터의 특징을 가지고 사용자를
판별하는 기능을 제공한다.
사용자를 판별하기에 앞서 입력받은 모션 데이터를 분석하는 기능을 텐서플로우를 활용한
이미지 분석 시스템으로 구현한다. 파이썬으로 구현된 텐서플로우 시스템은 MNIST 데이터베이
스로 훈련되어 28*28크기의 이미지를 인식해 0~9의 숫자로 판별할 수 있다. 이렇게 구현된 이
미지 판별 시스템에 모션 센서 데이터 시각화의 결과로 만들어진 이미지 파일을 입력 값으로
넣어 어떤 숫자인지 판단할 수 있다.
최근 사용 중인 인증 방법인 비밀번호, 지문인식 그리고 홍채 인식에 대한 단점을 극복하는
방법을 탐구하는 것을 목적으로 하여, 비밀번호의 입력이 번거롭다는 단점과 생체 인식인 지문
인식이나 홍채 인식의 한번 노출되면 다시는 사용할 수 없는 단점을 극복한다. 사용자가 쉽고
