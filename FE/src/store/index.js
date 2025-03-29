import { configureStore } from "@reduxjs/toolkit";
import challengeReducer from "@/features/challenge/ChallengeSlice.js";
import userRoducer from "@/features/user/UserSlice.js"

export const store = configureStore({
  reducer: {
    challenge: challengeReducer,
    user: userRoducer
  },
});

export default store;
