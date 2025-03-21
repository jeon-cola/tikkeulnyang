import { useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
export default function Account() {
    const [account, setAccount] = useState({
        bank:"",
        name:"",
        accountNum:""
    });

    function inputHandler(e) {
        const {name,value} = e.target;
        console.log(name,value)
        setAccount({
            ...account,
            [name]:value
        });
    };

    return(
        <div className="flex flex-col justify-center gap-5 min-w-[345px]">
            <CustomBackHeader title="등록 계좌 설정"/>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">은행 선택</p>
                <select id="" className="w-full" name="bank" value={account.bank} onChange={inputHandler}>
                    <option value="" disabled>은행을 선택해 주세요</option>
                    <option value="국민">국민 은행</option>
                </select>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">계좌 번호</p>
                <input type="text" placeholder="계좌 번호를 입력해 주세요" className="w-full" name="accountNum" value={account.accountNum} onChange={inputHandler}/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">예금주</p>
                <input type="text" placeholder="예금주 성함을 입력해 주세요" className="w-full" name="name" value={account.name} onChange={inputHandler}/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0,05)] rounded-[6px] p-4">
                <img src={bankImage} alt="은행 이미지" />
            </div>
            <div className="w-full flex flex-col items-center">
                <button>저장하기</button>
            </div>
        </div>
    )
}