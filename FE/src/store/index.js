import { configureStore } from "@reduxjs/toolkit";
import challengeReducer from "@/features/challenge/ChallengeSlice.js";

export const store = configureStore({
  reducer: {
    challenge: challengeReducer,
  },
});

export default store;
