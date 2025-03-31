import CustomHeader from "@/components/CustomHeader";
import HomeWidget from "@/features/home/components/HomeWidget";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { useState, useRef } from "react";

export default function RenderHome() {
  const [widgets, setWidgets] = useState([
    { id: "widget-1", title: "남은예산", content: "10,000원" },
    { id: "widget-2", title: "결제예정", content: "20,000원" },
    { id: "widget-3", title: "저번달통계", content: "" },
    { id: "widget-4", title: "현재 소비 금액", content: "40,000원" },
    { id: "widget-5", title: "남은 카드 실적", content: "50,000원" },
    { id: "widget-6", title: "버킷리스트", content: "5,000원" },
  ]);

  const [isEditing, setIsEditing] = useState(false);
  // 드래그 중인 상태를 추적하기 위한 ref
  const isDraggingRef = useRef(false);

  // 클릭 이벤트 처리를 위한 공통 핸들러
  const handleClick = (id) => {
    // 위젯 편집 모드 전환
    if (!id) {
      console.log("편집 버튼");

      if (isEditing === false) {
        setIsEditing(true);
      } else {
        setIsEditing(false);
      }
      return;
    }

    // 위젯 ID에 따른 다른 동작 수행
    switch (id) {
      case "widget-1":
        console.log("남은예산 위젯 클릭됨");
        // 남은예산 관련 동작
        break;
      case "widget-2":
        console.log("결제예정 위젯 클릭됨");
        // 결제예정 관련 동작
        break;
      case "widget-3":
        console.log("저번달통계 위젯 클릭됨");
        // 저번달통계 관련 동작
        break;
      case "widget-4":
        console.log("현재 소비 금액 위젯 클릭됨");
        // 현재 소비 금액 관련 동작
        break;
      case "widget-5":
        console.log("남은 카드 실적 위젯 클릭됨");
        // 남은 카드 실적 관련 동작
        break;
      case "widget-6":
        console.log("버킷리스트 위젯 클릭됨");
        // 버킷리스트 관련 동작
        break;
      default:
        console.log("알 수 없는 위젯 클릭됨");
    }
  };

  const onDragStart = () => {
    isDraggingRef.current = true;
  };

  const onDragUpdate = (update) => {
    // 드래그 중 업데이트 처리
    if (!update.destination) return;
  };

  const onDragEnd = (result) => {
    // 드래그 상태 초기화
    isDraggingRef.current = false;

    // 드롭 위치가 없거나, 드래그가 취소된 경우
    if (!result.destination) return;

    // 위치가 변경되지 않은 경우
    if (
      result.destination.droppableId === result.source.droppableId &&
      result.destination.index === result.source.index
    )
      return;

    // 원본 배열 복사
    const newWidgets = Array.from(widgets);

    // 드래그한 아이템 가져오기
    const [movedItem] = newWidgets.splice(result.source.index, 1);

    // 목적지 인덱스에 아이템 삽입
    newWidgets.splice(result.destination.index, 0, movedItem);

    // 상태 업데이트 - 약간 지연시켜 애니메이션이 자연스럽게 완료되도록 함
    requestAnimationFrame(() => {
      setWidgets(newWidgets);
    });
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

          <div
            className="flex justify-center items-center px-[5px] py-[3px] w-[52px] h-[23px] bg-white shadow-[1px_1px_4px_rgba(0,0,0,0.1)] rounded-[30px] cursor-pointer"
            onClick={() => handleClick()} // 편집 버튼 클릭 시 ID 없이 호출
          >
            <span className="font-['Pretendard'] font-normal text-[12px] leading-[14px] tracking-[0.07em] text-[#303030]">
              {isEditing ? "완료" : "편집"}
            </span>
          </div>
        </div>

        <DragDropContext
          onDragStart={onDragStart}
          onDragUpdate={onDragUpdate}
          onDragEnd={onDragEnd}
        >
          <Droppable droppableId="widgets">
            {(provided) => (
              <div
                className="flex flex-wrap w-full"
                {...provided.droppableProps}
                ref={provided.innerRef}
              >
                {widgets.map((widget, index) => (
                  <Draggable
                    key={widget.id}
                    draggableId={widget.id}
                    index={index}
                    isDragDisabled={!isEditing}
                  >
                    {(provided, snapshot) => (
                      <div
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...(isEditing ? provided.dragHandleProps : {})}
                        className={`w-[calc(50%-8px)] m-1 `}
                        style={{
                          ...provided.draggableProps.style,
                          // 스냅샷을 이용해 드래그 중일 때의 스타일을 제어
                          transition: snapshot.isDragging
                            ? "transform 0.01s"
                            : provided.draggableProps.style?.transition,
                        }}
                        onClick={() => !isEditing && handleClick(widget.id)} // 편집 모드가 아닐 때만 클릭 이벤트 처리
                      >
                        <HomeWidget
                          title={widget.title}
                          content={widget.content}
                        />
                      </div>
                    )}
                  </Draggable>
                ))}
                {provided.placeholder}
                {/* 드래그 중 원래 위치를 유지하기 위한 공간을 확보 */}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </div>
    </>
  );
}
