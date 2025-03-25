import { createSlice } from "@reduxjs/toolkit";

const userSlice = createSlice({
    name:"user",
    initialState: {
        isAuthenticated: false,
        nickName: null,
        email:null,
    },
    reducers: {
        setAuthenticated: (state, action) => {
            state.isAuthenticated = action.payload
        },
        setNickName: (state, action) => {
            state.nickName = action.payload
        },
        setEmail : (state, action) => {
            state.email = action.payload
        }
    }
})

export const {
    setAuthenticated,
    setEmail,
    setNickName
} = userSlice.actions

export default userSlice.reducer