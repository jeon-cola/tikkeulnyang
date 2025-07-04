import { useEffect, useState } from "react";
import BasicContainer from "@/features/challenge/components/BasicContainer";
import CardBox from "@/features/challenge/components/CardBox";
import ChallengeCard from "@/features/challenge/components/ChallengeCard";
import ChallengeNav from "@/features/challenge/components/ChallengeNav";
import ViewMoreButton from "@/features/challenge/components/ViewMoreButton";
import ChallengeDesc from "@/features/challenge/components/ChallengeDesc";
import CustomBackHeader from "@/components/CustomBackHeader";
import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useSelector, useDispatch } from "react-redux";
import { addOfficialChallenge } from "@/features/challenge/ChallengeSlice";
import RenderList from "@/features/challenge/components/RenderList";
import { ChallengeUtils } from "@/features/challenge/utils/ChallengeUtils";
import CreateButton from "./components/CreateButton";
import CustomHeader from "@/components/CustomHeader";
import IsLoading from "../../components/IsLoading"

export default function ChallengeMain() {
  const [officialChallenges, setOfficialChallenges] = useState([]);
  const [userChallenges, setUserChallenges] = useState([]);
  const [recommendChallenges, setRecommendChallenges] = useState([]);
  const [isLoading, setIsLoading] = useState(false)

  // 페이지 저장할 State
  const [officialPageCnt, setOfficialPageCnt] = useState(0);
  const [userPageCnt, setUserPageCnt] = useState(0);

  const dispatch = useDispatch();
  const challengeType = useSelector((state) => state.challenge.challengeType);

  const fetchOfficialChallenge = async (page, size) => {
    const response = await ChallengeService.getOfficial(page, size);
    console.log(response.data.content);

    // 날짜 형식 변경
    const formattedChallenges = response.data.content.map((challenge) => ({
      ...challenge,
      startDate: ChallengeUtils.formatDate(challenge.startDate),
      endDate: ChallengeUtils.formatDate(challenge.endDate),
    }));

    dispatch(addOfficialChallenge(formattedChallenges));
    setOfficialChallenges([...officialChallenges, ...formattedChallenges]);
  };

  const fetchUserChallenge = async (page, size) => {
    const response = await ChallengeService.getUser(page, size);
    console.log(response.data.content);

    // 날짜 형식 변경
    const formattedChallenges = response.data.content.map((challenge) => ({
      ...challenge,
      startDate: ChallengeUtils.formatDate(challenge.startDate),
      endDate: ChallengeUtils.formatDate(challenge.endDate),
    }));

    setUserChallenges([...userChallenges, ...formattedChallenges]);
  };

  const fetchRecommendChallenge = async (page, size) => {
    const response = await ChallengeService.getRecommend(page, size);
    console.log(response.data);

    // 날짜 형식 변경
    const formattedChallenges = response.data.map((challenge) => ({
      ...challenge,
      startDate: ChallengeUtils.formatDate(challenge.startDate),
      endDate: ChallengeUtils.formatDate(challenge.endDate),
    }));

    setRecommendChallenges([...recommendChallenges, ...formattedChallenges]);
  };

  // 추천, 공식, 유저 챌린지 를 불러올떄 loading 화면 
  useEffect(()=> {
    setIsLoading(!(userChallenges.length !== 0 && recommendChallenges.length !== 0 && officialChallenges.length !== 0))
  },[userChallenges,recommendChallenges,officialChallenges])

  // 페이지가 실행되자마자 우선 추천, 공식, 유저 챌린지를 4개씩 불러온다.

  useEffect(() => {
    fetchOfficialChallenge(officialPageCnt, 4);
    console.log(officialPageCnt);
  }, [officialPageCnt]);

  useEffect(() => {
    fetchUserChallenge(userPageCnt, 4);
    console.log(userPageCnt, userChallenges);
  }, [userPageCnt]);

  useEffect(() => {
    fetchRecommendChallenge(0, 4);
  }, []);

  // 공식 챌린지 목록 렌더링
  const renderOfficialChallenge = () => {
    const cardElements = [];

    for (let i = 0; i < officialChallenges.length; i += 2) {
      cardElements.push(
        <CardBox>
          {officialChallenges.slice(i, i + 2).map((challenge) => (
            <ChallengeCard
              thumbnailUrl={challenge.thumbnailUrl}
              type="공식챌린지"
              title={challenge.challengeName}
              startDate={challenge.startDate}
              endDate={challenge.endDate}
              challengeId={challenge.challengeId}
            />
          ))}
        </CardBox>
      );
    }

    console.log("cardElements.length : " + cardElements.length);
    return cardElements;
  };

  // 유저 챌린지 목록 렌더링
  const renderUserChallenge = () => {
    const cardElements = [];

    console.log("userChallenges : ", userChallenges);
    for (let i = 0; i < userChallenges.length; i += 2) {
      cardElements.push(
        <CardBox>
          {userChallenges.slice(i, i + 2).map((challenge) => (
            <ChallengeCard
              thumbnailUrl={challenge.thumbnailUrl}
              type="유저챌린지"
              title={challenge.challengeName}
              startDate={challenge.startDate}
              endDate={challenge.endDate}
              challengeId={challenge.challengeId}
            />
          ))}
        </CardBox>
      );
    }

    return cardElements;
  };

  // 추천 챌린지 조회
  const renderRecommendChallenge = () => {
    return (
      <div className="flex pt-[5px] pl-[16px] overflow-x-auto gap-7 pb-4 w-full">
        {recommendChallenges.map((challenge) => (
          <div key={challenge.challengeId} className="flex-shrink-0 w-fill">
            <ChallengeCard
              thumbnailUrl={challenge.thumbnailUrl}
              type="추천챌린지"
              title={challenge.challengeName}
              startDate={challenge.startDate}
              endDate={challenge.endDate}
              challengeId={challenge.challengeId}
              width="150px"
              height="185px"
            />
          </div>
        ))}
      </div>
    );
  };

  // 전체 챌린지 선택시
  const renderPage = () => {
    switch (challengeType) {
      case 0:
        return (
          <>
            {/* 추천 챌린지 목록 4개씩 렌더링 */}
            <BasicContainer>
              <ChallengeDesc type="추천 챌린지" button="전체보기 >" />

              {renderRecommendChallenge()}
            </BasicContainer>

            {/* 공식 챌린지 목록 4개씩 렌더링 */}
            <BasicContainer>
              <ChallengeDesc type="공식 챌린지" button="전체보기 >" />
              {renderOfficialChallenge()}
              <ViewMoreButton
                onIncrease={() => setOfficialPageCnt(officialPageCnt + 1)}
              />
            </BasicContainer>

            {/* 유저 챌린지 목록 4개씩 렌더링 */}
            <BasicContainer>
              <ChallengeDesc type="유저 챌린지" button="전체보기 >" />
              {renderUserChallenge()}
              <ViewMoreButton
                onIncrease={() => setUserPageCnt(userPageCnt + 1)}
              />
            </BasicContainer>
          </>
        );
      case 1:
        return (
          <>
            {/* 참여중 챌린지 탭 */}
            <RenderList pageType="particiated" />
          </>
        );
      case 2:
        return (
          <>
            {/* 참여 이력 탭 */}
            <RenderList pageType="past" />
          </>
        );
    }
  };
  return (
    <>
    {isLoading?<IsLoading/>
    :
    <>
      <CustomHeader title="챌린지" />
      <CreateButton />
      {/* <CustomBackHeader title="챌린지" /> */}
      <div className="flex flex-col items-start p-[0px_0px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <ChallengeNav />
        {renderPage()}
      </div>
    </>
  }
  </>
  );
}
