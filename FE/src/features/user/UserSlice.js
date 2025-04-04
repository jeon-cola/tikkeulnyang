import { createSlice } from "@reduxjs/toolkit";

const initialState= {
    isAuthenticated: false,
    nickName: null,
    email:null,
    id:null,
    profileImg:null,
    deposit:null
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
        setDeposit: (state, action) => {
            state.deposit = action.payload
        },
        setUserId: (state, action)=>{
            state.id = action.payload
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
    setDeposit,
    resetUser,
    setUserId
} = userSlice.actions

export default userSlice.reducer