<details>
    <summary><h3 style="display: inline; margin-left: 5px;">gamification</h3></summary>
    # CSS만을 사용해서 애니메이션 효과 없이, 단순히 여러 Layer로 스킨 입히기 기능 구현

## 개요

- 해당 기능을 구현하기 위해서 가장 간단하고 별도의 라이브러리를 설치하지 않고 하는 방법은 준비한 이미지를 CSS로 여러 Layer로 쌓는 방법이다.
- 하지만 만약 여기에 생동감을 주고 싶거나 애니메이션 효과를 주고 싶다면, anime.js나 GSAP를 써야 한다.
- GSAP를 추천하는 이유는, 해당 라이브러리는 구글, 페이스북등 많은 기업에서 실제로 쓰고 있는 라이브러리이고, 실제로 가장 널리 쓰이기 때문이다. 레퍼런스도 가장 많다.

---

## 1. 이미지 준비

### 이미지 형식 준비

- **PNG 형식**: 투명 배경을 지원하므로 옷 레이어링에 가장 적합
- **해상도**: 일반적으로 2x 해상도(레티나 디스플레이 지원)
- **파일 구조**:
  - 기본 캐릭터 이미지(body.png)
  - 각 의상 아이템별 투명 배경 이미지(hat.png, shirt.png, pants.png 등)

### 이미지 준비 시 고려사항

- 모든 이미지는 동일한 캔버스 크기를 유지해야 함
- 의상 아이템은 캐릭터 위에 올바르게 정렬되도록 위치 조정
- 각 의상 파츠는 캐릭터 신체의 같은 위치에 맞도록 제작

---

## 2. 폴더 구조 예시

```txt
src/
├── assets/
│   ├── character/
│   │   └── base.png
│   ├── hats/
│   │   ├── hat1.png
│   │   ├── hat2.png
│   │   └── ...
│   ├── shirts/
│   │   └── ...
│   └── pants/
│       └── ...
├── components/
│   ├── Character.js
│   ├── ItemSelector.js
│   └── ...
├── App.js
└── ...
```

## 3. 구현 단계

- 옷 아이템 데이터 구조 설정

```javascript
// src/data/items.js
export const items = {
  hats: [
    { id: "hat1", name: "모자 1", src: "/assets/hats/hat1.png" },
    { id: "hat2", name: "모자 2", src: "/assets/hats/hat2.png" },
    // ...
  ],
  shirts: [
    { id: "shirt1", name: "셔츠 1", src: "/assets/shirts/shirt1.png" },
    // ...
  ],
  pants: [
    // ...
  ],
};
```

- 캐릭터 컴포넌트 생성

```jsx
// src/components/Character.js
import React from "react";
import "./Character.css";

const Character = ({ selectedItems }) => {
  const { hat, shirt, pants } = selectedItems;

  return (
    <div className="character-container">
      <img
        src="/assets/character/base.png"
        className="character-base"
        alt="Character"
      />
      {hat && <img src={hat.src} className="character-hat" alt="Hat" />}
      {shirt && <img src={shirt.src} className="character-shirt" alt="Shirt" />}
      {pants && <img src={pants.src} className="character-pants" alt="Pants" />}
    </div>
  );
};

export default Character;
```

- CSS로 이미지 레이어링 설정

```css
/* src/components/Character.css */
.character-container {
  position: relative;
  width: 300px;
  height: 500px;
}

.character-base,
.character-hat,
.character-shirt,
.character-pants {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* Z-index로 레이어 순서 조정 */
.character-base {
  z-index: 1;
}
.character-pants {
  z-index: 2;
}
.character-shirt {
  z-index: 3;
}
.character-hat {
  z-index: 4;
}
```

- 아이템 선택 컴포넌트 생성

```jsx
// src/components/ItemSelector.js
import React from "react";
import { items } from "../data/items";
import "./ItemSelector.css";

const ItemSelector = ({ category, onSelect }) => {
  return (
    <div className="item-selector">
      <h3>{category.charAt(0).toUpperCase() + category.slice(1)}</h3>
      <div className="items-grid">
        {items[category].map((item) => (
          <div
            key={item.id}
            className="item-option"
            onClick={() => onSelect(category, item)}
          >
            <img src={item.src} alt={item.name} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default ItemSelector;
```

- 메인 App 컴포넌트에서 상태 관리(App.js가 아니라, 다른 컴포넌트여도 OK)

```jsx
// src/App.js
import React, { useState } from "react";
import Character from "./components/Character";
import ItemSelector from "./components/ItemSelector";
import "./App.css";

function App() {
  const [selectedItems, setSelectedItems] = useState({
    hat: null,
    shirt: null,
    pants: null,
  });

  const handleItemSelect = (category, item) => {
    setSelectedItems((prev) => ({
      ...prev,
      [category]: item,
    }));
  };

  return (
    <div className="app">
      <div className="character-view">
        <Character selectedItems={selectedItems} />
      </div>
      <div className="selectors">
        <ItemSelector category="hats" onSelect={handleItemSelect} />
        <ItemSelector category="shirts" onSelect={handleItemSelect} />
        <ItemSelector category="pants" onSelect={handleItemSelect} />
      </div>
    </div>
  );
}

export default App;
```

</details>

<details>
    <summary><h3 style="display: inline; margin-left: 5px;">반응형 웹</h3></summary>
    # 개요
