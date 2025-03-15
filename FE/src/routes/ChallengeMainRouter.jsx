import { Route, Routes } from "react-router-dom";
import ChallengeMain from "../features/challenge/challengeMain";

export default function ChallengeMainRouter(){
    return(
        <Routes>
            <Route path="/*" element={<ChallengeMain/>}/>
        </Routes>
    )
}