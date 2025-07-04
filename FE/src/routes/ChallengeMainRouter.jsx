import { Route, Routes } from "react-router-dom";
import ChallengeMain from "@/features/challenge/ChallengeMain";
import ChallengeDetail from "@/features/challenge/ChallengeDetail";
import ChallengeEnter from "@/features/challenge/ChallengeEnter";
import ChallengeMake from "@/features/challenge/ChallengeMake";
import ChallengeTotal from "@/features/challenge/ChallengeTotal";

export default function ChallengeMainRouter() {
  return (
    <Routes>
      <Route path="/" element={<ChallengeMain />} />
      <Route path="/:id" element={<ChallengeDetail />} />
      <Route path="/enter/:id" element={<ChallengeEnter />} />
      <Route path="/make" element={<ChallengeMake />} />
      <Route path="/total/:type" element={<ChallengeTotal />} />
    </Routes>
  );
}
