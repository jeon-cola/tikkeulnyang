import { configureStore } from "@reduxjs/toolkit";
import challengeReducer from "@/features/challenge/ChallengeSlice.js";
import userReducer from "@/features/user/UserSlice.js";

export const store = configureStore({
  reducer: {
    challenge: challengeReducer,
    user: userReducer,
  },
});

export default store;
