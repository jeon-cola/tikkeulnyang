import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import Box from "./components/Box";

import purseImg from "./assets/purse.png"

export default function PersonalLedger() {
  return (
    <div className="w-full">
      <Container>
        <Box text="유저님의 가계부" variant="title" />
        <Box text="예산 설정하러 가기" variant="highlight">
          <img src={purseImg} alt="돈주머니 사진" className="w-auto max-w-[50px] h-auto"/>
        </Box>
        <CustomCalendar />
      </Container>
    </div>
  );
}
