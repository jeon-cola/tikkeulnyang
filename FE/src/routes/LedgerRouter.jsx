import { Route, Routes } from "react-router-dom";
import LedgerMain from "@/features/ledger/LedgerMain";

export default function LedgerRouter() {
  return (
    <Routes>
      <Route path="/*" element={<LedgerMain />} />
    </Routes>
  );
}
