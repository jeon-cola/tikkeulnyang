import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import Box from "./components/box";

export default function PersonalLedger() {
  return (
    <div>
      <Container>
        <Box text="유저님의 가계부" />
        <Box text="예산 설정하러 가기" />
        <CustomCalendar />
      </Container>
    </div>
  );
}
