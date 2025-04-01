import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Api from "@/services/Api";

export default function AcceptInvite() {
  const { token } = useParams(); // URL에서 token 파라미터 추출
  const navigate = useNavigate();

  useEffect(() => {
    const acceptInvite = async () => {
      try {
        await Api.post(`/api/share/accept/${token}`);
        alert("초대가 수락되었습니다! 공유 가계부로 이동합니다.");
        navigate("/ledger/share"); // 공유 가계부로 이동 (경로는 프로젝트 구조에 맞게 조정)
      } catch (err) {
        console.error("초대 수락 실패:", err);
        alert(
          "초대 수락에 실패했습니다. 이미 수락했거나 잘못된 링크일 수 있어요."
        );
        navigate("/"); // 메인으로
      }
    };

    acceptInvite();
  }, [token]);

  return (
    <div className="flex justify-center items-center min-h-screen text-lg">
      초대 수락 중입니다...
    </div>
  );
}
