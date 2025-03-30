import CustomHeader from "@/components/CustomHeader";
import HomeWidget from "@/features/home/components/HomeWidget";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { useState } from "react";

export default function RenderHome() {
  const [widgets, setWidgets] = useState([
    { id: "widget-1", title: "남은예산", content: "10,000원" },
    { id: "widget-2", title: "결제예정", content: "20,000원" },
    { id: "widget-3", title: "저번달통계", content: "" },
    { id: "widget-4", title: "현재 소비 금액", content: "40,000원" },
    { id: "widget-5", title: "남은 카드 실적", content: "50,000원" },
    { id: "widget-6", title: "버킷리스트", content: "5,000원" },
  ]);

  const onDragEnd = (result) => {
    // 드롭 위치가 없거나, 드래그가 취소된 경우
    if (!result.destination) return;

    // 위치가 변경되지 않은 경우
    if (
      result.destination.droppableId === result.source.droppableId &&
      result.destination.index === result.source.index
    )
      return;

    // 위젯 배열 복사
    const newWidgets = Array.from(widgets);
    // 드래그한 아이템 제거
    const [reorderedItem] = newWidgets.splice(result.source.index, 1);
    // 새 위치에 아이템 삽입
    newWidgets.splice(result.destination.index, 0, reorderedItem);

    console.log("드래그 끝");
    // 상태 업데이트
    setWidgets(newWidgets);
  };

  return (
    <>
      <CustomHeader title="홈" />
      <div className="flex flex-col items-start p-[30px_10px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <div className="flex flex-col p-[12px_11px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <p className="w-auto h-[12px] font-['Pretendard'] text-left font-semibold text-[10px] leading-[12px] tracking-[0.07em] text-[#D0D0D0]">
            이번달 카드 추천
          </p>

          <p className="w-auto h-[14px] font-['Pretendard'] text-left font-semibold text-[12px] leading-[14px] tracking-[0.07em] text-black">
            혜택이 많은 카드를 골라
          </p>

          <p className="w-auto h-[13px] font-['Pretendard'] text-left font-semibold text-[11px] leading-[13px] tracking-[0.07em] text-black">
            카드 혜택으로 절약해보아요
          </p>

          <div className="absolute flex flex-row justify-center items-center p-[3px_5px] gap-[10px] w-[37px] h-[12px] right-[16px] bottom-[5px] bg-[rgba(8,8,8,0.46)] rounded-[30px]">
            <span className="w-[25px] h-[6px] font-['Pretendard'] font-semibold text-[5px] leading-[6px] tracking-[0.07em] text-white">
              자세히 보기
            </span>
          </div>
        </div>

        <div className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <h2 className="pt-[18px] font-['Pretendard'] font-semibold text-[14px] text-left leading-[17px] tracking-[0.01em] text-[#FF957A] mb-[6px]">
            안녕하세요 유저님
          </h2>

          <p className="font-['Pretendard'] font-semibold text-[12px] text-left leading-[22px] tracking-[0.01em] text-black">
            주 5회 무지출 챌린지에 도전 중이시군요
            <br />
            곧 마감일이 다가오고 있어요
            <br />
            끝까지 힘내요
          </p>

          <div className="pt-[99px] w-full justify-center flex flex-row">
            <button className=" text-white longButton">
              챌린지 자세히보기
            </button>
          </div>
        </div>

        <div className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start mt-[20px]">
            <h2 className=" font-['Pretendard'] text-left font-semibold text-[14px] leading-[17px] tracking-[0.07em] text-black mb-[7px]">
              가계부로 이동
            </h2>

            <p className="mb-[24px] text-left font-['Pretendard'] font-semibold text-[12px] leading-[14px] tracking-[0.07em] text-black">
              오늘의 지출 확인하기
            </p>
          </div>
        </div>

        <div className="flex justify-between w-full h-auto mt-[1px]">
          <h2 className="ml-[20px] font-['Pretendard'] font-semibold text-[14px] leading-[17px] tracking-[0.01em] text-black">
            위젯으로 모아보는 티끌냥
          </h2>

          <div className="flex justify-center items-center px-[5px] py-[3px] w-[52px] h-[23px] bg-white shadow-[1px_1px_4px_rgba(0,0,0,0.1)] rounded-[30px]">
            <span className="font-['Pretendard'] font-normal text-[12px] leading-[14px] tracking-[0.07em] text-[#303030]">
              편집
            </span>
          </div>
        </div>

        <DragDropContext onDragEnd={onDragEnd}>
          <Droppable droppableId="widgets">
            {(provided) => (
              <div
                className="grid grid-cols-2 gap-x-auto justify-items-stretch w-full"
                {...provided.droppableProps} // 드롭 영역에 필수로 전달해야 하는 props 모음
                ref={provided.innerRef} // React의 DOM 참조를 라이브러리에 전달
              >
                {widgets.map((widget, index) => (
                  <Draggable
                    key={widget.id}
                    draggableId={widget.id}
                    index={index}
                  >
                    {(provided) => (
                      <div
                        ref={provided.innerRef}
                        // 드래그 가능한 요소에 필요한 속성들. 주로 위치 조정과 관련
                        {...provided.draggableProps}
                        // 드래그 핸들 영역에 적용되는 속성으로, 사용자가 어디를 잡아당겨야 드래그가 시작되는지 정의
                        {...provided.dragHandleProps}
                        className={`${
                          index % 2 === 0
                            ? "justify-self-start"
                            : "justify-self-end"
                        } ${index > 1 ? "mt-6" : ""}`}
                      >
                        <HomeWidget
                          title={widget.title}
                          content={widget.content}
                        />
                      </div>
                    )}
                  </Draggable>
                ))}
                {/* {provided.placeholder} */}{" "}
                {/* 드래그 중 원래 위치를 유지하기 위한 공간을 확보 */}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </>
  );
}
