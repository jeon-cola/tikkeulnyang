import { useEffect, useState } from "react";
import Api from "@/services/Api";

export default function ProfileImageList({ onClick }) {
  const [partners, setPartners] = useState([]);

  useEffect(() => {
    const fetchPartners = async () => {
      try {
        const res = await Api.get("api/share/partners");
        console.log("👤 파트너 응답:", res.data); // ✅ 콘솔 확인용
        setPartners(res.data.data);
      } catch (err) {
        console.error("공유 사용자 불러오기 실패:", err);
      }
    };

    fetchPartners();
  }, []);

  return (
    <div className="flex gap-2 overflow-x-auto">
      {partners.map((partner) => (
        <div className="flex flex-col justify-center items-center whitespace-nowrap" key={partner.userId}>

          <img
            className="w-10 h-10 rounded-full border-2 border-gray-300 cursor-pointer hover:border-blue-500 "
            src={partner.profileImageUrl}
            alt={partner.nickname}
            title={partner.nickname}
            onClick={() => onClick(partner.userId)}
            />
            <p className="text-xs">{partner.nickname}</p>
          </div>
        
      ))}
    </div>
  );
}
