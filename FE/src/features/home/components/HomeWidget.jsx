import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import { useEffect } from "react";

export default function Homewidget({ title, content }) {
  const settings = {
    arrows: false,
    dots: true,
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

  useEffect(() => {
    console.log("HomeWidget", content);
  }, [content]);

  return (
    <>
      <div className="flex flex-col items-start p-[18px] gap-[4px] w-[176px] h-[177px] bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px]">
        <div className="slider-container relative w-full h-full">
          <h3 className="absolute top-0 left-0 text-left font-['Pretendard'] font-normal text-[20px] leading-[30px] text-black">
            {title}
          </h3>
          <Slider {...settings} className="h-[140px]">
            {/* 위젯 내용 */}
            {/* 타이틀 */}
            <div className="h-[140px]"></div>
            {content.map((item, index) => (
              <div
                key={index}
                className="mt-[50px] text-left font-['Pretendard'] font-normal text-[24px] leading-[36px] text-black mt-[1px]"
              >
                {item}
              </div>
            ))}
          </Slider>
        </div>
      </div>
    </>
  );
}
