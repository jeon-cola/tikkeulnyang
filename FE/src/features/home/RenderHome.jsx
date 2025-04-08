import CustomHeader from "@/components/CustomHeader";
import HomeWidget from "@/features/home/components/HomeWidget";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import { useState, useRef, useEffect } from "react";
import { HomeService } from "@/features/home/services/HomeService";
import { useNavigate } from "react-router-dom";
import CustomBackHeader from "@/components/CustomBackHeader";
import CalendarWidget from "@/features/home/assets/calendar_widget.png";
import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import ChallengeWidget from "@/features/home/components/ChallengeWidget";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import AlertModal from "@/components/AlertModal";

export default function RenderHome() {
  const navigate = useNavigate();
  const [widgets, setWidgets] = useState([
    { id: "widget-1", title: "남은예산", content: ["0원"] },
    { id: "widget-2", title: "구독 결제 예정", content: [] },
    { id: "widget-3", title: "지난달 통계", content: [] },
    { id: "widget-4", title: "현재 소비 금액", content: ["0원"] },
    { id: "widget-5", title: "남은 카드 실적", content: ["50,000원"] },
    { id: "widget-6", title: "버킷리스트", content: [] },
  ]);

  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isModal, setIsModal] = useState(false);
  const [challengeParticipated, setChallengeParticipated] = useState([]);
  const [completedChallenges, setCompletedChallenges] = useState([]);

  const sliderSettings = {
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

  useEffect(() => {
    const initialize = async () => {
      try {
        await fetchMain();
        await fetchChallenges();
        // setIsModal(true);
        //await challengeSettle();
        await getSettlementAlert();
      } catch (error) {
        console.error(error);
      }
    };

    initialize();
  }, []);

  // 현재 유저가 참여하고 있는 챌린지 조회
  const fetchChallenges = async () => {
    const response = await ChallengeService.getChallengeParticipated();
    console.log("참여중인 챌린지:", response.data);
    setChallengeParticipated(response.data);
    return response.data; // 챌린지 데이터 반환
  };

  // 챌린지 성공시 분배
  // async function challengeSettle() {
  //   const challenges = await ChallengeService.getPast(); // 로그인한 사용자가 참여한 챌린지 이력 조회

  //   console.log(challenges.data);
  //   if (challenges.data.length > 0) {
  //     console.log("챌린지 성공시 분배 challengeSettle 실행");
  //     for (const challenge of challenges.data) {
  //       console.log("challengeId:", challenge.challengeId);
  //       const response = await HomeService.postChallengeSettle(
  //         challenge.challengeId
  //       );

  //       console.log(response);
  //       if (response.status === 200) {
  //         setIsModal(true);
  //       }
  //     }
  //   }
  // }

  // 성공한 챌린지가 있는지 알람
  const getSettlementAlert = async () => {
    const challenges = await HomeService.getSettlementAlert();
    console.log("종료한 챌린지가 있는지 알람", challenges);

    if (challenges.data.length > 0) {
      setCompletedChallenges(challenges.data);
      setIsModal(true);
      for (const challenge of challenges.data) {
        console.log(challenge.challengeName);
      }
    }
  };

  // 모달창 닫은 후 챌린지 페이지로 이동
  function isCloseModal() {
    setIsModal(false);
    HomeService.patchSettlementAlert();
    navigate(`/challenge`);
  }

  // 드래그 중인 상태를 추적하기 위한 ref
  const isDraggingRef = useRef(false);

  // 클릭 이벤트 처리를 위한 공통 핸들러
  const handleClick = (id) => {
    // 위젯 편집 모드 전환
    if (!id) {
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
        // 예산 페이지로 이동
        navigate(`/ledger`);
        break;
      case "widget-2":
        // 구독 페이지로 이동
        navigate(`/home/subscribe`);
        break;
      case "widget-3":
        console.log("통계 페이지 미완");

        break;
      case "widget-4":
        console.log("현재 소비 금액 위젯 클릭됨");

        break;
      case "widget-5":
        navigate(`/card`);

        break;
      case "widget-6":
        navigate(`/bucketlist/list`);

        break;
    }
  };

  // 위젯 드래그 앤 드롭 위한 함수들
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

  // 메인 화면에 뿌릴 데이터 조회(처음 시작하자마자 실행)
  const fetchMain = async () => {
    try {
      const response = await HomeService.getMain();
      //console.log(response);

      if (response && response.data && response.data.data) {
        const data = response.data.data;

        // 받아온 response를 위젯에 넣어준다.
        setWidgets((prevWidgets) => {
          return prevWidgets.map((widget) => {
            switch (widget.id) {
              case "widget-1": // 남은예산
                return {
                  ...widget,
                  content: [`${data.remaining_budget.amount}원`],
                };
              case "widget-2": // 결제예정
                return {
                  ...widget,
                  content: data.upcoming_subscriptions.map(
                    (item) => `${item.subscribeName}: ${item.subscribePrice}원`
                  ),
                };
              case "widget-4": // 현재 소비 금액
                return {
                  ...widget,
                  content: [`${data.current_consumption_amount}원`],
                };
              case "widget-6": // 버킷리스트
                return {
                  ...widget,
                  content: data.buckets.map(
                    (item) => `${item.title}: ${item.target_amount}원`
                  ),
                };
              default:
                return widget;
            }
          });
        });

        setIsLoading(false);
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <>
      {isLoading ? (
        <></>
      ) : (
        <>
          {/* <CustomBackHeader title="홈" /> */}
          <CustomHeader title="홈" />
          <div className="flex flex-col items-start p-[30px_10px_82px] gap-6 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
            {/* 참여중인 챌린지 위젯 */}
            <Slider {...sliderSettings} className="h-fill w-full">
              {challengeParticipated.length > 0 &&
                challengeParticipated.map((challenge) => (
                  <ChallengeWidget
                    key={challenge.id}
                    challengeId={challenge.challengeId}
                    thumbnailUrl={challenge.thumbnailUrl}
                    challengeName={challenge.challengeName}
                    endDate={challenge.endDate}
                  />
                ))}
            </Slider>

            <div className="flex flex-col pt-5 pb-9 p-[12px_11px_15px] gap-2 relative w-full h-auto bg-white rounded-[6px]">
              <div className="text-left font-semibold text-sm leading-[12px] tracking-[0.07em] text-[#D0D0D0]">
                이번달 카드 추천
              </div>

              <div className="text-left pt-3 font-semibold text-lg leading-[13px] tracking-[0.07em] text-black">
                카드 혜택으로 절약하기
              </div>

              {/* <div className="absolute flex flex-row justify-center items-center pt-2 pb-2 pl-2 pr-2 gap-[10px] right-4 bottom-2 bg-[rgba(8,8,8,0.46)] rounded-[30px]">
                <span
                  className="font-semibold text-xs leading-[6px] tracking-[0.07em] text-white"
                  onClick={() => navigate(`/card`)}
                >
                  자세히 보기
                </span>
              </div> */}
            </div>

            <div
              className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]"
              onClick={() => navigate(`/ledger`)}
            >
              <div className="flex flex-col items-start mt-[20px]">
                <h2 className=" text-left font-semibold text-xl leading-[17px] tracking-[0.01em] text-black mb-[7px]">
                  가계부로 이동
                </h2>

                <p className="mt-[10px] mb-[24px] text-left font-normal text-lg leading-[14px] tracking-3 text-black">
                  오늘의 지출 확인하기
                </p>

                <img
                  src={CalendarWidget}
                  alt={CalendarWidget}
                  className="absolute right-5 bottom-6 w-17 object-contain ml-17 mt-17"
                />
              </div>
            </div>

            <div className="flex justify-between w-full h-auto mt-[1px]">
              <h2 className="ml-[5px] font-semibold text-xl text-left leading-[17px] tracking-[0.01em] text-black">위젯으로 모아보기</h2>

              <div
                className="flex justify-center items-center px-[5px] py-[3px] bg-white shadow-[1px_1px_4px_rgba(0,0,0,0.1)] rounded-[30px] cursor-pointer"
                onClick={() => handleClick()} // 편집 버튼 클릭 시 ID 없이 호출
              >
                <div className="text-sm pl-2 pr-2 leading-[14px] tracking-[0.07em] text-[#303030]">
                  {isEditing ? "완료" : "편집"}
                </div>
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
                            className={`w-[calc(50%-8px)] m-1 flex items-center`}
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
          {/* 챌린지 종료 알람 */}
          <AlertModal
            title="챌린지 종료"
            isClose={isCloseModal}
            isOpen={isModal}
            height={170}
          >
            <div className="flex flex-col items-center gap-4">
              {completedChallenges.map((challenge, index) => (
                <div>
                  <div key={index} className="text-center">
                    <span className="text-xl">{challenge.challengeName}</span>
                    <span className="font-semibold text-blue-900 ml-2 text-xl">
                      {challenge.participationStatus}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </AlertModal>
          ;
        </>
      )}
    </>
  );
}
