import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { useEffect, useState } from "react";
// import ScheduleWidget from "../assets/schedule_widget.png";
// import BudgetWidget from "../assets/budget_widget.png";
// import CreditCycleWidget from "../assets/credit_cycle_widget.png";
// import CreditWidget from "../assets/credit_widget.png";
// import GraphWidget from "../assets/graph_widget.png";
// import BucketWidget from "../assets/bucket_widget.png";

import ScheduleWidget from "../assets/widget/schedule_ver2.png";
import BudgetWidget from "../assets/widget/money_bag.png";
import CreditCycleWidget from "../assets/widget/pig.png";
import CreditWidget from "../assets/widget/credit_ver2.png";
import GraphWidget from "../assets/widget/graph_ver2.png";
import BucketWidget from "../assets/widget/bucket_ver2.png";

export default function Homewidget({ title, content }) {
  const settings = {
    arrows: false,
    dots: false,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: Math.floor(Math.random() * (10000 - 2000 + 1)) + 5000, // 5초~10초 사이에 자동 재생
    appendDots: (dots) => (
      <div
        style={{
          position: "absolute",
          bottom: "5px",
          width: "100%",
        }}
      >
        <ul style={{ margin: "0" }}> {dots} </ul>
      </div>
    ),
    dotsClass: "slick-dots custom-dots",
  };

  const [icon, setIcon] = useState(null);
  const [message, setMessage] = useState(null)

  useEffect(() => {
    console.log("HomeWidget", content);
  }, [content]);

  useEffect(() => {
    if (title === "남은예산") {
      setIcon(BudgetWidget);
      setMessage("현명한 지출 관리!");
    } else if (title === "구독 결제 예정") {
      setIcon(ScheduleWidget);
      setMessage("다가오는 결제 확인");
    } else if (title === "지난달 통계") {
      setIcon(GraphWidget);
      setMessage("소비 패턴 분석");
    } else if (title === "현재 소비 금액") {
      setIcon(CreditCycleWidget);
      setMessage("소비 현황 한눈에");
    } else if (title === "남은 카드 실적") {
      setIcon(CreditWidget);
      setMessage("실적 달성 확인");
    } else if (title === "버킷리스트") {
      setIcon(BucketWidget);
      setMessage("목표를 향한 발걸음");
    }
  }, []);

  return (
    <>
      <div
        className={`flex flex-col p-[18px] w-full h-full shadow-md rounded-lg bg-white`}
      >
        <div className="slider-container relative w-full h-full">
          <div>
            <h3 className="absolute top-0 left-0 right-0 text-left font-normal text-[20px] leading-[30px] text-black">
              {title}
            </h3>
            <p className="absolute top-7 left-0 text-left text-[#A7A7A7]">{message}</p>
            <img
              src={icon}
              alt={icon}
              className="w-15.5 object-contain ml-17 mt-17"
            />
          </div>
          {/* <Slider {...settings} className="h-[140px]"> */}
          {/* 위젯 내용 */}
          {/* 타이틀 */}
          {/* <div className="h-[140px]"></div> */}
          {/* {content.map((item, index) => (
              <div key={index} className=" leading-[36px] text-black">
                {item}
              </div>
            ))}
          </Slider> */}
        </div>
      </div>
    </>
  );
}
