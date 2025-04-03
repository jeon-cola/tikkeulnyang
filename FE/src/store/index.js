import { configureStore } from "@reduxjs/toolkit";
import challengeReducer from "@/features/challenge/ChallengeSlice.js";
import userReducer from "@/features/user/UserSlice.js";
import editReducer from "@/features/ledger/EditSlice.js";

export const store = configureStore({
  reducer: {
    challenge: challengeReducer,
    user: userReducer,
    editMode: editReducer,
  },
});

export default store;
