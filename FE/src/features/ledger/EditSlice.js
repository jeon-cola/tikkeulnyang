import { createSlice } from "@reduxjs/toolkit";
import { UNSAFE_SingleFetchRedirectSymbol } from "react-router-dom";

// 편집 모드 상태를 관리하는 리덕스 슬라이스
const editSlice = createSlice({
  name: "edit",
  initialState: {
    editMode: false, // 기본적으로 편집 모드는 꺼져 있음
  },
  reducers: {
    // 편집 모드 토글 액션
    toggleEditMode: (state) => {
      state.editMode = !state.editMode;
    },
    // 편집 모드 설정 액션
    setEditMode: (state, action) => {
      state.editMode = action.payload;
    },
  },
});

// 액션 생성자 및 리듀서 내보내기
export const { toggleEditMode, setEditMode } = editSlice.actions;
export default editSlice.reducer;
