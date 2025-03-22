import { createSlice } from "@reduxjs/toolkit";

const challengeSlice = createSlice({
  name: "challenge",
  initialState: {
    // 초기 상태
    challengeType: 0,
  },
  reducers: {
    // 상태 변경 함수들
    setPage: (state, action) => {
      state.challengeType = action.payload;
    },
  },
});

export const { setPage } = challengeSlice.actions;
export default challengeSlice.reducer;
