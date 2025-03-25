import { useNavigate, useLocation } from "react-router-dom";
import Box from "./Box";
import PurseImg from "../assets/MoneyPurse.png";

export default function LedgerHeader() {
  const navigate = useNavigate();
  const location = useLocation();

  // 공유페이지 여부 확인
  const isSharePage = location.pathname.includes("share");
  const isDetailPage = location.pathname.includes("detail");
  return (
    <div className="w-full item-center flex flex-col gap-[10px]">
      {/* 첫번째 박스 : 공유가계부 or 개인가계부 표시*/}
      {!isDetailPage && (
        <>
          <Box
            text={isSharePage ? "유저님의 공유 가계부" : "유저님의 가계부"}
            variant="title"
          >
            <div className="flex flex-row gap-[5px]">
              <button
                className={isSharePage ? "blackButton" : "whiteButton"}
                onClick={() => navigate("/ledger/share")}
              >
                공유
              </button>
              <button
                className={isSharePage ? "whiteButton" : "blackButton"}
                onClick={() => navigate("/ledger")}
              >
                개인
              </button>
            </div>
          </Box>

          {/* 두번재 박스 : 예산생성 */}
          <Box text="예산을 설정하세요" variant="highlight">
            <img
              src={PurseImg}
              alt="돈주머니 사진"
              className="w-auto max-w-[50px] h-auto"
            />
          </Box>
        </>
      )}

      {isDetailPage && (
        <>
          <Box text="유저님의 가계부 세부내역" variant="title">
            <div className="flex flex-row gap-[5px]">
              <button
                className={isSharePage ? "blackButton" : "whiteButton"}
                onClick={() => navigate("/ledger/share")}
              >
                편집
              </button>
            </div>
          </Box>
        </>
      )}
    </div>
  );
}
