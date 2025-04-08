import CreateButton from "@/features/challenge/components/CreateButton";

export default function CustomHeader({ title, showCreateButton = false }) {
  return (
    <header className="w-full h-12 bg-white shadow-md rounded-t-2xl fixed top-0 left-0 right-0 flex-none order-none flex-grow-0 z-10 flex items-center">
      <p className="text-xl font-semibold ml-6">{title}</p>

      {/* 챌린지 페이지에서만 + 버튼이 보인다. */}
      {showCreateButton && <CreateButton />}
    </header>
  );
}
