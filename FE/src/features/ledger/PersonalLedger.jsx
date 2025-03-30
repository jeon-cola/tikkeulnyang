import { Outlet } from "react-router-dom";
import Container from "@/components/Container";
import PersonalCalendar from "./components/PersonalCalendar";
import LedgerHeader from "./components/LedgerHeader";
// import Cat from "./assets/black_cat.png";
export default function PersonalLedger() {
  return (
    <div className="w-full">
      <Outlet />
      <Container>
        <LedgerHeader />
        <PersonalCalendar />
      </Container>
    </div>
  );
}
