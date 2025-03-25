import { createSlice } from "@reduxjs/toolkit";

const initialState= {
    isAuthenticated: false,
    nickName: null,
    email:null,
    profileImg:null
}

const userSlice = createSlice({
    name:"user",
    initialState,
    reducers: {
        setAuthenticated: (state, action) => {
            state.isAuthenticated = action.payload
        },
        setNickName: (state, action) => {
            state.nickName = action.payload
        },
        setEmail : (state, action) => {
            state.email = action.payload
        },
        setProfileImg : (state, action) => {
            state.profileImg = action.payload
        },
        resetUser : (status) => {
            return initialState
        }
    }
})

export const {
    setAuthenticated,
    setEmail,
    setNickName,
    setProfileImg,
    resetUser
} = userSlice.actions

export default userSlice.reducer