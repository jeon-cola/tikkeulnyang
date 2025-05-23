import { createSlice } from "@reduxjs/toolkit";

const challengeSlice = createSlice({
  name: "challenge",
  initialState: {
    // 초기 상태
    challengeType: 0, // 0:전체챌린지, 1: 참여중챌린지, 2: 참여이력

    recommendedChallenges: [],
    officialChallenges: [],
    userChallenges: [],

    recommChallPage: 0,
    officalChallPage: 0,
    userChallPage: 0,
  },
  reducers: {
    // 상태 변경 함수들

    // 현재 보고 있는 페이지
    setPage: (state, action) => {
      state.challengeType = action.payload;
    },

    // 메인 화면에 노출될 페이지
    addRecommChallenge: (state, action) => {
      state.recommendedChallenges.push(action.payload);
    },
    addOfficialChallenge: (state, action) => {
      state.officialChallenges.push(action.payload);
    },
    addUserChallenge: (state, action) => {
      state.userChallenges.push(action.payload);
    },

    // 챌린지 배열 초기화
    clearRecommChallenge: (state) => {
      state.recommendedChallenges = [];
    },
    clearOfficialChallenge: (state) => {
      state.officialChallenges = [];
    },
    clearUserChallenge: (state) => {
      state.userChallenges = [];
    },

    setRecommChallengePage: (state, action) => {
      state.recommChallPage = action.payload;
    },
    setOfficialChallengePage: (state, action) => {
      state.officalChallPage = action.payload;
    },
    setUserChallengePage: (state, action) => {
      state.userChallPage = action.payload;
    },
  },
});

export const {
  setPage,
  addRecommChallenge,
  addOfficialChallenge,
  addUserChallenge,
  setRecommChallengePage,
  setOfficialChallengePage,
  setUserChallengePage,
} = challengeSlice.actions;
export default challengeSlice.reducer;
