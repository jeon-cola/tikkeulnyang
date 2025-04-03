import { createSlice } from "@reduxjs/toolkit";
import { UNSAFE_SingleFetchRedirectSymbol } from "react-router-dom";

const editSlice = createSlice({
  name: "editMode",
  initialState: {
    isEditMode: false,
  },
  reducers: {
    toggleEditMode: (state) => {
      state.isEditMode = !state.isEditMode;
    },
    setEditMode: (state, action) => {
      state.isEditMode = action.payloadW;
    },
  },
});

export const { toggleEditMode, setEditMode } = editSlice.actions;
export default editSlice.reducer;
