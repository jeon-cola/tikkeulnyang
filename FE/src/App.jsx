import "./App.css";
import NavBar from "./components/navBar";
import Router from "./routes";
import BasicContainer from "./features/challenge/components/BasicContainer";
import ChallengeNav from "./features/challenge/components/ChallengeNav";
import ChallengeCard from "./features/challenge/components/challengeCard";
import ViewMoreButton from "./features/challenge/components/ViewMoreButton";
function App() {
  return (
    <>
      <ChallengeNav />
      <BasicContainer>
        <div className="flex flex-row justify-between items-center p-0 gap-[30px] w-[366px] h-[207px] flex-none">
          <ChallengeCard
            imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
            type="공식챌린지"
            title="카페 방문 줄이기"
            startDate="01-01"
            endDate="01-31"
          />
          <ChallengeCard
            imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
            type="공식챌린지"
            title="카페 방문 줄이기"
            startDate="01-01"
            endDate="01-31"
          />
        </div>
        <ViewMoreButton />
        <ViewMoreButton />
      </BasicContainer>
      <Router />
    </>
  );
}

export default App;