- 대부분의 최신 웹 프로젝트는 "모바일 우선(Mobile-First)" 반응형 디자인을 채택한다.
	1. 먼저 모바일 화면에 최적화된 디자인을 만들고
	2. 미디어 쿼리를 사용해 화면이 커질 때 레이아웃을 확장
	3. 컴포넌트 기반 접근법으로 다양한 화면 크기에서 재사용 가능한 요소 설계

---

# calc()

```css
property: calc(expression);
```

예시)
위의 예시 `width: calc(50% - 20px);`가 사용된 실제 상황 :

```css
.card-container {
  display: flex;
  flex-wrap: wrap;
}

.card {
  width: calc(50% - 20px);
  margin: 0 10px 20px;
}
```

이 경우, 각 카드는:

- 컨테이너 너비의 50%를 차지하지만, 20픽셀을 뺀 크기.
- 왼쪽과 오른쪽에 각각 10픽셀의 마진이 있다(총 20픽셀).

이런 방식으로, 두 개의 카드가 완벽하게 한 줄에 맞게 배치된다. 카드 하나의 실제 차지하는 공간은 "너비 + 좌우 마진"이므로, `(50% - 20px) + 20px = 50%`가 된다. 두 카드를 합치면 정확히 컨테이너의 100% 너비를 차지하게 된다.

---

# clamp(), min(), max()

- `clamp` : 최솟값, 기본값, 최댓값을 지정하여 범위 내에서 크기를 자동 조절

```css
.container {
  width: clamp(300px, 80%, 1200px);
  /* 최소 300px, 기본적으로 80%, 최대 1200px */
}
```

- `min(), max()` : min은 제공된 값 중 더 작은 값을 선택, max()는 제공된 값중 더 큰 값을 선택 ```

```css
.column {
  width: min(600px, 50%);
  /* 600px과 50% 중 더 작은 값 */
}

.button {
  font-size: max(16px, 1vw);
  /* 16px과 1vw 중 더 큰 값 (텍스트가 너무 작아지지 않도록) */
}
```

---

# aspect-ratio

- 요소의 가로세로 비율을 유지

```css
.video-container {
  width: 100%;
  aspect-ratio: 16 / 9;
}

.profile-image {
  width: 100%;
  max-width: 300px;
  aspect-ratio: 1 / 1; /* 정사각형 */
}
```

</details>

<details>
    <summary><h3 style="display: inline; margin-left: 5px;">피그마 기초용어, 단축키</h3></summary>

    # 프레임, 레이어, Section

- Section > Frame (섹션은 프레임의 모임)
- 레이어 : 층
- Secion 하나가 곧 하나의 Page인것인가?

# 단축키

- ctrl+ D : 복제
- alt + G : 그룹화
- alt + ctrl + G : 그룹화 풀기
- shift + S : Seciont 묶어주기
- shift + G : 그리드 보여주기, 숨기기
- R 누르고 드래그 : 사각형
- F 누르고 드래그 : 프레임
- (여러 요소 선택 후) shift + A : 오토 레이아웃 설정
-

# unsplash

- 무료 이미지 적용

---

# 2. 와이어프레임, 프로토타입 작업시 레이아웃 적용 방법

와이어프레임이나 프로토타입 작업 시 그리드 설정은 작업의 성격과 단계에 따라 다르게 접근합니다:

1. 초기 와이어프레임 단계

- 그리드 없이 자유롭게 레이아웃 구상
- 대략적인 요소 배치와 구조 설계
- Auto Layout만으로도 충분한 경우가 많음

1. 상세 디자인 단계

- 그리드 시스템 적용
- 정확한 간격과 정렬 설정
- 반응형 레이아웃 고려

실무 워크플로우 예시:

1. 러프한 와이어프레임
   - Auto Layout으로 빠르게 구성
   - 기본적인 구조와 흐름 확인
2. 디자인 시스템 설정
   - 그리드 시스템 설정
   - 컴포넌트 규칙 정의
   - 간격과 정렬 기준 설정
3. 상세 디자인
   - 정의된 그리드에 맞춰 디자인 조정
   - 컴포넌트 재사용 및 정렬

즉, 초기에는 그리드 없이 자유롭게 작업하고, 디자인이 구체화되는 단계에서 그리드를 적용하는 것이 일반적이다.

---

# 3. Frame과 Group의 역할 차이

역할과 목적 중심으로 :

## Frame의 역할/목적:

- 레이아웃 구조 정의

  - Auto Layout 적용 가능
  - Grid 시스템 적용 가능
  - 고정된 크기와 배경 설정 가능
  - 독립적인 스크롤 영역으로 활용 가능

- 컴포넌트 설계
  - 명확한 경계를 가진 UI 요소 생성
  - 재사용 가능한 컴포넌트의 기본 단위
  - overflow 처리 가능

## Group의 역할/목적:

- 간단한 요소 묶음

  - 여러 레이어를 함께 관리하기 위한 용도
  - 선택과 이동을 편리하게 하기 위한 그룹화
  - 임시적인 그룹화에 적합

- 유연한 크기 관리
  - 내부 요소들의 크기에 따라 자동으로 조절
  - 별도의 제약 없이 자유로운 요소 배치

---

# Frame? Auto Layout? - Fix, Hug, Fill의 차이

---

# Grid 시스템

## Full Screen을 위한 기본 설정

- Count: 12, Type: Stretch, width: auto, Margin: 24, Gutter 24

## 모바일

- Count: 4, Type: Stretch, width: auto, Margin: 24, Gutter: 24

</details>
