import { useEffect, useState } from "react";
import BasicContainer from "@/features/challenge/components/BasicContainer";
import CardBox from "@/features/challenge/components/CardBox";
import ChallengeCard from "@/features/challenge/components/ChallengeCard";
import ChallengeNav from "@/features/challenge/components/ChallengeNav";
import ViewMoreButton from "@/features/challenge/components/ViewMoreButton";
import ChallengeDesc from "@/features/challenge/components/ChallengeDesc";
import ChallengeCard2 from "./components/ChallengeCard2";
import CustomHeader from "@/components/CustomHeader";
import { ChallengeService } from "@/features/challenge/servies/ChallengeService";
import { useSelector, useDispatch } from "react-redux";
import { addOfficialChallenge } from "@/features/challenge/ChallengeSlice";
import RenderHistory from "@/features/challenge/components/RenderHistory";

export default function ChallengeMain() {
  const [officialChallenges, setOfficialChallenges] = useState([]);
  const dispatch = useDispatch();
  const challengeType = useSelector((state) => state.challenge.challengeType);

  const fetchOfficialChallenge = async () => {
    const response = await ChallengeService.getOfficial(1, 4);
    console.log(response.data.content);
    dispatch(addOfficialChallenge(response.data.content));
    setOfficialChallenges(response.data.content);
  };

  // 페이지가 실행되자마자 우선 추천, 공식, 유저 챌린지를 4개씩 불러온다.
  useEffect(() => {
    fetchOfficialChallenge();
    console.log(challengeType);
  }, []);

  // 전체 챌린지 선택시
  const renderPage = () => {
    switch (challengeType) {
      case 0:
        return (
          <>
            <BasicContainer>
              <ChallengeDesc type="추천 챌린지" button="전체보기 >" />
              <CardBox>
                {officialChallenges.slice(0, 2).map((challenge) => (
                  <ChallengeCard
                    imageUrl={challenge.imageUrl}
                    type="공식챌린지"
                    title={challenge.challengeName}
                    startDate={challenge.startDate}
                    endDate={challenge.endDate}
                    challengeId={challenge.challengeId}
                  />
                ))}
              </CardBox>

              <CardBox>
                {officialChallenges.slice(2, 4).map((challenge) => (
                  <ChallengeCard
                    imageUrl={challenge.imageUrl}
                    type="공식챌린지"
                    title={challenge.challengeName}
                    startDate={challenge.startDate}
                    endDate={challenge.endDate}
                    challengeId={challenge.challengeId}
                  />
                ))}
              </CardBox>

              <ViewMoreButton />
            </BasicContainer>

            <BasicContainer>
              <ChallengeDesc type="공식 챌린지" button="전체보기 >" />
              <CardBox>
                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="공식챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="5"
                />

                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="공식챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="6"
                />
              </CardBox>

              <CardBox>
                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="공식챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="7"
                />

                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="공식챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="8"
                />
              </CardBox>
              <ViewMoreButton />
            </BasicContainer>

            <BasicContainer>
              <ChallengeDesc type="유저 챌린지" button="전체보기 >" />
              <CardBox>
                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="유저챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="5"
                />

                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="유저챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="6"
                />
              </CardBox>

              <CardBox>
                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="유저챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="7"
                />

                <ChallengeCard
                  imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
                  type="유저챌린지"
                  title="카페 방문 줄이기"
                  startDate="01-01"
                  endDate="01-31"
                  challengeId="8"
                />
              </CardBox>
              <ViewMoreButton />
            </BasicContainer>
          </>
        );

      case 2:
        return (
          <>
            <RenderHistory />
          </>
        );
    }
  };
  return (
    <>
      <CustomHeader title="챌린지" />
      <div className="flex flex-col items-start p-[30px_10px_12px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <ChallengeNav />
        {renderPage()}
      </div>
    </>
  );
}
