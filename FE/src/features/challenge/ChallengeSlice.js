import { createSlice } from "@reduxjs/toolkit";

const challengeSlice = createSlice({
  name: "challenge",
  initialState: {
    // 초기 상태
    challengeType: 0, // 0:전체챌린지, 1: 참여중챌린지, 2: 참여이력

    recommendedChallenges: [],
    officialChallenges: [],
    userChallenges: [],

    recommChallPage: 1,
    officalChallPage: 1,
    userChallPage: 1,
  },
  reducers: {
    // 상태 변경 함수들

    // 현재 보고 있는 페이지
    setPage: (state, action) => {
      state.challengeType = action.payload;
    },

    // 메인 화면에 노출될 페이지
    addRecommChallenge: (state, action) => {
      state.recommendedChallenges = [
        ...state.recommendedChallenges,
        ...action.payload,
      ];
    },
    addOfficialChallenge: (state, action) => {
      state.officialChallenges = [
        ...state.officialChallenges,
        ...action.payload,
      ];
    },
    addUserChallenge: (state, action) => {
      state.userChallenges = [...state.userChallenges, ...action.payload];
    },

    // 첫 페이지 설정 (처음 로딩 시 사용)
    setInitialRecommChallenge: (state, action) => {
      state.recommendedChallenges = action.payload;
    },
    setInitialOfficialChallenge: (state, action) => {
      state.officialChallenges = action.payload;
    },
    setInitialUserChallenge: (state, action) => {
      state.userChallenges = action.payload;
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

    incrementRecommChallengePage: (state) => {
      state.recommChallPage += 1;
    },
    incrementOfficialChallengePage: (state) => {
      state.officalChallPage += 1;
    },
    incrementUserChallengePage: (state) => {
      state.userChallPage += 1;
    },
  },
});

export const {
  setPage,
  addRecommChallenge,
  addOfficialChallenge,
  addUserChallenge,
  setInitialRecommChallenge,
  setInitialOfficialChallenge,
  setInitialUserChallenge,
  clearRecommChallenge,
  clearOfficialChallenge,
  clearUserChallenge,
  setRecommChallengePage,
  setOfficialChallengePage,
  setUserChallengePage,
  incrementRecommChallengePage,
  incrementOfficialChallengePage,
  incrementUserChallengePage,
} = challengeSlice.actions;
export default challengeSlice.reducer;
