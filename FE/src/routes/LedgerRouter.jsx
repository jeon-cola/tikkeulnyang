import { Route, Routes } from "react-router-dom";
import LedgerMain from "@/features/ledger/LedgerMain";
import PersonalLedger from "@/features/ledger/PersonalLedger";
import SharedLedger from "../features/ledger/SharedLedger";
import LedgerDetail from "@/features/ledger/LedgerDetail";
import BudgetMain from "@/features/ledger/components/budget/BudgetMain";
import BudgetMake from "@/features/ledger/components/budget/BudgetMake";
import BudgetReport from "@/features/ledger/components/budget/BudgetReport";

export default function LedgerRouter() {
  return (
    <Routes>
      {/* LedgerMain은 Layout 컴포넌트 (Outlet 필요) */}
      <Route path="/*" element={<LedgerMain />}>
        <Route index element={<PersonalLedger />} />
        <Route path="share" element={<SharedLedger />} />
        <Route path="detail" element={<LedgerDetail />} />

        {/* budget 중첩 라우팅 */}
        <Route path="budget" element={<BudgetMain />} />
        <Route path="budget/make" element={<BudgetMake />} />
        <Route path="budget/report" element={<BudgetReport />} />
      </Route>
    </Routes>
  );
}
