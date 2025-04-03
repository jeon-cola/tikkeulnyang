import React, { useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setPage } from "@/features/challenge/ChallengeSlice";

export default function ChallengeNav() {
  const dispatch = useDispatch();
  const challengeType = useSelector((state) => state.challenge.challengeType);
  const handleClick = (type) => {
    dispatch(setPage(type));
  };

  useEffect(() => {
    //console.log("현재 선택된 타입:", challengeType);
  }, [challengeType]);
  const tabs = [
    { id: 0, title: "전체 챌린지" },
    { id: 1, title: "참여중 챌린지" },
    { id: 2, title: "참여이력" },
  ];

  // 밑줄 인디케이터 위치 이동 훅
  useEffect(() => {
    if (tabsRef.current[challengeType] && indicatorRef.current) {
      const tabElement = tabsRef.current[challengeType];
      const indicatorElement = indicatorRef.current;

      indicatorElement.style.width = `${tabElement.offsetWidth}px`;
      indicatorElement.style.left = `${tabElement.offsetLeft}px`;
    }
  }, [challengeType]);

  // const [activeTab, setActiveTab] = useState(0);
  const tabsRef = useRef([]);
  const indicatorRef = useRef(null);
  return (
    <>
      {/**
       *
       * version 1
       *
       */}

      {/* <div className="relative w-[372px] h-[42px] bg-white rounded-full flex-none order-0 self-stretch flex-grow-0">
        <div
          onClick={() => handleClick(0)}
          className="flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[43px] left-0 top-0 bg-[#FF957A] border border-[#DFDFDF] rounded-full"
        >
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-white flex-none order-0 flex-grow-0">
            전체 챌린지
          </span>
        </div>

        <div
          onClick={() => handleClick(1)}
          className="box-border flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[42px] left-[127px] top-0 bg-white rounded-full"
        >
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-[#303030] flex-none order-0 flex-grow-0">
            참여중 챌린지
          </span>
        </div>

        <div
          onClick={() => handleClick(2)}
          className="flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[42px] left-[254px] top-0 bg-white rounded-full"
        >
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-[#303030] flex-none order-0 flex-grow-0">
            참여이력
          </span>
        </div>
      </div> */}

      {/**
       *
       * version 2
       *
       */}

      <div className="w-full bg-white">
        {/* 탭 헤더 */}
        <div className="relative border-b border-gray-200">
          <div className="flex">
            {tabs.map((tab, index) => (
              <button
                key={tab.id}
                ref={(el) => (tabsRef.current[index] = el)}
                className={`w-full py-3 px-4 text-center focus:outline-none transition-colors duration-300 ${
                  challengeType === index
                    ? "text-[#FF957A] font-bold"
                    : "text-gray-500 hover:text-gray-700"
                }`}
                onClick={() => handleClick(index)}
              >
                {tab.title}
              </button>
            ))}
          </div>

          {/* 밑줄 인디케이터 */}
          <div
            ref={indicatorRef}
            className="absolute bottom-0 h-0.5 bg-[#FF957A] transition-all duration-300 ease-in-out"
          />
        </div>
      </div>
    </>
  );
}
