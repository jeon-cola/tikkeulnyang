import { useEffect, useState } from "react";
import Api from "@/services/Api";

export default function InviteLinkSection() {
  const [inviteLink, setInviteLink] = useState("");
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    const fetchInviteLink = async () => {
      try {
        const res = await Api.post("api/share/invite");
        setInviteLink(res.data.data); // 링크만 저장
      } catch (err) {
        console.error("초대 링크 생성 실패:", err);
      }
    };

    fetchInviteLink();
  }, []);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(inviteLink);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch (err) {
      console.error("클립보드 복사 실패:", err);
    }
  };

  return (
    <div className="flex flex-col items-center">
      <input
        type="text"
        value={inviteLink}
        readOnly
        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm text-gray-700 mb-3"
      />
      <button
        onClick={handleCopy}
        className={`px-4 py-2 bg-blue-500 text-white rounded transition-colors duration-200
      ${
        copied
          ? "bg-[#F3F4F6]! text-gray-800!" // 복사됨 상태: 연한 회색 배경 + 진한 글씨
          : " text-white hover:bg-[#ff6f6f]"
      }`}
      >
        {copied ? "복사됨!" : "링크 복사하기"}
      </button>
    </div>
  );
}
