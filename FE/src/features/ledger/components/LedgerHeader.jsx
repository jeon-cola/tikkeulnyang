import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import Box from "./Box";
import PurseImg from "../assets/money_purse.png";

export default function LedgerHeader() {
  const navigate = useNavigate();
  const isDetailPage = location.pathname.includes("detail");
  const userInfo = useSelector((state) => state.user);

  console.log(userInfo.nickName);
  return (
    <div className="w-full item-center flex flex-col gap-[10px]">
      {!isDetailPage && (
        <>
          <Box text={`${userInfo.nickName}님의 가계부`} variant="title">
            <button
              className="blackButton "
              onClick={() => navigate("/ledger/detail")}
            >
              세부내역
            </button>
          </Box>

          {/* 두번재 박스 : 예산생성 */}
          <Box
            text="예산을 설정하세요"
            variant="highlight"
            onClick={() => navigate("/ledger/budget")}
          >
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
          <Box text={`${userInfo.nickName}의 가계부 세부내역`} variant="title">
            <div className="flex flex-row gap-[5px]">
              <button
                className="whiteButton"
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
