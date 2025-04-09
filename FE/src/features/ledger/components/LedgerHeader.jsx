import { useNavigate } from "react-router-dom";
import Box from "./Box";
import PurseImg from "../assets/money_purse.png";
import { useSelector } from "react-redux";

export default function LedgerHeader({
  onEditClick,
  isEditMode,
  onAdd,
  onEdit,
  onDelete,
  isCreateModeOn,
  isEditModeOn,
  isDeleteModeOn,
}) {
  const navigate = useNavigate();
  const isSharePage = location.pathname.includes("share");
  const isDetailPage = location.pathname.includes("detail");
  const userInfo = useSelector((state) => state.user);

  return (
    <div className="w-full item-center flex flex-col gap-3">
      {!isDetailPage && (
        <>
          <Box text={`${userInfo.nickName}님의 가계부`} variant="title">
            {" "}
            <div className="flex flex-row gap-2">
              <button
                className={`${
                  isSharePage ? "blackButton" : "whiteButton"
                } px-4 py-2 min-w-20 text-sm md:text-base`}
                onClick={() => navigate("/ledger/share")}
              >
                공유
              </button>
              <button
                className={`${
                  isSharePage ? "whiteButton" : "blackButton"
                } px-4 py-2 min-w-20 text-sm md:text-base`}
                onClick={() => navigate("/ledger")}
              >
                개인
              </button>
            </div>
          </Box>

          {/* 두번재 박스 : 예산생성 */}
          {!isSharePage && (
            <Box
              text="예산을 설정하세요"
              variant="highlight"
              onClick={() => navigate("/ledger/budget")}
            >
              <img
                src={PurseImg}
                alt="돈주머니 사진"
                className="w-auto max-w-[45px] h-auto"
              />
            </Box>
          )}
        </>
      )}

      {isDetailPage && (
        <>
          {/* 상단 흰색 박스 안에 타이틀 + 편집 버튼 + 편집 모드 시 하단 버튼들 포함 */}
          <div className="w-full bg-white rounded-lg shadow-md p-4 flex flex-col gap-3">
            {/* 상단 타이틀 영역 */}
            <div className="flex justify-between items-center">
              <p className="text-[18px] md:text-lg font-semibold text-gray-800">
                {userInfo.nickName}의 가계부 세부내역
              </p>
              <button
                className={`${
                  isEditMode ? "blackButton" : "whiteButton"
                } px-4 py-2 text-sm md:text-base`}
                onClick={onEditClick}
              >
                {isEditMode ? "완료" : "편집"}
              </button>
            </div>

            {/* ✨ 편집 모드일 때만 보이는 버튼 그룹 */}
            {isEditMode && (
              <div className="pt-1">
                <div className="grid grid-cols-3 gap-2">
                  <button
                    className={`py-3 rounded transition-colors text-sm md:text-base ${
                      isCreateModeOn ? "tikkeulButton" : "greyButton"
                    }`}
                    onClick={onAdd}
                  >
                    {isCreateModeOn ? "추가 완료" : "내역 추가"}
                  </button>
                  <button
                    className={`py-3 rounded transition-colors text-sm md:text-base ${
                      isEditModeOn ? "tikkeulButton" : "greyButton"
                    }`}
                    onClick={onEdit}
                  >
                    {isEditModeOn ? "수정 완료" : "내역 수정"}
                  </button>
                  <button
                    className={`py-3 rounded transition-colors text-sm md:text-base ${
                      isDeleteModeOn ? "tikkeulButton" : "greyButton"
                    }`}
                    onClick={onDelete}
                  >
                    {isDeleteModeOn ? "삭제 완료" : "내역 삭제"}
                  </button>
                </div>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
