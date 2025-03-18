import { Route, Routes } from "react-router-dom";
import ChallengeMain from "../features/challenge/ChallengeMain";
import ChallengeDetail from "@/features/challenge/ChallengeDetail";

export default function ChallengeMainRouter() {
  return (
    <Routes>
      <Route path="/" element={<ChallengeMain />} />
      <Route path="/:id" element={<ChallengeDetail />} />
    </Routes>
  );
}
