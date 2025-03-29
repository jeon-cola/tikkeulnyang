import { Outlet } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";

export default function LedgerMain() {
  return (
    <div>
      <CustomHeader title="가계부" />
      <Outlet />
    </div>
  );
}
