import CustomCalendar from "../../../components/CustomCalendar";

export default function PersonalLedger() {
  return (
    <div>
      <div className="flex flex-col items-start p-[30px_20px_12px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <CustomCalendar />
      </div>
    </div>
  );
}
