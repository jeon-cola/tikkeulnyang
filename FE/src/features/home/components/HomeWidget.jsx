import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

export default function Homewidget({ title, content, children }) {
  const settings = {
    arrows: false,
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
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

  return (
    <>
      <div className="flex flex-col items-start p-[18px] gap-[4px] w-[176px] h-[177px] bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px]">
        <div className="slider-container relative w-full h-full">
          <Slider {...settings} className="h-full">
            {/* 위젯 내용 */}
            {/* 타이틀 */}
            <div className="h-[140px]">
              <h3 className="text-left font-['Pretendard'] font-normal text-[20px] leading-[30px] text-black">
                {title}
              </h3>

              <p className="text-left font-['Pretendard'] font-normal text-[24px] leading-[36px] text-black mt-[1px]">
                {content}
              </p>
            </div>
            {/* {children} */}
            <div>2</div>
          </Slider>
        </div>
      </div>
    </>
  );
}
