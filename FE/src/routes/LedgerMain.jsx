import { Route, Routes } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";
import PersonalLedger from "./PersonalLedger";
import SharedLedger from "./SharedLedger";

export default function LedgerMain() {
  console.log("테스트입니다");

  return (
    <div>
      <CustomHeader title="가계부" />
      <Routes>
        <Route path="/" element={<PersonalLedger />} />
        <Route path="share" element={<SharedLedger />} />
      </Routes>
    </div>
  );
}
