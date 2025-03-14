import { Route, Routes } from "react-router-dom";
import ChallengeMain from "../features/challenge/ChallengeMain";

export default function ChallengeMainRouter(){
    return(
        <Routes>
            <Route path="/*" element={<ChallengeMain/>}/>
        </Routes>
    )
}
