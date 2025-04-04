import { useNavigate } from "react-router-dom";

export default function BudgetBar() {
  const navigate = useNavigate();
  return (
    <div className="flex justify-between items-center w-full mt-[30px] pt-[15px] gap-3">
      {/* <button
        className="blackButton flex-1! h-full! text-base!"
        onClick={() => navigate("/ledger/budget")}
      >
        예산 보기
      </button> */}
      <button
        className="blackButton flex-1! h-full! text-base!"
        onClick={() => navigate("/ledger/budget/make")}
      >
        예산 설정
      </button>
      <button
        className="blackButton flex-1! h-full! text-base!"
        onClick={() => navigate("/ledger/budget/report")}
      >
        소비 리포트
      </button>
    </div>
  );
}
