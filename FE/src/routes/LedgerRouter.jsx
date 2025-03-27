import { Route, Routes } from "react-router-dom";
import LedgerMain from "@/features/ledger/LedgerMain";
import MyLedger from "@/features/ledger/MyLedger";
import LedgerDetail from "@/features/ledger/LedgerDetail";
import LedgerEdit from "@/features/ledger/LedgerEdit";
import BudgetMake from "@/features/ledger/budget/BudgetMake";
import BudgetDetail from "@/features/ledger/budget/BudgetDetail";
import BudgetReport from "@/features/ledger/budget/BudgetReport";

export default function LedgerRouter() {
  return (
    <Routes>
      {/* LedgerMain은 Layout용 (Outlet 포함) */}
      <Route path="/*" element={<LedgerMain />}>
        <Route index element={<MyLedger />} />
        <Route path="detail" element={<LedgerDetail />} />
        <Route path="Edit" element={<LedgerEdit />} />
        <Route path="budget" element={<BudgetDetail />} />
        <Route path="budgetmake" element={<BudgetMake />}>
          <Route path="budgetReport" element={<BudgetReport />} />
        </Route>
      </Route>
    </Routes>
  );
}
